package org.srs.jobcontrol;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The interface to be implemented by the job control server
 * @author tonyj
 */
public interface JobControl extends Remote
{
   String submit(Job job) throws RemoteException, JobSubmissionException, JobControlException;
   JobStatus status(String jobID) throws RemoteException, NoSuchJobException, JobControlException;
   void cancel(String jobID) throws RemoteException, NoSuchJobException, JobControlException;
}
