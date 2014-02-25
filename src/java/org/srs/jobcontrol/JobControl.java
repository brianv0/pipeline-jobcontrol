package org.srs.jobcontrol;

import com.healthmarketscience.rmiio.RemoteInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * The interface to be implemented by the job control server
 * @author tonyj
 */
public interface JobControl extends Remote {

   public String submit(Job job) 
           throws RemoteException, JobSubmissionException, JobControlException;
   
   public JobStatus status(String jobID) 
           throws RemoteException, NoSuchJobException, JobControlException;
   
   public Map<String,JobStatus> arrayStatus(List<String> jobID) 
           throws RemoteException, JobControlException;
   
   public String getFile(String sp_id, File workingDir, String fileName) 
           throws RemoteException, FileNotFoundException, TimeoutException, JobControlException;
   
   public RemoteInputStream getFileStream(String spID, File workingDir, String fileName)
            throws IOException, JobControlException;
   
   public void cancel(String jobID) 
           throws RemoteException, NoSuchJobException, JobControlException;
}
