package org.glast.jobcontrol.lsf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.glast.jobcontrol.Job;
import org.glast.jobcontrol.JobControl;
import org.glast.jobcontrol.JobControlException;
import org.glast.jobcontrol.JobStatus;
import org.glast.jobcontrol.JobSubmissionException;
import org.glast.jobcontrol.NoSuchJobException;
import org.glast.jobcontrol.OutputProcessor;
import org.glast.jobcontrol.common.JobControlService;

/**
 * The main class for the LSF job control server.
 * @author Tony Johnson
 */
class LSFJobControlService extends JobControlService
{
   private final static String SUBMIT_COMMAND = "/usr/local/bin/bsub";
   private final static String KILL_COMMAND = "/usr/local/bin/bkill";
   private final static Pattern pattern = Pattern.compile("Job <(\\d+)>");
   private final LSFStatus lsfStatus = new LSFStatus();
   
   private LSFJobControlService()
   {
   }
   public static void main(String[] args) throws RemoteException
   {
      LSFJobControlService service = new LSFJobControlService();
      JobControl stub = (JobControl) UnicastRemoteObject.exportObject(service, 0);
      
      String user = System.getProperty("user.name");
      // Bind the remote object's stub in the registry
      Registry registry = LocateRegistry.getRegistry();
      registry.rebind("JobControlService-"+user, stub);
      service.logger.info("Server ready, user "+user);
   }
   
   public String submit(Job job) throws JobSubmissionException, JobControlException
   {
      try
      {
         String ip = RemoteServer.getClientHost();
         logger.info("submitting: "+job.getCommand()+" from "+ip);
         checkPermission(ip);
         
         String id = submitInternal(job);
         logger.fine("job "+id+" submitted");
         return id;
      }
      catch (ServerNotActiveException t)
      {
         logger.log(Level.SEVERE,"Unexpected error",t);
         throw new JobControlException("Unexpected error",t);
      }
      catch (JobSubmissionException t)
      {
         logger.log(Level.SEVERE,"job submission failed",t);
         throw t;
      }
      catch (JobControlException t)
      {
         logger.log(Level.SEVERE,"job submission failed",t);
         throw t;
      }
   }
   private String submitInternal(Job job) throws JobSubmissionException, JobControlException
   {
      String command = job.getCommand();
      if (command == null || command.length() == 0) throw new JobSubmissionException("Missing command");
      List<String> bsub = new ArrayList<String>(Arrays.<String>asList(SUBMIT_COMMAND.split("\\s+")));
      String logFileName = job.getLogFile()==null ? "logFile.log" : sanitize(job.getLogFile());
      bsub.add("-o");
      bsub.add(logFileName);
      if (job.getMaxCPU() != 0)
      {
         bsub.add("-c");
         bsub.add(String.valueOf(convertToMinutes(job.getMaxCPU())));
      }
      if (job.getMaxMemory() != 0)
      {
         bsub.add("-M");
         bsub.add(String.valueOf(job.getMaxMemory()));
      }
      if (job.getName() != null)
      {
         bsub.add("-J");
         bsub.add(sanitize(job.getName()));
      }
      if (!job.getRunAfter().isEmpty())
      {
         bsub.add("-w");
         StringBuilder condition = new StringBuilder();
         for (Iterator iter = job.getRunAfter().iterator(); iter.hasNext() ; )
         {
            condition.append("ended(").append(iter.next()).append(')');
            if (iter.hasNext()) condition.append("&&");
         }
         bsub.add(condition.toString());
      }
      if (job.getExtraOptions() != null)
      {
         bsub.addAll(tokenizeExtraOption(job.getExtraOptions()));
      }
      bsub.addAll(Arrays.<String>asList(command.split("\\s+")));
      if (job.getArguments() != null)
      {
         bsub.addAll(job.getArguments());
      }
      String fullCommand = toFullCommand(bsub);
      logger.info("Submit: "+fullCommand);
      
      // Things to be undone if the submit fails.
      List<Runnable> undoList = new ArrayList<Runnable>();
      try
      {
         ProcessBuilder builder = new ProcessBuilder(bsub);
         Map<String,String> env = builder.environment();
         
         env.put("JOBCONTROL_SUBMIT_COMMAND",fullCommand);
         
         if (job.getEnv() != null)
         {
            env.putAll(job.getEnv());
         }
         if (job.getWorkingDirectory() != null)
         {
            File dir = new File(job.getWorkingDirectory());
            for (int retry : retryDelays)
            {
               if (!dir.exists())
               {
                  // This occasionally fails due to NFS/automount problems, so retry a few times
                  boolean rc = dir.mkdirs();
                  if (!rc)
                  {
                     if (retry > 0)
                     {
                        Thread.sleep(retry);
                        continue;
                     }
                     else throw new JobSubmissionException("Could not create working directory "+dir);
                  }
                  else
                  {
                     undoList.add(new DeleteFile(dir));
                  }
               }
               else if (!dir.isDirectory()) throw new JobSubmissionException("Working directory is not a directory "+dir);
               else if (job.getArchiveOldWorkingDir() != null)
               {
                  archiveOldWorkingDir(dir, job.getArchiveOldWorkingDir(),undoList);
               }
               break;
            }
            
            builder.directory(dir);
            env.put("JOBCONTROL_LOGFILE",new File(dir,logFileName).getAbsolutePath());
            storeFiles(dir, job.getFiles(), undoList);
         }
         builder.redirectErrorStream(true);
         Process process = builder.start();
         OutputProcessor output = new OutputProcessor(process.getInputStream(),logger);
         process.waitFor();
         output.join();
         int rc = process.exitValue();
         if (rc != 0) throw new JobControlException("Process failed rc="+rc);
         
         if (output.getStatus() != null) throw output.getStatus();
         
         List<String> result = output.getResult();
         if (result.size() == 0) throw new JobControlException("Unexpected output length "+result.size());
         for (String line : result)
         {
            Matcher matcher = pattern.matcher(line);
            boolean ok = matcher.find();
            if (ok) 
            {
               undoList.clear();
               return matcher.group(1);
            }
         }
         throw new JobControlException("Could not find job number in output");
      }
      catch (IOException x)
      {
         throw new JobControlException("IOException during job submission",x);
      }
      catch (InterruptedException x)
      {
         throw new JobControlException("Job submission interrupted",x);
      }
      finally
      {
         Collections.reverse(undoList);
         for (Runnable undo : undoList) undo.run();
      }
   }
   private int convertToMinutes(int seconds)
   {
      return (seconds+59)/60;
   }
   private void checkPermission(String ip) throws SecurityException
   {
      if (!ip.startsWith("134.79") && !ip.startsWith("198.129")) throw new SecurityException();
   }
   public JobStatus status(String jobID) throws NoSuchJobException, JobControlException
   {
      try
      {
         String ip = RemoteServer.getClientHost();
         logger.info("status: "+jobID+" from "+ip);
         checkPermission(ip);
         
         Map<String,JobStatus> statii = lsfStatus.getStatus();
         JobStatus result = statii.get(jobID);
         if (result == null) throw new NoSuchJobException("Job id "+jobID);
         return result;
      }
      catch (ServerNotActiveException t)
      {
         logger.log(Level.SEVERE,"Unexpected error",t);
         throw new JobControlException("Unexpected error",t);
      }
      catch (NoSuchJobException t)
      {
         logger.log(Level.SEVERE,"job status failed",t);
         throw t;
      }
      catch (JobControlException t)
      {
         logger.log(Level.SEVERE,"job status failed",t);
         throw t;
      }
   }
   
   public void cancel(String jobID) throws NoSuchJobException, JobControlException
   {
      try
      {
         String ip = RemoteServer.getClientHost();
         logger.info("killing: "+jobID+" from "+ip);
         checkPermission(ip);
         
         cancelInternal(jobID);
         logger.fine("job "+jobID+" cancelled");
      }
      catch (ServerNotActiveException t)
      {
         logger.log(Level.SEVERE,"Unexpected error",t);
         throw new JobControlException("Unexpected error",t);
      }
      catch (NoSuchJobException t)
      {
         logger.log(Level.SEVERE,"job cancellation failed",t);
         throw t;
      }
      catch (JobControlException t)
      {
         logger.log(Level.SEVERE,"job cancellation failed",t);
         throw t;
      }
   }
   private void cancelInternal(String jobID) throws NoSuchJobException, JobControlException
   {
      try
      {
         List<String> commands = new ArrayList<String>();
         commands.add(KILL_COMMAND);
         commands.add(jobID);
         ProcessBuilder builder = new ProcessBuilder();
         builder.redirectErrorStream(true);
         builder.command(commands);
         Process process = builder.start();
         OutputProcessor output = new OutputProcessor(process.getInputStream(),logger);
         process.waitFor();
         output.join();
         int rc = process.exitValue();
         if (rc == 255) throw new NoSuchJobException("No such job, id="+jobID);
         else if (rc != 0) throw new JobControlException("Command failed rc="+rc);
      }
      catch (IOException x)
      {
         throw new JobControlException("IOException while killing job "+jobID,x);
      }
      catch (InterruptedException x)
      {
         throw new JobControlException("InterruptedException while killing job "+jobID,x);
      }
   }
   
}
