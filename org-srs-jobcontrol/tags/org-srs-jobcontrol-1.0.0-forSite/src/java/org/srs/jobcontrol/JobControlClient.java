package org.srs.jobcontrol;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
      
      Registry registry = LocateRegistry.getRegistry(host,port);
      return (JobControl) registry.lookup(serviceName+"-"+user);
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
         throw new JobControlException("Remote Exception during job submission",x.getCause());
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
         throw new JobControlException("Remote Exception getting job status",x.getCause());
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
         throw new JobControlException("Remote Exception killing job",x.getCause());
      }      
   }
}
