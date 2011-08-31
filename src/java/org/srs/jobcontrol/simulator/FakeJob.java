package org.srs.jobcontrol.simulator;

import java.util.Date;
import org.srs.jobcontrol.Job;
import org.srs.jobcontrol.JobStatus.Status;

public class FakeJob {

    private String id;
    private Job job;
    private Date submitted;
    private Date started;
    private Date ended;
    private Status status;

    public FakeJob(String id, Job job) {
        super();
        this.id = id;
        this.job = job;
        this.status = status.PENDING;
        this.submitted = new Date();
    }

    public Date getEnded() {
        return ended;
    }

    public long getProcessInstance() {
        return Long.valueOf(job.getEnv().get("PIPELINE_PROCESSINSTANCE"));
    }

    public Date getStarted() {
        return started;
    }

    public Status getStatus() {
        return status;
    }

    public Date getSubmitted() {
        return submitted;
    }

    public String getUser() {
        return "DummyUser";
    }

    synchronized void setEnded(Date endDate) {
        ended = endDate;
        status = Status.DONE;
    }

    synchronized void setStarted(Date startDate) {
        started = startDate;
        status = Status.RUNNING;
    }
    String getWorkingDir()
    {
        return job.getWorkingDirectory();
    }
    String getID()
    {
        return id;
    }
}
