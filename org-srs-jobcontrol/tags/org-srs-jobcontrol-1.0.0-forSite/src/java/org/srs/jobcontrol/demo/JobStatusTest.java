package org.srs.jobcontrol.demo;
import org.srs.jobcontrol.JobControlClient;
import org.srs.jobcontrol.JobControlException;
import org.srs.jobcontrol.JobStatus;
import org.srs.jobcontrol.NoSuchJobException;

/**
 *
 * @author Tony Johnson
 */
public class JobStatusTest
{
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) throws NoSuchJobException, JobControlException
   {
      JobControlClient client = new JobControlClient();
      JobStatus status = client.status(args[0]);
      System.out.println(status);
      
      //client.cancel("316983");
   }
}
