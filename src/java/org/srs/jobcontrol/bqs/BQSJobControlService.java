package org.srs.jobcontrol.bqs;

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
import org.srs.jobcontrol.common.CLIJobControlService;
import org.srs.jobcontrol.common.JobControlService.DeleteFile;

/**
 * The main class for the job control server.
 * @author Tony Johnson
 */
class BQSJobControlService extends CLIJobControlService {
    private final static String SUBMIT_COMMAND = System.getProperty("org.srs.jobcontrol.bqs.submitCommand","/usr/local/bin/qsub -l platform=LINUX");
    private final static String KILL_COMMAND = System.getProperty("org.srs.jobcontrol.bqs.killCommand","/usr/local/bin/qdel");
    private final static Pattern pattern = Pattern.compile("job (\\w+) submitted.*");
    private final BQSStatus bqsStatus = new BQSStatus();    
    
    private BQSJobControlService() {
    }
    public static void main(String[] args) throws RemoteException, JMException {
        BQSJobControlService service = new BQSJobControlService();
        JobControl stub = (JobControl) UnicastRemoteObject.exportObject(service, 1098);
        
        String user = System.getProperty("user.name");
        // Bind the remote object's stub in the registry
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("BQSJobControlService-"+user, stub);
        service.logger.info("Server ready, user "+user);
        
        // Register the JMX bean
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("org.srs.jobcontrol:type=JobControlService");
        mbs.registerMBean(service, name);
    }
    
    public String submit(Job job) throws JobSubmissionException, JobControlException {
        try {
	
	    logger.info("BEGIN BQS JobControlService submit");
            String ip = RemoteServer.getClientHost();
            logger.info("submitting: "+job.getCommand()+" from "+ip);
            checkPermission(ip);
            
            String jobName = submitInternal(job);
            logger.fine("job "+jobName+" submitted");
	    nSubmitted.incrementAndGet();
            lastSuccessfulJobSubmissionTime = System.currentTimeMillis();
	    logger.info("END BQS JobControlService submit");
            

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
        qsub.add("-o"); 
        qsub.add(logFileName);
        qsub.add("-eo"); // Send stderr to the stdout (the log file)
        if (job.getMaxCPU() != 0) 
        { 
           qsub.add("-l");
           qsub.add("T="+convertToNormalisedSec(job.getMaxCPU()));
        }
        if (job.getMaxMemory() != 0) 
        { 
           qsub.add("-l");
           qsub.add("M="+job.getMaxMemory()+"MB");
        }
        // Ignore the jobname option for BQS, we need BQS to assign a unique name for later query
        //if (job.getName() != null) { qsub.append(" -N ").append(sanitize(job.getName())).append(" ");  }

	qsub.add("-V");
        /**interjob dependencies not managed by BQS in this simple manner:
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
        // BQS only accepts a script as a argument, not a command
        // Also BQS does not automatically copy the current working directory to the job
        String bqsCommand = "bqs_script";
        if (job.getWorkingDirectory() != null)
        {
           bqsCommand = job.getWorkingDirectory()+"/"+bqsCommand;
        }
        qsub.add(bqsCommand);
        
        StringBuilder bqs_script = new StringBuilder();
        if (job.getWorkingDirectory() != null)
        {
           bqs_script.append("cd ").append(job.getWorkingDirectory()).append('\n');
        }
        bqs_script.append(command);
        
        bqs_script.append('\n');
        job.getFiles().put("bqs_script",bqs_script.toString());
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
            OutputProcessor output = new OutputProcessor(process.getInputStream(), logger);
            process.waitFor();
            output.join();            
            int rc = process.exitValue();
            String jobId = processSubmittedJobOutput( output, rc );
            undoList.clear();
            return jobId;
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

    @Override
    public Map<String, JobStatus> getCurrentStatus() throws JobControlException{
        return bqsStatus.getStatus();
    }

    @Override
    public String extractJobId(List<String> result) throws JobSubmissionException{
        for(String line: result){
            logger.info( "line:" + line );
            Matcher matcher = pattern.matcher( line );
            boolean ok = matcher.find();
            if(ok){
                return matcher.group( 1 );
            }
        }
        throw new JobSubmissionException("Unable to get job ID in output");
    }
    
}
