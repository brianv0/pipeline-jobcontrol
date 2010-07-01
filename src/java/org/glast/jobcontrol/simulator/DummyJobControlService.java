package org.glast.jobcontrol.simulator;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import org.glast.jobcontrol.Job;
import org.glast.jobcontrol.JobControl;
import org.glast.jobcontrol.JobControlException;
import org.glast.jobcontrol.JobStatus;
import org.glast.jobcontrol.JobSubmissionException;
import org.glast.jobcontrol.NoSuchJobException;
import java.util.concurrent.atomic.AtomicInteger;
import org.glast.jobcontrol.common.BaseJobStatus;

/**
 *
 * @author tonyj
 */
public class DummyJobControlService implements JobControl {

    private static final Logger logger = Logger.getLogger("org.glast.jobcontrol.simulator");
    private Map<String, FakeJob> jobs = Collections.synchronizedMap(new WeakHashMap<String, FakeJob>());
    private static AtomicInteger nextJobId = new AtomicInteger(1);
    private JobExecutor executor;

    public DummyJobControlService(JobExecutor executor) {
        this.executor = executor;
    }

    public void start() throws RemoteException {
        JobControl stub = (JobControl) UnicastRemoteObject.exportObject(this, 0);

        String user = System.getProperty("user.name");
        // Bind the remote object's stub in the registry
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("JobControlService-" + user, stub);
        DummyJobControlService.logger.info("Server ready, user " + user);
    }

    @Override
    public String submit(Job job) throws RemoteException, JobSubmissionException, JobControlException {
        String id = generateJobId();
        FakeJob fakeJob = new FakeJob(id, job);
        jobs.put(id, fakeJob);
        executor.submit(fakeJob);
        return id;
    }

    @Override
    public JobStatus status(String id) throws RemoteException, NoSuchJobException, JobControlException {
        FakeJob job = jobs.get(id);
        if (job == null) {
            throw new NoSuchJobException(id);
        }
        synchronized (job) {
           BaseJobStatus status = new BaseJobStatus();
           status.setComment("");
           status.setHost("dummyHost");
           status.setSubmitted(job.getSubmitted());
           status.setStarted(job.getStarted());
           status.setEnded(job.getEnded());
           status.setUser(job.getUser());
           status.setStatus(job.getStatus());
           return status;
        }
    }

    @Override
    public void cancel(String id) throws RemoteException, NoSuchJobException, JobControlException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String generateJobId() {
        return String.valueOf(nextJobId.getAndIncrement());
    }

}
