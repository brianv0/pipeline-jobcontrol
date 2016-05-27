package org.srs.jobcontrol.slurm;

import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

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

/**
 * Slurm job submission daemon.
 * @author bvan
 */
public class SlurmJobControlService extends CLIJobControlService {
    private final String DEFAULT_SUBMIT = "sbatch";
    private final String SUBMIT_COMMAND = 
            System.getProperty("org.srs.jobcontrol.slurm.submitCommand", DEFAULT_SUBMIT);
    private final static String KILL_COMMAND = 
            System.getProperty("org.srs.jobcontrol.slurm.killCommand", "scancel");
    
    private final Supplier<Map<String, JobStatus>> statii;

    /**
     * 
     * @param username Slurm user name
     */
    private SlurmJobControlService(){ 
        statii = new SlurmStatusSupplier(System.getProperty("user.name"));
    }
    
    public static void main(String[] args) throws RemoteException, JMException{
        String user = System.getProperty("user.name");
        SlurmJobControlService service = new SlurmJobControlService();
        JobControl stub = (JobControl) UnicastRemoteObject.exportObject(service, 1097);
        // Bind the remote object's stub in the registry
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind(service.getClass().getSimpleName() + "-" + user, stub);
        SlurmJobControlService.logger.log(Level.INFO, "Server ready, user {0}", user);

        // Register the JMX bean
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("org.srs.jobcontrol:type=JobControlService");
        mbs.registerMBean(service, name);
    }

    @Override
    public String submit(Job job) throws JobSubmissionException, JobControlException{
        try {
            logger.info("BEGIN slurm JobControlService submit");
            String ip = RemoteServer.getClientHost();
            logger.log(Level.INFO, "submitting: {0} from {1}", new Object[]{job.getCommand(), ip});
            checkPermission(ip);

            String jobName = submitInternal(job);
            logger.log(Level.FINE, "job {0} submitted", jobName);
            nSubmitted.incrementAndGet();
            lastSuccessfulJobSubmissionTime = System.currentTimeMillis();
            logger.info("END PBS JobControlService submit");
            return jobName;
        } catch(ServerNotActiveException ex) {
            logger.log(Level.SEVERE, "Unexpected error", ex);
            this.lastFailedJobSubmissionTime = System.currentTimeMillis();
            throw new JobControlException("Unexpected error", ex);
        } catch(JobSubmissionException | JobControlException ex) {
            logger.log(Level.SEVERE, "job submission failed", ex);
            this.lastFailedJobSubmissionTime = System.currentTimeMillis();
            throw ex;
        }
    }

    private String submitInternal(Job job) throws JobSubmissionException, JobControlException{
        SlurmScriptJobBuilder jobBuilder = new SlurmScriptJobBuilder();
        jobBuilder.build(job);
        // submit
        List<String> commands = new ArrayList<>();
        commands.add(SUBMIT_COMMAND);
        
        String command = "slurm_pilot"; // normally would do job.getCommand(), but that won't work here
        commands.add(command);
        
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(Paths.get(job.getWorkingDirectory()).toFile());
        builder.redirectErrorStream(true);
        builder.command(commands);
        
        Process process;
        try {
            process = builder.start();
            OutputProcessor output = new OutputProcessor(process.getInputStream(), logger);
            process.waitFor();
            output.join();
            int rc = process.exitValue();
            if(rc != 0){
                logger.log(Level.INFO, "Error submitting job:\n", Joiner.on("\n").join(output.getResult()));
                throw new JobControlException("Command failed rc=" + rc,
                    new RuntimeException(Joiner.on("\n").join(output.getResult())));
            }
            return extractJobId(output.getResult());
        } catch(IOException | InterruptedException ex) {
            logger.log(Level.SEVERE, "Unkown error submitting job", ex);
            throw new JobControlException("Error submitting job", ex);
        }
    }

    @Override
    public void cancel(String jobID) throws NoSuchJobException, JobControlException{
        try {
            String ip = RemoteServer.getClientHost();
            logger.log(Level.INFO, "killing: {0} from {1}", new Object[]{jobID, ip});
            checkPermission(ip);
            cancelInternal(jobID);
            logger.log(Level.FINE, "job {0} cancelled", jobID);
        } catch(ServerNotActiveException t) {
            logger.log(Level.SEVERE, "Unexpected error", t);
            throw new JobControlException("Unexpected error", t);
        } catch(NoSuchJobException | JobControlException t) {
            logger.log(Level.SEVERE, "job cancellation failed", t);
            throw t;
        }
    }

    private void cancelInternal(String jobID) throws NoSuchJobException, JobControlException{
        try {
            List<String> commands = new ArrayList<>();
            commands.add(KILL_COMMAND);
            commands.add(String.valueOf(jobID));
            ProcessBuilder builder = new ProcessBuilder();
            builder.redirectErrorStream(true);
            builder.command(commands);
            Process process = builder.start();
            OutputProcessor output = new OutputProcessor(process.getInputStream(), logger);
            process.waitFor();
            output.join();
            
            int rc = process.exitValue();
            if(rc == 255){
                throw new NoSuchJobException("No such job, id=" + jobID);
            } else if(rc != 0){
                throw new JobControlException("Command failed rc=" + rc, 
                        new RuntimeException(Joiner.on("\n").join(output.getResult())));
            }
        } catch(IOException x) {
            throw new JobControlException("IOException while killing job " + jobID, x);
        } catch(InterruptedException x) {
            throw new JobControlException("InterruptedException while killing job " + jobID, x);
        }
    }

    @Override
    public Map<String, JobStatus> getCurrentStatus() throws JobControlException{
        try{
            return statii.get();
        } catch (RuntimeException ex){
            if(ex.getCause() instanceof JobControlException){
                throw (JobControlException) ex.getCause();
            }
            throw new JobControlException("Unkown runtime exception occurred: ", ex.getCause());
        }
    }

    @Override
    public String extractJobId(List<String> result) throws JobSubmissionException{
        return result.get(0).substring("Submitted batch job ".length());   // jobid at end of this string
    }

}
