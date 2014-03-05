package org.srs.jobcontrol.demo;

import org.srs.jobcontrol.Job;
import org.srs.jobcontrol.JobControlClient;
import org.srs.jobcontrol.JobControlException;
import org.srs.jobcontrol.JobSubmissionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Tony Johnson
 */
public class BQSJobControlTest
{
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) throws JobSubmissionException, IOException, JobControlException
   {
      BufferedReader reader  = new BufferedReader(new InputStreamReader(JobControlTest2.class.getResourceAsStream("hello.csh")));
      StringBuffer script = new StringBuffer();
      for (;;)
      {
         String line = reader.readLine();
         if (line == null) break;
         script.append(line);
         script.append('\n');
      }
      reader.close();
      Map<String,String> files = new HashMap<String,String>();
      files.put("hello.csh",script.toString());

      Job job = new Job();
      job.setFiles(files);

      String workDir = "/sps/glast/Pipeline2/workDir";
      job.setWorkingDirectory(workDir);
      job.setCommand("csh < hello.csh");
      Map<String,String> env = new HashMap<String,String>();
      env.put("email","tonyj@slac.stanford.edu");
      job.setEnv(env);
      job.setName("Test2bis");
      JobControlClient client = new JobControlClient("glastpro","ccsvli09.in2p3.fr",1099,"BQSJobControlService");
      String id = client.submit(job);
      System.out.println("Job "+id+" submitted");
   }
   private static void usage()
   {
      System.out.println("usage: java "+JobStatusTest.class.getName()+"command [args...]");
      System.exit(0);
   }
}
