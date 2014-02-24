package org.srs.jobcontrol;

import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * This is the main class clients should use to interact with the job submission system.
 * @author Tony Johnson
 */
public class JobControlClient
{
   private final String user;
   private final String host; 
   private final String serviceName;
   private final int port;
   private JobControl service;
   
   public JobControlClient()
   {
      this(null);
   }
   public JobControlClient(String user)
   {
     this(user,null);
   }
   public JobControlClient(String user, String host)
   {
      this(user,host,1099);
   }
   public JobControlClient(String user, String host, int port)
   {
      this(user,host,port,null);
   }
   public JobControlClient(String user, String host, int port, String serviceName)
   {
      this.user = user == null ? "glast" : user;
      this.host = host == null ? "glast-jobcontrol01.slac.stanford.edu" : host;
      this.port = port;
      this.serviceName = serviceName == null ? "JobControlService" : serviceName;
   }
   
   private JobControl getJobControl() throws NotBoundException, RemoteException
   {
      if(service == null){
          Registry registry = LocateRegistry.getRegistry(host,port);
          service = (JobControl) registry.lookup(serviceName+"-"+user);
      }
      return service;
   }

    /**
     * Submit a job.
     * @param job The job to be submitted
     * @throws org.srs.jobcontrol.JobSubmissionException Thrown if an error occurs during job submission
     * @return The job ID
     */
   public String submit(Job job) throws JobSubmissionException, JobControlException
   {
      try
      {
         return getJobControl().submit(job);
      }
      catch (NotBoundException x)
      {
         throw new JobControlException("Server not running during job submission",x);
      }
      catch (RemoteException x)
      {
         this.service = null;
         throw new JobControlException("Remote Exception during job submission",x.getCause());
      }
   }
   
    /**
     * Get a map of statuses from a list of jobs of interest
     * @param jobIDs List of all jobIDs we are interested in.
     * @throws org.srs.jobcontrol.NoSuchJobException Thrown if the specified ID is unknown, or if any other error occurs.
     * @return The jobs status
     */
   public Map<String, JobStatus> arrayStatus(List<String> jobIDs) throws NoSuchJobException, JobControlException
   {
      try
      {
         return getJobControl().arrayStatus( jobIDs );
      }
      catch (NotBoundException x)
      {
         throw new JobControlException("Server not running while getting job status",x);
      }
      catch (RemoteException x)
      {
         this.service = null;
         throw new JobControlException("Remote Exception getting job status",x.getCause());
      }
   }
   
    /**
     * Get the status of a job.
     * @param jobID The jobID for which status should be returned
     * @throws org.srs.jobcontrol.NoSuchJobException Thrown if the specified ID is unknown, or if any other error occurs.
     * @return The jobs status
     */
   public JobStatus status(String jobID) throws NoSuchJobException, JobControlException
   {
      try
      {
         return getJobControl().status(jobID);
      }
      catch (NotBoundException x)
      {
         throw new JobControlException("Server not running while getting job status",x);
      }
      catch (RemoteException x)
      {
         this.service = null;
         throw new JobControlException("Remote Exception getting job status",x.getCause());
      }
   }
   
    /**
     * Attempt to get a remote file as a string. Files should be small, otherwise you the stream.
     * @param spID String in the form of "streamPk:processInstancePk"
     * @param workingDir Working dir of this process instance
     * @param fileName name of file relative to workingDir
     * @return A string representation of the file.
     * @throws FileNotFoundException
     * @throws TimeoutException
     * @throws JobControlException 
    */
   public String getFile(String spID, File workingDir, String fileName) 
           throws FileNotFoundException, TimeoutException, JobControlException
   {
      try {
         return getJobControl().getFile( spID, workingDir, fileName );
      }
      catch (RemoteException x) {
         this.service = null;
         throw new JobControlException( "Remote Exception getting job status", x.getCause());
      }
      catch (NotBoundException x) {
         throw new JobControlException("Server not running while getting job status",x);
      }
   }
   
    /**
     * Get an InputStream from a remote file to be transferred over RMI.
     * You should do everything you can to close this InputStream when you are done.
     * 
     * @param spID String in the form of "streamPk:processInstancePk"
     * @param workingDir Working dir of this process instance
     * @param fileName name of file relative to workingDir
     * @return A InputStream to a remote file
     */
   public InputStream getFileStream(String spID, File workingDir, String fileName) 
           throws IOException, JobControlException
   {
      try {
         return RemoteInputStreamClient.wrap( 
                 getJobControl().getFileStream( spID, workingDir, fileName ) );
      }
      catch (RemoteException x) { 
         this.service = null;
         throw new JobControlException( "Remote Exception getting remote file stream", x.getCause());
      }
      catch (NotBoundException x) {
         throw new JobControlException("Server not running while getting remote file stream",x);
      }
   }
   
   /**
    * Cancels a job. If the job is already running it will be killed.
    */
   public void cancel(String jobID) throws NoSuchJobException, JobControlException
   {
      try
      {
         getJobControl().cancel(jobID);
      }
      catch (NotBoundException x)
      {
         throw new JobControlException("Server not running while killing job",x);
      }
      catch (RemoteException x)
      {
         this.service = null;
         throw new JobControlException("Remote Exception killing job",x.getCause());
      }
   }
}
