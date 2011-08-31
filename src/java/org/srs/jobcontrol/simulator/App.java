package org.srs.jobcontrol.simulator;

import java.lang.management.ManagementFactory;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Hello world!
 *
 */
public class App implements AppMBean {

    private JobExecutor jobExecutor;
    private MailSender mailSender;
    private DummyJobControlService service;

    public static void main(String[] args) throws RemoteException {
        App app = new App();
        app.start();
    }

    private App() {
        mailSender = new MailSender(System.getProperty("smtp.server","smtp.jaws.com"),
                                    Integer.getInteger("smtp.server.port",25),
                                    System.getProperty("from.address","pipeline-test@slac.stanford.edu"), 
                                    System.getProperty("to.address","tonyj321@jaws.com"));
        jobExecutor = new JobExecutor(mailSender, 
                                    Integer.getInteger("jobs.max", 10), 
                                    Integer.getInteger("job.time",100),
                                    Integer.getInteger("job.sigma",5),
                                    Float.parseFloat(System.getProperty("job.failureRate","0")));
        service = new DummyJobControlService(jobExecutor);

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            mbs.registerMBean(this, new ObjectName("org.srs.jobcontrol.simulator:type=App"));
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setMaxJobs(int maxJobs) {
        jobExecutor.setMaxJobs(maxJobs);
    }

    public int getMaxJobs() {
        return jobExecutor.getMaxJobs();
    }

    public void setJobTime(float time) {
        jobExecutor.setJobTime(time);
    }

    public float getJobTime() {
        return jobExecutor.getJobTime();
    }

    public void setJobSigma(float time) {
        jobExecutor.setJobSigma(time);
    }

    public float getJobSigma() {
        return jobExecutor.getJobSigma();
    }

    public int getNRunning() {
        return jobExecutor.getNRunning();
    }

    public int getQueueSize() {
        return jobExecutor.getQueueSize();
    }
    public void setFailureRate(float rate) {
       jobExecutor.setFailureRate(rate);
    }
    public float getFailureRate() {
       return jobExecutor.getFailureRate();
    }

    private void start() throws RemoteException {
        service.start();
    }
}
