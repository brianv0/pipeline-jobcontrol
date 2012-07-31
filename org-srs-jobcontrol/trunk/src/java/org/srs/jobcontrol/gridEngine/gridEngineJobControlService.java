/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.srs.jobcontrol.gridEngine;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.srs.jobcontrol.Job;
import org.srs.jobcontrol.JobControl;
import org.srs.jobcontrol.JobControlException;
import org.srs.jobcontrol.JobStatus;
import org.srs.jobcontrol.JobSubmissionException;
import org.srs.jobcontrol.NoSuchJobException;
import org.srs.jobcontrol.OutputProcessor;
import org.srs.jobcontrol.common.JobControlService;
import org.srs.jobcontrol.common.JobControlService.DeleteFile;

/**
 *
 * @author zimmer
 */

class gridEngineJobControlService extends JobControlService {
    private final static String GROUP = System.getProperty("org.srs.jobcontrol.ge.group","P_glast"); // the default group for the submit command
    private String default_submit = "/opt/sge/bin/lx24-amd64/qsub -P "+GROUP;
    private String SUBMIT_COMMAND = System.getProperty("org.srs.jobcontrol.ge.submitCommand",default_submit);
    private final static String KILL_COMMAND = System.getProperty("org.srs.jobcontrol.ge.killCommand","/opt/sge/bin/lx24-amd64/qdel");
    private final static Pattern pattern = Pattern.compile("Your job (\\w+)*");
    private final gridEngineStatus geStatus = new gridEngineStatus();    
    
    private gridEngineJobControlService() {
    }
    public static void main(String[] args) throws RemoteException, JMException {
        gridEngineJobControlService service = new gridEngineJobControlService();
        JobControl stub = (JobControl) UnicastRemoteObject.exportObject(service, 1097);
        
        String user = System.getProperty("user.name");
        // Bind the remote object's stub in the registry
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("gridEngineJobControlService-"+user, stub);
        service.logger.info("Server ready, user "+user);
        
        // Register the JMX bean
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("org.srs.jobcontrol:type=JobControlService");
        mbs.registerMBean(service, name);
    }
    
    public String submit(Job job) throws JobSubmissionException, JobControlException {
        try {
	
	    logger.info("BEGIN gridEngine JobControlService submit");
            String ip = RemoteServer.getClientHost();
            logger.info("submitting: "+job.getCommand()+" from "+ip);
            checkPermission(ip);
            
            String jobName = submitInternal(job);
            logger.fine("job "+jobName+" submitted");
	    nSubmitted.incrementAndGet();
            lastSuccessfulJobSubmissionTime = System.currentTimeMillis();
	    logger.info("END gridEngine JobControlService submit");
            

            return jobName;
        } catch (ServerNotActiveException t) {
            logger.log(Level.SEVERE,"Unexpected error",t);
            this.lastFailedJobSubmissionTime = System.currentTimeMillis();
            throw new JobControlException("Unexpected error",t);
        } catch (JobSubmissionException t) {
            logger.log(Level.SEVERE,"job submission failed",t);
            this.lastFailedJobSubmissionTime = System.currentTimeMillis();
            throw t;
        } catch (JobControlException t) {
            logger.log(Level.SEVERE,"job submission failed",t);
            this.lastFailedJobSubmissionTime = System.currentTimeMillis();
            throw t;
        }
    }
    private String submitInternal(Job job) throws JobSubmissionException, JobControlException {
        //logger.info("BEGIN submitInternal");
        String command = job.getCommand();
	logger.info("command:" + command);
	
        if (command == null || command.length() == 0) throw new JobSubmissionException("Missing command");
        List<String> qsub = new ArrayList<String>(Arrays.<String>asList(SUBMIT_COMMAND.split("\\s+")));
        String logFileName = job.getLogFile()==null ? "logFile.log" : sanitize(job.getLogFile());
        // eventhough we may not use lyons resources, we still use the protocol, so enable it...
        qsub.add("-l");
        qsub.add("xrootd=1"); // activate use of xrootd
        qsub.add("-l");
        qsub.add("sps=1"); // activate use of sps;
        // SZ 2011-07-24: remove email notification - is handled explicitly in the wrapper script
        qsub.add("-m");
        qsub.add("n"); // send *NO* email at all!
        qsub.add("-o");
        qsub.add(job.getWorkingDirectory()+"/"+logFileName);// SZ 2011-07-18, GE needs path for logFile
        qsub.add("-j");
        qsub.add("y");// Send stderr to the stdout (the log file)
        if (job.getMaxCPU() != 0) 
        { 
           qsub.add("-l");
           qsub.add("ct="+convertToNormalisedSec(job.getMaxCPU()));
        }
        if (job.getMaxMemory() != 0) 
        { 
           qsub.add("-l");
           qsub.add("vmem="+job.getMaxMemory()+"M");
        }
        // Ignore the jobname option for gridEngine, we need gridEngine to assign a unique name for later query
        //if (job.getName() != null) { qsub.append(" -N ").append(sanitize(job.getName())).append(" ");  }

	qsub.add("-V");
        /**interjob dependencies not managed by gridEngine in this simple manner:
         * if (!job.getRunAfter().isEmpty()) {
            qsub.append(" -w ");
            for (Iterator iter = job.getRunAfter().iterator(); iter.hasNext() ; ) {
                qsub.append("ended(").append(iter.next()).append(')');
                if (iter.hasNext()) qsub.append("&&");
            }
        }*/
        
        if (job.getExtraOptions() != null)
        { 
            qsub.addAll(tokenizeExtraOption(job.getExtraOptions())); 
        }
        // gridEngine only accepts a script as a argument, not a command
        // Also gridEngine does not automatically copy the current working directory to the job
        String geCommand = "ge_script";
        if (job.getWorkingDirectory() != null)
        {
           geCommand = job.getWorkingDirectory()+"/"+geCommand;
        }
        qsub.add(geCommand);
        
        StringBuilder ge_script = new StringBuilder();
        if (job.getWorkingDirectory() != null)
        {
           ge_script.append("cd ").append(job.getWorkingDirectory()).append('\n');
        }
        ge_script.append(command);
        if (job.getArguments() != null) {
            for (String arg : job.getArguments()) { ge_script.append(" \""+arg+"\""); }
        }
        ge_script.append('\n');
        job.getFiles().put("ge_script",ge_script.toString());
        String fullCommand = toFullCommand(qsub);
        logger.info("Submit: "+fullCommand);
             
        // Things to be undone if the submit fails.
        List<Runnable> undoList = new ArrayList<Runnable>();
        try {

            ProcessBuilder builder = new ProcessBuilder(qsub);
            Map<String,String> env = builder.environment();
            env.put("JOBCONTROL_SUBMIT_COMMAND",fullCommand);
            
            if (job.getEnv() != null) {
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
            
            List<String> result = output.getResult();
            int rc = process.exitValue();
            logger.info("process.exitValue=" + rc);
            if (rc != 0) {
               StringBuilder message = new StringBuilder("Process failed rc="+rc);
               if (!result.isEmpty()) message.append(" output was:");
               for (String line : result)
               {
                  message.append('\n').append(line);
               }
               throw new JobControlException(message.toString());
            }
            
            if (output.getStatus() != null) throw output.getStatus();

            if (result.size() == 0) throw new JobControlException("Unexpected output length "+result.size());
            for (String line : result) {
	    
	        logger.info("line:" + line);
                Matcher matcher = pattern.matcher(line);
		  
                boolean ok = matcher.find();

                //if (ok) return Integer.parseInt(matcher.group(1));
		if (ok)  {
                   undoList.clear();
                   return matcher.group(1);
                }
            }
            throw new JobControlException("Could not find job number in output");
        } catch (IOException x) {
            throw new JobControlException("IOException during job submission",x);
        } catch (InterruptedException x) {
            throw new JobControlException("Job submission interrupted",x);
        }
        finally {
            Collections.reverse(undoList);
            for (Runnable undo : undoList) undo.run();
        }
    }

    private int convertToNormalisedSec(int seconds) {
        double f = 1.0;
        return (int)f*seconds;
    }
    private void checkPermission(String ip) throws SecurityException {
        //if (!ip.startsWith("134.79") && !ip.startsWith("198.129")) throw new SecurityException();
        if (!ip.startsWith("134.158") && !ip.startsWith("134.79")) throw new SecurityException();
	else
	  System.out.println("Permission OK");
    }
    public JobStatus status(String jobID) throws NoSuchJobException, JobControlException {
        try {
            System.out.println("in gridEngineJobControlService.status");
            String ip = RemoteServer.getClientHost();
            //logger.info("status: "+jobID+" from "+ip);
            System.out.println("status: "+jobID+" from "+ip);
            checkPermission(ip);
            
            Map<String,JobStatus> statii = geStatus.getStatus();
            JobStatus result = statii.get(jobID);
            if (result == null) throw new NoSuchJobException("Job id "+jobID);
            return result;
        } catch (ServerNotActiveException t) {
            logger.log(Level.SEVERE,"Unexpected error",t);
            throw new JobControlException("Unexpected error",t);
        } catch (NoSuchJobException t) {
            logger.log(Level.SEVERE,"job status failed",t);
            throw t;
        } catch (JobControlException t) {
            logger.log(Level.SEVERE,"job status failed",t);
            throw t;
        }
    }
    
    public void cancel(String jobID) throws NoSuchJobException, JobControlException {
        try {
            String ip = RemoteServer.getClientHost();
            logger.info("killing: "+jobID+" from "+ip);
            checkPermission(ip);
            
            cancelInternal(jobID);
            logger.fine("job "+jobID+" cancelled");
        } catch (ServerNotActiveException t) {
            logger.log(Level.SEVERE,"Unexpected error",t);
            throw new JobControlException("Unexpected error",t);
        } catch (NoSuchJobException t) {
            logger.log(Level.SEVERE,"job cancellation failed",t);
            throw t;
        } catch (JobControlException t) {
            logger.log(Level.SEVERE,"job cancellation failed",t);
            throw t;
        }
    }
    private void cancelInternal(String jobID) throws NoSuchJobException, JobControlException {
        try {
            List<String> commands = new ArrayList<String>();
            commands.add(KILL_COMMAND);
            commands.add(String.valueOf(jobID));
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
        } catch (IOException x) {
            throw new JobControlException("IOException while killing job "+jobID,x);
        } catch (InterruptedException x) {
            throw new JobControlException("InterruptedException while killing job "+jobID,x);
        }
    }
   
   public String getStatus()
   {
      try
      {
         geStatus.getStatus();
         return "OK";
      }
      catch (JobControlException x)
      {
         logger.log(Level.SEVERE,"Error getting status",x);
         return "Bad "+(x.getMessage());
      }
   }
   
   public Map<String, Integer> getJobCounts()
   {
      try
      {
         return computeJobCounts(geStatus.getStatus());
      }
      catch (JobControlException x)
      {
         logger.log(Level.SEVERE,"Error getting job counts",x);
         return null;
      }
   }
}
