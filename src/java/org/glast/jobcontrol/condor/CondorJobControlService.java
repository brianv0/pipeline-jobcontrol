package org.glast.jobcontrol.condor;

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
import org.glast.jobcontrol.Job;
import org.glast.jobcontrol.JobControl;
import org.glast.jobcontrol.JobControlException;
import org.glast.jobcontrol.JobStatus;
import org.glast.jobcontrol.JobSubmissionException;
import org.glast.jobcontrol.NoSuchJobException;
import org.glast.jobcontrol.OutputProcessor;
import org.glast.jobcontrol.common.JobControlService;
import org.glast.jobcontrol.common.JobControlService.DeleteFile;

/**
 * The main class for the LSF job control server.
 * @author Tony Johnson
 */
class CondorJobControlService extends JobControlService {

    private final static String SUBMIT_COMMAND = "/usr/local/bin/condor_submit condor.submit";
    private final static String KILL_COMMAND = "/usr/local/bin/bkill";
    private final static Pattern pattern = Pattern.compile("submitted to cluster (\\d+)\\.");

    private CondorJobControlService() {
    }

    public static void main(String[] args) throws RemoteException, JMException {
        CondorJobControlService service = new CondorJobControlService();
        JobControl stub = (JobControl) UnicastRemoteObject.exportObject(service, 0);

        String user = System.getProperty("user.name");
        // Bind the remote object's stub in the registry
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("JobControlService-" + user, stub);
        CondorJobControlService.logger.info("Server ready, user " + user);

        // Register the JMX bean
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("org.glast.jobcontrol:type=JobControlService");
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
        submitFile.put("executable", command);


        Map<String, String> env = new HashMap<String, String>();
        if (job.getEnv() != null) {
            env.putAll(job.getEnv());
        }


//        if (job.getMaxCPU() != 0) {
//            bsub.add("-c");
//            bsub.add(String.valueOf(convertToMinutes(job.getMaxCPU())));
//        }
//        if (job.getMaxMemory() != 0) {
//            bsub.add("-M");
//            bsub.add(String.valueOf(job.getMaxMemory()));
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
        String fullCommand = toFullCommand(bsub);
        logger.info("Submit: " + fullCommand);
        env.put("JOBCONTROL_SUBMIT_COMMAND", fullCommand);

        // Things to be undone if the submit fails.
        List<Runnable> undoList = new ArrayList<Runnable>();
        try {
            ProcessBuilder builder = new ProcessBuilder(bsub);

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
                    envValue.append(entry.getKey()).append("='").append(entry.getValue()).append("'");
                }
                envValue.append("\"");
                submitFile.put("environment", envValue.toString());
                // Add the arguments to the submit file
                if (job.getArguments() != null) {

                    StringBuilder argValue = new StringBuilder("\"");
                    for (String argument : job.getArguments()) {
                        argValue.append("'").append(argument).append("'");
                    }
                    argValue.append("\"");
                    submitFile.put("arguments", argValue.toString());
                }
                // Write the submit file
                File file = new File(dir, "condor.submit");
                PrintWriter writer = new PrintWriter(new FileWriter(file));
                undoList.add(new DeleteFile(file));
                for (Map.Entry<String, String> fileLine : submitFile.entrySet()) {
                    writer.print(fileLine.getKey());
                    writer.print("=");
                    writer.println(fileLine.getValue());
                }
                writer.close();
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
        if (!ip.startsWith("134.79") && !ip.startsWith("198.129")) {
            throw new SecurityException();
        }
    }

    @Override
    public JobStatus status(String jobID) throws NoSuchJobException, JobControlException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void cancel(String jobID) throws NoSuchJobException, JobControlException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getStatus() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, Integer> getJobCounts() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
