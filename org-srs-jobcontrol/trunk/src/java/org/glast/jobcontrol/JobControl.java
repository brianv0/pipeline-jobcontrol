package org.glast.jobcontrol;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The interface to be implemented by the job control server
 * @author tonyj
 */
interface JobControl extends Remote
{
   int submit(Job job) throws RemoteException, JobSubmissionException, JobControlException;
   JobStatus status(int jobID) throws RemoteException, NoSuchJobException, JobControlException;
   void cancel(int jobID) throws RemoteException, NoSuchJobException, JobControlException;
}
