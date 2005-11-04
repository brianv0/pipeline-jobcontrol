package org.glast.jobcontrol.demo;
import org.glast.jobcontrol.JobControlClient;
import org.glast.jobcontrol.JobStatus;
import org.glast.jobcontrol.JobSubmissionException;
import org.glast.jobcontrol.NoSuchJobException;

/**
 *
 * @author Tony Johnson
 */
public class JobStatusTest
{
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) throws NoSuchJobException
   {
      JobControlClient client = new JobControlClient();
      JobStatus status = client.status(699183);
      System.out.println(status);
   }
}
