package org.glast.jobcontrol;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author tonyj
 */
interface JobControl extends Remote
{
   public int submit(Job job) throws RemoteException, JobSubmissionException;
   public JobStatus status(int jobID) throws RemoteException, NoSuchJobException;
}
