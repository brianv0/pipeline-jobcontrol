package org.srs.jobcontrol.simulator;

import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author tonyj
 */
public class JobExecutor
{
   private int maxRunning;
   private int nRunning;
   private MailSender sender;
   private Random random = new Random();
   private BlockingQueue<FakeJob> jobQueue = new LinkedBlockingQueue<FakeJob>();
   Timer timer = new Timer();
   private float sigma;
   private float time;
   private float failureProbability;

   JobExecutor(MailSender sender, int maxRunning, float time, float sigma, float failureProbability)
   {
      this.sender = sender;
      this.maxRunning = maxRunning;
      this.time = time;
      this.sigma = sigma;
      this.failureProbability = failureProbability;
   }

   float getFailureRate()
   {
      return failureProbability;
   }

   void setFailureRate(float rate)
   {
      failureProbability = rate;
   }

   int getNRunning()
   {
      return nRunning;
   }

   int getQueueSize()
   {
      return jobQueue.size();
   }

   void setMaxJobs(int maxJobs)
   {
      maxRunning = maxJobs;
      checkSubmitJobs();
   }

   int getMaxJobs()
   {
      return maxRunning;
   }

   void setJobTime(float time)
   {
      this.time = time;
   }

   float getJobTime()
   {
      return time;
   }

   void setJobSigma(float sigma)
   {
      this.sigma = sigma;
   }

   float getJobSigma()
   {
      return sigma;
   }

   public synchronized void submit(FakeJob job)
   {

      jobQueue.add(job);
      checkSubmitJobs();

   }

   private synchronized void checkSubmitJobs()
   {
      while (nRunning < maxRunning)
      {
         FakeJob job = jobQueue.poll();
         if (job == null)
         {
            break;
         }
         startJob(job);
         long estimatedTime = estimateTime(time, sigma);
         timer.schedule(new EndTask(job), estimatedTime);
      }
   }

   private long estimateTime(float mean, float sigma)
   {
      long result = (long) ((mean + sigma * random.nextGaussian()) * 1000);
      if (result < 0)
      {
         result = 0;
      }
      return result;
   }

   private synchronized void startJob(FakeJob job)
   {
      try
      {
         Date startDate = new Date();
         job.setStarted(startDate);
         sender.sendStartMessage(job.getProcessInstance(), job.getWorkingDir(), startDate);
         nRunning++;
      }
      catch (MessagingException ex)
      {
         Logger.getLogger(JobExecutor.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   private synchronized void endJob(FakeJob job)
   {
      try
      {
         Date endDate = new Date();
         job.setEnded(endDate);
         int rc = random.nextFloat() < failureProbability ? -1 : 0;
         sender.sendEndMessage(job.getProcessInstance(), job.getWorkingDir(), job.getStarted(), endDate, rc);
         nRunning--;
         checkSubmitJobs();
      }
      catch (MessagingException ex)
      {
         Logger.getLogger(JobExecutor.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   private class EndTask extends TimerTask
   {
      private FakeJob job;

      EndTask(FakeJob job)
      {
         this.job = job;
      }

      @Override
      public void run()
      {
         endJob(job);
      }
   }
}
