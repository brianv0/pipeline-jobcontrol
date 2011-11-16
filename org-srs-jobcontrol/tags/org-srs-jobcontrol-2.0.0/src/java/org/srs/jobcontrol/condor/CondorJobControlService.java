package org.srs.jobcontrol.condor;

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

/**
 * The main class for the LSF job control server.
 * @author Tony Johnson
 */
class CondorJobControlService extends JobControlService {

    private final static String SUBMIT_COMMAND = "/usr/local/bin/condor_submit condor.submit";
    private final static String KILL_COMMAND = "/usr/local/bin/condor_rm";
    private final static Pattern pattern = Pattern.compile("submitted to cluster (\\d+)\\.");
    private CondorStatus status = new CondorStatus();

    private CondorJobControlService() {
    }

    public static void main(String[] args) throws RemoteException, JMException {
        CondorJobControlService service = new CondorJobControlService();
        JobControl stub = (JobControl) UnicastRemoteObject.exportObject(service, 1098);

        String user = System.getProperty("user.name");
        // Bind the remote object's stub in the registry
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("JobControlService-" + user, stub);
        CondorJobControlService.logger.info("Server ready, user " + user);

        // Register the JMX bean
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("org.srs.jobcontrol:type=JobControlService");
        mbs.registerMBean(service, name);
    }

    @Override
    public String submit(Job job) throws JobSubmissionException, JobControlException {
        try {
            String ip = RemoteServer.getClientHost();
            logger.info("submitting: " + job.getCommand() + " from " + ip);
            checkPermission(ip);

            String id = submitInternal(job);
            logger.fine("job " + id + " submitted");
            nSubmitted.incrementAndGet();
            lastSuccessfulJobSubmissionTime = System.currentTimeMillis();
            return id;
        } catch (ServerNotActiveException t) {
            logger.log(Level.SEVERE, "Unexpected error", t);
            this.lastFailedJobSubmissionTime = System.currentTimeMillis();
            throw new JobControlException("Unexpected error", t);
        } catch (JobSubmissionException t) {
            logger.log(Level.SEVERE, "job submission failed", t);
            this.lastFailedJobSubmissionTime = System.currentTimeMillis();
            throw t;
        } catch (JobControlException t) {
            logger.log(Level.SEVERE, "job submission failed", t);
            this.lastFailedJobSubmissionTime = System.currentTimeMillis();
            throw t;
        }
    }

    private String submitInternal(Job job) throws JobSubmissionException, JobControlException {
        String command = job.getCommand();
        if (command == null || command.length() == 0) {
            throw new JobSubmissionException("Missing command");
        }
        List<String> bsub = new ArrayList<String>(Arrays.<String>asList(SUBMIT_COMMAND.split("\\s+")));
        Map<String, String> submitFile = new HashMap<String, String>();
        String logFileName = job.getLogFile() == null ? "logFile.log" : sanitize(job.getLogFile());
        submitFile.put("Output", logFileName);
        submitFile.put("Error", "logFile.err");
        submitFile.put("Log", "condor.log");
        submitFile.put("Universe", "Vanilla");
        submitFile.put("should_transfer_files", "IF_NEEDED");
        submitFile.put("when_to_transfer_output", "ON_EXIT");
        submitFile.put("executable", "condor.commands");


        Map<String, String> env = new HashMap<String, String>();
        if (job.getEnv() != null) {
            env.putAll(job.getEnv());
        }
        StringBuilder requirements = new StringBuilder();

//        if (job.getMaxCPU() != 0) {
//            bsub.add("-c");
//            bsub.add(String.valueOf(convertToMinutes(job.getMaxCPU())));
//        }
        if (job.getMaxMemory() != 0) {
            if (requirements.length() > 0) {
                requirements.append(" && ");
            }
            requirements.append("Memory>").append(job.getMaxMemory());
        }
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
        submitFile.put("requirements", requirements.toString());
        String fullCommand = toFullCommand(bsub);
        logger.info("Submit: " + fullCommand);
        env.put("JOBCONTROL_SUBMIT_COMMAND", fullCommand);

        // Things to be undone if the submit fails.
        List<Runnable> undoList = new ArrayList<Runnable>();
        try {
            ProcessBuilder builder = new ProcessBuilder(bsub);

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
                StringBuilder envValue = new StringBuilder("\"");
                for (Map.Entry<String, String> entry : env.entrySet()) {
                    envValue.append(entry.getKey()).append("='").append(entry.getValue()).append("'").append(' ');
                }
                envValue.append("\"");
                submitFile.put("environment", envValue.toString());
                // Write the command file
                {
                    File file = new File(dir, "condor.commands");
                    PrintWriter writer = new PrintWriter(new FileWriter(file));
                    undoList.add(new DeleteFile(file));
                    writer.println("#!/bin/bash");
                    writer.print(command);
                    if (job.getArguments() != null) {
                        for (String argument : job.getArguments()) {
                            writer.print(' ');
                            writer.print(argument);
                        }
                    }
                    writer.close();
                    file.setExecutable(true);
                }
                // Write the submit file
                {
                    File file = new File(dir, "condor.submit");
                    PrintWriter writer = new PrintWriter(new FileWriter(file));
                    undoList.add(new DeleteFile(file));
                    for (Map.Entry<String, String> fileLine : submitFile.entrySet()) {
                        writer.print(fileLine.getKey());
                        writer.print("=");
                        writer.println(fileLine.getValue());
                    }
                    writer.println("queue");
                    writer.close();
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

    private void checkPermission(String ip) throws SecurityException {
        //FIXME: Do something better
        if (!ip.startsWith("134.79") && !ip.startsWith("198.129")) {
            throw new SecurityException();
        }
    }

    @Override
    public JobStatus status(String jobID) throws NoSuchJobException, JobControlException {
        try {
            String ip = RemoteServer.getClientHost();
            logger.info("status: " + jobID + " from " + ip);
            checkPermission(ip);

            Map<String, JobStatus> statii = status.getStatus();
            JobStatus result = statii.get(jobID);
            if (result == null) {
                throw new NoSuchJobException("Job id " + jobID);
            }
            return result;
        } catch (ServerNotActiveException t) {
            logger.log(Level.SEVERE, "Unexpected error", t);
            throw new JobControlException("Unexpected error", t);
        } catch (NoSuchJobException t) {
            logger.log(Level.SEVERE, "job status failed", t);
            throw t;
        } catch (JobControlException t) {
            logger.log(Level.SEVERE, "job status failed", t);
            throw t;
        }
    }

    @Override
    public void cancel(String jobID) throws NoSuchJobException, JobControlException {
        try {
            String ip = RemoteServer.getClientHost();
            logger.info("killing: " + jobID + " from " + ip);
            checkPermission(ip);

            cancelInternal(jobID);
            logger.fine("job " + jobID + " cancelled");
        } catch (ServerNotActiveException t) {
            logger.log(Level.SEVERE, "Unexpected error", t);
            throw new JobControlException("Unexpected error", t);
        } catch (NoSuchJobException t) {
            logger.log(Level.SEVERE, "job cancellation failed", t);
            throw t;
        } catch (JobControlException t) {
            logger.log(Level.SEVERE, "job cancellation failed", t);
            throw t;
        }
    }

    private void cancelInternal(String jobID) throws NoSuchJobException, JobControlException {
        try {
            List<String> commands = new ArrayList<String>();
            commands.add(KILL_COMMAND);
            commands.add(jobID);
            ProcessBuilder builder = new ProcessBuilder();
            builder.redirectErrorStream(true);
            builder.command(commands);
            Process process = builder.start();
            OutputProcessor output = new OutputProcessor(process.getInputStream(), logger);
            process.waitFor();
            output.join();
            int rc = process.exitValue();
            if (rc == 1) {
                throw new NoSuchJobException("No such job, id=" + jobID);
            } else if (rc != 0) {
                throw new JobControlException("Command failed rc=" + rc);
            }
        } catch (IOException x) {
            throw new JobControlException("IOException while killing job " + jobID, x);
        } catch (InterruptedException x) {
            throw new JobControlException("InterruptedException while killing job " + jobID, x);
        }
    }

    @Override
    public String getStatus() {
        try {
            status.getStatus();
            return "OK";
        } catch (JobControlException x) {
            logger.log(Level.SEVERE, "Error getting status", x);
            return "Bad " + (x.getMessage());
        }
    }

    @Override
    public Map<String, Integer> getJobCounts() {
        try {
            return computeJobCounts(status.getStatus());
        } catch (JobControlException x) {
            logger.log(Level.SEVERE, "Error getting job counts", x);
            return null;
        }
    }
}
