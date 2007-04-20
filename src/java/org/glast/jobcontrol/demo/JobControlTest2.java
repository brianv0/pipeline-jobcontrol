package org.glast.jobcontrol.demo;

import org.glast.jobcontrol.Job;
import org.glast.jobcontrol.JobControlClient;
import org.glast.jobcontrol.JobControlException;
import org.glast.jobcontrol.JobSubmissionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Tony Johnson
 */
public class JobControlTest2
{
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) throws JobSubmissionException, IOException, JobControlException
   {
      BufferedReader reader  = new BufferedReader(new InputStreamReader(JobControlTest2.class.getResourceAsStream("run.csh")));
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
      files.put("run.csh",script.toString());
      
      Job job = new Job();
      job.setFiles(files);
      job.setCommand("csh < run.csh");
      job.setWorkingDirectory("/nfs/farm/g/glast/u13/DataServer/xxx");
      Map<String,String> env = new HashMap<String,String>();
      env.put("email","tonyj@slac.stanford.edu");
      env.put("PRUNE_TASK","ChickenLittle-GR-v7r3p24");
      env.put("PRUNE_DATATYPE","merit");
      env.put("PRUNE_TCUT","EvtEventId < 10000");
      env.put("PRUNE_OUTFILE","outfile");
      env.put("PRUNE_DEBUG","true");
      job.setEnv(env);
      job.setName("Test2");
      JobControlClient client = new JobControlClient("glastpro","ccsvli09.in2p3.fr");
      int id = client.submit(job);
      System.out.println("Job "+id+" submitted");
   }
   private static void usage()
   {
      System.out.println("usage: java "+JobStatusTest.class.getName()+"command [args...]");
      System.exit(0);
   }
}
