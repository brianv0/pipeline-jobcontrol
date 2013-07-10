package org.srs.jobcontrol;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.TimeoutException;

/**
 * The interface to be implemented by the job control server
 * @author tonyj
 */
public interface JobControl extends Remote
{
   String submit(Job job) throws RemoteException, JobSubmissionException, JobControlException;
   JobStatus status(String jobID) throws RemoteException, NoSuchJobException, JobControlException;
   String summary(File workingDir) throws RemoteException, FileNotFoundException, TimeoutException, JobControlException;
   void cancel(String jobID) throws RemoteException, NoSuchJobException, JobControlException;
}
