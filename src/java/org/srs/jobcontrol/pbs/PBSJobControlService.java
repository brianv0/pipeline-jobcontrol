/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.srs.jobcontrol.pbs;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import java.util.HashMap;
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
import org.srs.jobcontrol.pbs.PBSJobControlService;
import org.srs.jobcontrol.pbs.PBSStatus;

/**
 *
 * @author zimmer
 */
public class PBSJobControlService extends JobControlService{
    private final static String GROUP = System.getProperty("org.srs.jobcontrol.ge.group","P_glast"); // the default group for the submit command
    private String default_submit = "qsub PBS.submit";
    private String SUBMIT_COMMAND = System.getProperty("org.srs.jobcontrol.pbs.submitCommand",default_submit);
    private final static String KILL_COMMAND = System.getProperty("org.srs.jobcontrol.pbs.killCommand","qdel");
    private final static Pattern pattern = Pattern.compile("Your job (\\w+)*"); // might need adjustment.
    private final PBSStatus pbsStatus = new PBSStatus();    
    
    private PBSJobControlService() {
    }
    public static void main(String[] args) throws RemoteException, JMException {
        PBSJobControlService service = new PBSJobControlService();
        JobControl stub = (JobControl) UnicastRemoteObject.exportObject(service, 1097);
        
        String user = System.getProperty("user.name");
        // Bind the remote object's stub in the registry
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("PBSJobControlService-"+user, stub);
        service.logger.info("Server ready, user "+user);
        
        // Register the JMX bean
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("org.srs.jobcontrol:type=JobControlService");
        mbs.registerMBean(service, name);
    }
    
    public String submit(Job job) throws JobSubmissionException, JobControlException {
        try {
	
	    logger.info("BEGIN PBS JobControlService submit");
            String ip = RemoteServer.getClientHost();
            logger.info("submitting: "+job.getCommand()+" from "+ip);
            checkPermission(ip);
            
            String jobName = submitInternal(job);
            logger.fine("job "+jobName+" submitted");
	    nSubmitted.incrementAndGet();
            lastSuccessfulJobSubmissionTime = System.currentTimeMillis();
	    logger.info("END PBS JobControlService submit");
            

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
        String command = job.getCommand();
        if (command == null || command.length() == 0) {
            throw new JobSubmissionException("Missing command");
        }
        List<String> qsub = new ArrayList<String>(Arrays.<String>asList(SUBMIT_COMMAND.split("\\s+")));
        Map<String, String> submitFile = new HashMap<String, String>();
        String logFileName = job.getLogFile() == null ? "logFile.log" : sanitize(job.getLogFile());
        
        submitFile.put("#PBS -l mem=",Integer.toString(job.getMaxMemory()));
        submitFile.put("#PBS -l walltime=",Integer.toString(job.getMaxCPU()));
        submitFile.put("#PBS -o ",logFileName);
        submitFile.put("#PBS -j ","o"); // merge StdErr/StdOut
        submitFile.put("#PBS -r","n"); // do not re-run
        submitFile.put("#PBS -V",""); // export all env vars
        //TODO: what about access to xrootd/afs?
        StringBuilder PBS_script = new StringBuilder();
        if (job.getWorkingDirectory() != null)
        {
           PBS_script.append("cd ").append(job.getWorkingDirectory()).append('\n');
        }
        PBS_script.append(command);
        if (job.getArguments() != null) {
            for (String arg : job.getArguments()) { PBS_script.append(" \""+arg+"\""); }
        }
        PBS_script.append('\n');
        submitFile.put("",PBS_script.toString());
                
        Map<String, String> env = new HashMap<String, String>();
        if (job.getEnv() != null) {
            env.putAll(job.getEnv());
        }
//        StringBuilder requirements = new StringBuilder();

//        if (job.getMaxCPU() != 0) {
//            bsub.add("-c");
//            bsub.add(String.valueOf(convertToMinutes(job.getMaxCPU())));
//        }
//        if (job.getMaxMemory() != 0) {
//            if (requirements.length() > 0) {
//                requirements.append(" && ");
//            }
//            requirements.append("Memory>").append(job.getMaxMemory());
//        }
//        if (job.getName() != null) {
//            bsub.add("-J");
//            bsub.add(sanitize(job.getName()));
//        }
//        if (!job.getRunAfter().isEmpty()) {
//            bsub.add("-w");
//            StringBuilder condition = new StringBuilder();
//            for (Iterator iter = job.getRunAfter().iterator(); iter.hasNext();) {
//                condition.append("ended(").append(iter.next()).append(')');
//                if (iter.hasNext()) {
//                    condition.append("&&");
//                }
//            }
//            bsub.add(condition.toString());
//        }
//        if (job.getExtraOptions() != null) {
//            bsub.addAll(tokenizeExtraOption(job.getExtraOptions()));
//        }
//        submitFile.put("requirements", requirements.toString());
        String fullCommand = toFullCommand(qsub);
        logger.info("Submit: " + fullCommand);
        env.put("JOBCONTROL_SUBMIT_COMMAND", fullCommand);

        // Things to be undone if the submit fails.
        List<Runnable> undoList = new ArrayList<Runnable>();
        try {
            ProcessBuilder builder = new ProcessBuilder(qsub);

            //FIXME: This does not work if working directory is null
            if (job.getWorkingDirectory() != null) {
                File dir = new File(job.getWorkingDirectory());
                for (int retry : retryDelays) {
                    if (!dir.exists()) {
                        // This occasionally fails due to NFS/automount problems, so retry a few times
                        boolean rc = dir.mkdirs();
                        if (!rc) {
                            if (retry > 0) {
                                Thread.sleep(retry);
                                continue;
                            } else {
                                throw new JobSubmissionException("Could not create working directory " + dir);
                            }
                        } else {
                            undoList.add(new DeleteFile(dir));
                        }
                    } else if (!dir.isDirectory()) {
                        throw new JobSubmissionException("Working directory is not a directory " + dir);
                    } else if (job.getArchiveOldWorkingDir() != null) {
                        archiveOldWorkingDir(dir, job.getArchiveOldWorkingDir(), undoList);
                    }
                    break;
                }
                env.put("JOBCONTROL_LOGFILE", new File(dir, logFileName).getAbsolutePath());
                builder.directory(dir);
                storeFiles(dir, job.getFiles(), undoList);
                // Add the environment to the submit file
//                StringBuilder envValue = new StringBuilder("\"");
//                for (Map.Entry<String, String> entry : env.entrySet()) {
//                    envValue.append(entry.getKey()).append("='").append(entry.getValue()).append("'").append(' ');
//                }
//                envValue.append("\"");
//                //submitFile.put("environment", envValue.toString());
                // Write the submit file
                {
                    File file = new File(dir, "PBS.submit");
                    PrintWriter writer = new PrintWriter(new FileWriter(file));
                    undoList.add(new DeleteFile(file));
                    writer.println("#!/bin/bash");
                    for (Map.Entry<String, String> fileLine : submitFile.entrySet()) {
                        writer.print(fileLine.getKey());
                        writer.print("=");
                        writer.println(fileLine.getValue());
                    }
                    writer.close();
                    file.setExecutable(true);
                }
                
            }
            builder.redirectErrorStream(true);
            Process process = builder.start();
            OutputProcessor output = new OutputProcessor(process.getInputStream(), logger);
            process.waitFor();
            output.join();
            List<String> result = output.getResult();
            int rc = process.exitValue();
            if (rc != 0) {
                StringBuilder message = new StringBuilder("Process failed rc=" + rc);
                if (!result.isEmpty()) {
                    message.append(" output was:");
                }
                for (String line : result) {
                    message.append('\n').append(line);
                }
                throw new JobControlException(message.toString());
            }

            if (output.getStatus() != null) {
                throw output.getStatus();
            }

            if (result.size() == 0) {
                throw new JobControlException("Unexpected output length " + result.size());
            }
            for (String line : result) {
                Matcher matcher = pattern.matcher(line);
                boolean ok = matcher.find();
                if (ok) {
                    undoList.clear();
                    return matcher.group(1);
                }
            }
            throw new JobControlException("Could not find job number in output");
        } catch (IOException x) {
            throw new JobControlException("IOException during job submission", x);
        } catch (InterruptedException x) {
            throw new JobControlException("Job submission interrupted", x);
        } finally {
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
        if (!ip.startsWith("134.158") && !ip.startsWith("134.79")) throw new SecurityException();
	else
	  System.out.println("Permission OK");
    }
    public JobStatus status(String jobID) throws NoSuchJobException, JobControlException {
        try {
            System.out.println("in PBSJobControlService.status");
            String ip = RemoteServer.getClientHost();
            //logger.info("status: "+jobID+" from "+ip);
            System.out.println("status: "+jobID+" from "+ip);
            checkPermission(ip);
            
            Map<String,JobStatus> statii = pbsStatus.getStatus();
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
         pbsStatus.getStatus();
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
         return computeJobCounts(pbsStatus.getStatus());
      }
      catch (JobControlException x)
      {
         logger.log(Level.SEVERE,"Error getting job counts",x);
         return null;
      }
   }
}
