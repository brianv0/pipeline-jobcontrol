package org.srs.jobcontrol.demo;

import org.srs.jobcontrol.Job;
import org.srs.jobcontrol.JobControlClient;
import org.srs.jobcontrol.JobControlException;
import org.srs.jobcontrol.JobSubmissionException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tony Johnson
 */
public class JobControlTest
{
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) throws JobSubmissionException, JobControlException
   {
      if (args.length < 1) usage();
      String command = args[0];
      StringBuilder extraArgs = new StringBuilder();
      for (int i=2; i < args.length; i++) {
          extraArgs.append( args[i] ).append( " " );
      }
     
      Job job = new Job();
      job.setCommand(command);
      job.setExtraOptions( extraArgs.toString() );
      JobControlClient client = new JobControlClient();
      String id = client.submit(job);
      System.out.println("Job "+id+" submitted");
   }
   private static void usage()
   {
      System.out.println("usage: java "+JobStatusTest.class.getName()+"command [args...]");
      System.exit(0);
   }
}
