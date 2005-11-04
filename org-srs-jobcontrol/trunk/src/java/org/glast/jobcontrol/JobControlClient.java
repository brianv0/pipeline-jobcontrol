package org.glast.jobcontrol;

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
   private JobControl getJobControl() throws NotBoundException, RemoteException
   {
      // FixMe: This should not be hardwired
      Registry registry = LocateRegistry.getRegistry("glast03.slac.stanford.edu");
      return (JobControl) registry.lookup("JobControl");
   }
    /**
     * Submit a job.
     * @param job The job to be submitted
     * @throws glast.jobcontrol.JobSubmissionException Thrown if an error occurs during job submission
     * @return The job ID
     */
   public int submit(Job job) throws JobSubmissionException
   {
      try
      {
         return getJobControl().submit(job);
      }
      catch (NotBoundException x)
      {
         throw new JobSubmissionException("Remote Exception during job submission",x);
      }
      catch (RemoteException x)
      {
         throw new JobSubmissionException("Remote Exception during job submission",x.getCause());
      }
   }
    /**
     * Get the status of a job.
     * @param jobID The jobID for which status should be returned
     * @throws glast.jobcontrol.NoSuchJobException Thrown if the specified ID is unknown, or if any other error occurs.
     * @return The jobs status
     */
   public JobStatus status(int jobID) throws NoSuchJobException
   {
      try
      {
         return getJobControl().status(jobID);
      }
      catch (NotBoundException x)
      {
         throw new NoSuchJobException("Remote Exception getting job status",x);
      }
      catch (RemoteException x)
      {
         throw new NoSuchJobException("Remote Exception getting job status",x.getCause());
      }
   }
}
