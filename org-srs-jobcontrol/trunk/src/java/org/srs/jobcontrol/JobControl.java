package org.srs.jobcontrol;

import com.healthmarketscience.rmiio.RemoteInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
   String getFile(String sp_id, File workingDir, String fileName) 
           throws RemoteException, FileNotFoundException, TimeoutException, JobControlException;
   RemoteInputStream getFileStream(String spID, File workingDir, String fileName)
            throws IOException, JobControlException;
   void cancel(String jobID) throws RemoteException, NoSuchJobException, JobControlException;
}
