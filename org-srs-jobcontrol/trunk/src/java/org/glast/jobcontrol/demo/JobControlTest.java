package org.glast.jobcontrol.demo;

import org.glast.jobcontrol.Job;
import org.glast.jobcontrol.JobControlClient;
import org.glast.jobcontrol.JobControlException;
import org.glast.jobcontrol.JobSubmissionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
      List<String> arguments = new ArrayList<String>();
      for (int i=2; i<args.length; i++) arguments.add(args[i]);
     
      Job job = new Job();
      job.setCommand(command);
      job.setArguments(arguments);
      JobControlClient client = new JobControlClient();
      int id = client.submit(job);
      System.out.println("Job "+id+" submitted");
   }
   private static void usage()
   {
      System.out.println("usage: java "+JobStatusTest.class.getName()+"command [args...]");
      System.exit(0);
   }
}
