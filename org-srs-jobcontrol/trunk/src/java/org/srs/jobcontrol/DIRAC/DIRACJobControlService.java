/*  
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.srs.jobcontrol.DIRAC;
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
import org.json.JSONObject;
import org.srs.jobcontrol.Job;
import org.srs.jobcontrol.JobControl;
import org.srs.jobcontrol.JobControlException;
import org.srs.jobcontrol.JobStatus;
import org.srs.jobcontrol.JobSubmissionException;
import org.srs.jobcontrol.NoSuchJobException;
import org.srs.jobcontrol.OutputProcessor;
import org.srs.jobcontrol.common.JobControlService;
/**
 *
 * @author zimmer
 * TODO: handle creation of archive folders
 */
public class DIRACJobControlService extends JobControlService{
    private final static String SUBMIT_COMMAND = System.getProperty("org.srs.jobcontrol.DIRACsubmitCommand","dirac-submit");
    private final static String KILL_COMMAND = System.getProperty("org.srs.jobcontrol.DIRACkillCommand","dirac-wms-job-kill");
    private final static Pattern pattern = Pattern.compile("Your job (\\w+)*");
    private final DIRACStatus DIRACStatus = new DIRACStatus();    
    
    private DIRACJobControlService() {
    }
    public static void main(String[] args) throws RemoteException, JMException {
        DIRACJobControlService service = new DIRACJobControlService();
        JobControl stub = (JobControl) UnicastRemoteObject.exportObject(service, 1096);
        
        String user = System.getProperty("user.name");
        // Bind the remote object's stub in the registry
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("DIRACJobControlService-"+user, stub);
        DIRACJobControlService.logger.info("Server ready, user "+user);
        
        // Register the JMX bean
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("org.srs.jobcontrol:type=JobControlService");
        mbs.registerMBean(service, name);
    }
    
    public String submit(Job job) throws JobSubmissionException, JobControlException {
        try {
	
	    logger.info("BEGIN DIRAC JobControlService submit");
            String ip = RemoteServer.getClientHost();
            logger.log(Level.INFO, "submitting: {0} from {1}", new Object[]{job.getCommand(), ip});
            checkPermission(ip);
            
            String jobName = submitInternal(job);
            logger.log(Level.FINE, "job {0} submitted", jobName);
	    nSubmitted.incrementAndGet();
            lastSuccessfulJobSubmissionTime = System.currentTimeMillis();
	    logger.info("END DIRAC JobControlService submit");
            

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
        //TODO: implement env as dict object in file (serialized)
        //logger.info("BEGIN submitInternal");
        String command = job.getCommand();
	logger.log(Level.INFO, "command:{0}", command);
	
        if (command == null || command.length() == 0) {
            throw new JobSubmissionException("Missing command");
        }
        List<String> qsub = new ArrayList<String>(Arrays.<String>asList(SUBMIT_COMMAND.split("\\s+")));
        
        qsub.add(command);
        qsub.add("-p");
        if (job.getMaxCPU() != 0) 
        { 
           qsub.add("cpu="+convertToNormalisedSec(job.getMaxCPU()));
        }
        // the later created json file containing the pipeline env vars is referenced here
        qsub.add("-p");
        qsub.add("env=pipeline_environment.json"); 
        
        // everything else is defined in extra options
        if (job.getExtraOptions() != null)
        { 
            qsub.addAll(tokenizeExtraOption(job.getExtraOptions())); 
        }
        // DIRAC only accepts a script as a argument, not a command
        // Also DIRAC does not automatically copy the current working directory to the job
        
        String fullCommand = toFullCommand(qsub);
        logger.log(Level.INFO, "Submit: {0}", fullCommand);
             
        // Things to be undone if the submit fails.
        List<Runnable> undoList = new ArrayList<Runnable>();
        try {

            ProcessBuilder builder = new ProcessBuilder(qsub);
            Map<String,String> env = builder.environment();
            env.put("JOBCONTROL_SUBMIT_COMMAND",fullCommand);
           
            if (job.getEnv() != null) {
                env.putAll(job.getEnv());
            }
            JSONObject env_pipeline_json = new JSONObject(env);
            job.getFiles().put("pipeline_environment.json", env_pipeline_json.toString()); // that should create this file in the working directory?
            
            builder.redirectErrorStream(true);
            Process process = builder.start();
            OutputProcessor output = new OutputProcessor(process.getInputStream(),logger);

            process.waitFor();
            output.join();
            
            List<String> result = output.getResult();
            int rc = process.exitValue();
            logger.log(Level.INFO, "process.exitValue={0}", rc);
            if (rc != 0) {
               StringBuilder message = new StringBuilder("Process failed rc="+rc);
               if (!result.isEmpty()) {
                    message.append(" output was:");
                }
               for (String line : result)
               {
                  message.append('\n').append(line);
               }
               throw new JobControlException(message.toString());
            }
            
            if (output.getStatus() != null) {
                throw output.getStatus();
            }

            if (result.isEmpty()) {
                throw new JobControlException("Unexpected output length "+result.size());
            }
            for (String line : result) {
	    
	        logger.log(Level.INFO, "line:{0}", line);
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
            for (Runnable undo : undoList) {
                undo.run();
            }
        }
    }

    private int convertToNormalisedSec(int seconds) {
        double f = 1.0;
        return (int)f*seconds;
    }
    private void checkPermission(String ip) throws SecurityException {
        //if (!ip.startsWith("134.79") && !ip.startsWith("198.129")) throw new SecurityException();
        if (!ip.startsWith("134.158") && !ip.startsWith("134.79")) {
            throw new SecurityException();
        }
	else {
            System.out.println("Permission OK");
        }
    }
    public JobStatus status(String jobID) throws NoSuchJobException, JobControlException {
        try {
            System.out.println("in DIRACJobControlService.status");
            String ip = RemoteServer.getClientHost();
            //logger.info("status: "+jobID+" from "+ip);
            System.out.println("status: "+jobID+" from "+ip);
            checkPermission(ip);
            
            Map<String,JobStatus> statii;
            statii = DIRACStatus.getStatus();
            JobStatus result = statii.get(jobID);
            if (result == null) {
                throw new NoSuchJobException("Job id "+jobID);
            }
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
            logger.log(Level.INFO, "killing: {0} from {1}", new Object[]{jobID, ip});
            checkPermission(ip);
            
            cancelInternal(jobID);
            logger.log(Level.FINE, "job {0} cancelled", jobID);
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
            if (rc == 255) {
                throw new NoSuchJobException("No such job, id="+jobID);
            }
            else if (rc != 0) {
                throw new JobControlException("Command failed rc="+rc);
            }
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
         DIRACStatus.getStatus();
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
         return computeJobCounts(DIRACStatus.getStatus());
      }
      catch (JobControlException x)
      {
         logger.log(Level.SEVERE,"Error getting job counts",x);
         return null;
      }
   }
    
}
//