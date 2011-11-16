package org.srs.jobcontrol.common;

import org.srs.jobcontrol.*;
import org.srs.jobcontrol.JobStatus.Status;
import java.io.Serializable;
import java.util.Date;

/**
 * Summary of the status of a job.
 * @author tonyj
 */
public final class CommonJobStatus implements Serializable, JobStatus {

    static final long serialVersionUID = 6311542340392104385L;
    private String id;
    private Status status;
    private String host;
    private String queue;
    private int cpuUsed;
    private int memoryUsed;
    private int swapUsed;
    private Date submitted;
    private Date started;
    private Date ended;
    private String comment;
    private String user;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getQueue() {
        return queue;
    }

    @Override
    public int getCpuUsed() {
        return cpuUsed;
    }

    public int getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(int memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    @Override
    public int getSwapUsed() {
        return swapUsed;
    }

    @Override
    public Date getSubmitted() {
        return submitted;
    }

    @Override
    public Date getStarted() {
        return started;
    }

    @Override
    public Date getEnded() {
        return ended;
    }

    @Override
    public String getComment() {
        return comment;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public void setCpuUsed(int cpuUsed) {
        this.cpuUsed = cpuUsed;
    }

    public void setSwapUsed(int swapUsed) {
        this.swapUsed = swapUsed;
    }

    public void setSubmitted(Date submitted) {
        this.submitted = submitted;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public void setEnded(Date ended) {
        this.ended = ended;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Job " + id + " " + status + " " + host;
    }
}
