package org.glast.jobcontrol;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Tony Johnson
 */
class JobControlService implements JobControl
{
   private final static String SUBMIT_COMMAND = "/usr/local/bin/bsub";
   private final static Pattern pattern = Pattern.compile("Job <(\\d+)>");
   private Logger logger = Logger.getLogger("org.freehep.jobcontrol");
   private LSFStatus lsfStatus = new LSFStatus();
   
   private JobControlService()
   {
   }
   public static void main(String[] args) throws RemoteException
   {
      
      JobControlService service = new JobControlService();
      JobControl stub = (JobControl) UnicastRemoteObject.exportObject(service, 0);
      
      // Bind the remote object's stub in the registry
      Registry registry = LocateRegistry.getRegistry();
      registry.rebind("JobControl", stub);
      service.logger.info("Server ready");
   }
   
   public int submit(Job job) throws JobSubmissionException
   {
      try
      {
         String ip = RemoteServer.getClientHost();
         logger.info("submitting: "+job.getCommand()+" from "+ip);
         checkPermission(ip);
         
         int id = submitInternal(job);
         logger.fine("job "+id+" submitted");
         return id;
      }
      catch (ServerNotActiveException t)
      {
         logger.log(Level.SEVERE,"Unexpected error",t);
         throw new JobSubmissionException("Unexpected error",t);
      }
      catch (JobSubmissionException t)
      {
         logger.log(Level.SEVERE,"job submission failed",t);
         throw t;
      }
   }
   private int submitInternal(Job job) throws JobSubmissionException
   {
      String command = job.getCommand();
      if (command == null || command.length() == 0) throw new JobSubmissionException("Missing command");
      StringBuilder bsub = new StringBuilder(SUBMIT_COMMAND);
      if (job.getLogFile() == null) { bsub.append(" -o %J.log"); }
      else { bsub.append(" -o ").append(job.getLogFile()); }
      if (job.getMaxCPU() != 0) { bsub.append(" -c ").append(convertToMinutes(job.getMaxCPU())); }
      if (job.getMaxMemory() != 0) { bsub.append(" -M ").append(job.getMaxMemory()); }
      if (!job.getRunAfter().isEmpty())
      {
         bsub.append(" -w ");
         for (Iterator iter = job.getRunAfter().iterator(); iter.hasNext() ; )
         {
            bsub.append("ended(").append(iter.next()).append(')');
            if (iter.hasNext()) bsub.append("&&");
         }
      }
      if (job.getExtraOptions() != null) { bsub.append(' ').append(job.getExtraOptions()); }
      bsub.append(' ').append(command);
      
      try
      {
         List<String> commands = new ArrayList<String>(Arrays.asList(bsub.toString().split("\\s+")));
         if (job.getArguments() != null)
         {
            for (String arg : job.getArguments()) { commands.add("\""+arg+"\""); }
         }
         ProcessBuilder builder = new ProcessBuilder(commands);
         if (job.getEnv() != null)
         {
            Map<String,String> env = builder.environment();
            env.putAll(job.getEnv());
         }
         if (job.getWorkingDirectory() != null)
         {
            File dir = new File(job.getWorkingDirectory());
            if (!dir.exists())
            {
               boolean rc = dir.mkdirs();
               if (!rc) throw new IOException("Could not create working directory "+dir);
            }
            else if (!dir.isDirectory()) throw new IOException("Working directory is not a directory "+dir);
            builder.directory(dir);
            
            // Create any files send with the job
            
            Map<String,String> files = job.getFiles();
            if (files != null)
            {
               for (Map.Entry<String,String> entry : files.entrySet())
               {
                  File file = new File(dir,entry.getKey());
                  if (file.exists()) throw new SecurityException("File "+file+" already exists");
                  PrintWriter writer = new PrintWriter(new FileWriter(file));
                  writer.print(entry.getValue());
                  writer.close();
               }
            }
         }
         builder.redirectErrorStream(true);
         Process process = builder.start();
         OutputProcessor output = new OutputProcessor(process.getInputStream(),logger);
         process.waitFor();
         output.join();
         int rc = process.exitValue();
         if (rc != 0) throw new JobSubmissionException("Process failed rc="+rc);
         
         if (output.getStatus() != null) throw output.getStatus();
         
         List<String> result = output.getResult();
         if (result.size() == 0) throw new JobSubmissionException("Unexpected output length "+result.size());
         for (String line : result)
         {
            Matcher matcher = pattern.matcher(line);
            boolean ok = matcher.find();
            if (ok) return Integer.parseInt(matcher.group(1));
         }
         throw new JobSubmissionException("Could not find job number in output");
      }
      catch (IOException x)
      {
         throw new JobSubmissionException("IOException during job submission",x);
      }
      catch (InterruptedException x)
      {
         throw new JobSubmissionException("Job submission interrupted",x);
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
   public JobStatus status(int jobID) throws NoSuchJobException
   {
      String ip;
      try
      {
         ip = RemoteServer.getClientHost();
         checkPermission(ip);
         
         Map<Integer,JobStatus> statii = lsfStatus.getStatus();
         JobStatus result = statii.get(jobID);
         if (result == null) throw new NoSuchJobException("Job id "+jobID);
         return result;
      }
      catch (ServerNotActiveException t)
      {
         logger.log(Level.SEVERE,"Unexpected error",t);
         throw new NoSuchJobException("Unexpected error",t);
      }
      catch (NoSuchJobException t)
      {
         logger.log(Level.SEVERE,"job status failed",t);
         throw t;
      }
      
   }
}
