package org.srs.jobcontrol;

import java.util.Date;

/**
 *
 * @author tonyj
 */
public interface JobStatus
{
    public enum Status { DONE, PENDING, RUNNING, WAITING, SUSPENDED, FAILED, UNKNOWN };
    String getComment();
    int getCpuUsed();
    Date getEnded();
    String getHost();
    String getId();
    int getMemoryUsed();
    String getQueue();
    Date getStarted();
    Status getStatus();
    Date getSubmitted();
    int getSwapUsed();
    String getUser();
}
