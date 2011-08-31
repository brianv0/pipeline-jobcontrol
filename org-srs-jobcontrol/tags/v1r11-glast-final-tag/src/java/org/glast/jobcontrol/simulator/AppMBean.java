package org.glast.jobcontrol.simulator;

/**
 *
 * @author tonyj
 */
public interface AppMBean {

    float getJobSigma();

    float getJobTime();

    float getFailureRate();

    int getMaxJobs();

    int getNRunning();

    int getQueueSize();

    void setJobSigma(float time);

    void setJobTime(float time);

    void setMaxJobs(int maxJobs);

    void setFailureRate(float rate);
}

