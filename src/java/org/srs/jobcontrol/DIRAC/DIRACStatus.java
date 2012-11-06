/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.srs.jobcontrol.DIRAC;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.srs.jobcontrol.*;
import org.srs.jobcontrol.common.CommonJobStatus;
import org.srs.jobcontrol.DIRAC.StatusXML.*;
import org.srs.jobcontrol.JobStatus.Status;
/*
 *
 * @author zimmer
 */
class DIRACStatus
{
   private final static long CACHE_TIME = 60*1000; // Needed to avoid excessive calls to bjobs
   private final static String STATUS_COMMAND = System.getProperty("org.srs.jobcontrol.DIRACstatusCommand","dirac-status -p xml=True");
   private final static Logger logger = Logger.getLogger("org.srs.jobcontrol.DIRACStatus");
   
   private Map<String,JobStatus> map;
   private long timeStamp;
   
   /** Creates a new instance of DIRACStatus */
   DIRACStatus()
   {
   }
   
   private void updateStatus() throws JobControlException
   {
      try
      {
         String command = STATUS_COMMAND;
         List<String> commands = new ArrayList<String>(Arrays.asList(command.split("\\s+")));
         ProcessBuilder builder = new ProcessBuilder();
         
         builder.command(commands);
         Process process = builder.start();
         OutputProcessor output = new OutputProcessor(process.getErrorStream(),logger);
         process.waitFor();
         output.join();
         int rc = process.exitValue();
         if (rc != 0) {
              throw new JobControlException("Process failed, rc="+rc);
          }
         
         JAXBContext jc = JAXBContext.newInstance ("org.srs.jobcontrol.DIRAC.StatusXML");
         Unmarshaller um = jc.createUnmarshaller ();
         SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         final Joblist joblist = (Joblist)um.unmarshal(process.getInputStream());
         // main loop over all jobs
         for (Joblist.Job job : joblist.getJob()){
             final String job_id = String.valueOf(job.getJobID());
             CommonJobStatus stat = new CommonJobStatus();
             stat.setId(job_id);
             if (job.getTotalCPUTimes()!=null || !"-".equals(job.getTotalCPUTimes())){
                 stat.setCpuUsed(Math.round(job.getTotalCPUTimes()));
             }
             if (job.getMemorykB()!=null || !"-".equals(job.getMemorykB())){
                 String memory_kb = job.getMemorykB().replace("kB",""); // now we removed the kB
                 Float memory_kb_flt = Float.valueOf(memory_kb).floatValue();
                 int memory_used = Math.round(memory_kb_flt)*1024;
                 stat.setMemoryUsed(memory_used);
             }
             if (job.getSite()!=null || !"-".equals(job.getSite())){
                 stat.setHost(job.getSite());
             }
             
             if (job.getStarted()!=null || !"-".equals(job.getStarted())){
                 final String str_started_time = job.getStarted();
                 Date start_time = (Date)formatter.parse(str_started_time);
                 stat.setStarted(start_time);
             }
             if (job.getEnded()!=null || !"-".equals(job.getEnded())){
                 final String str_end_time = job.getEnded();
                 Date end_time = (Date)formatter.parse(str_end_time);
                 stat.setStarted(end_time);
             }
             if (job.getSubmitted()!=null || !"-".equals(job.getSubmitted())){
                 final String str_sub_time = job.getEnded();
                 Date submit_time = (Date)formatter.parse(str_sub_time);
                 stat.setStarted(submit_time);
             }
             if (job.getPilotReference()!=null){
                 final String pilot_reference = job.getPilotReference();
                 stat.setComment("pilot reference="+pilot_reference);
             }
             if (job.getStatus()!=null){
                 Status sstatus = toStatus(job.getStatus());
                 System.out.println("*DEBUG* status found "+sstatus.toString());
                 stat.setStatus(sstatus);
             }
            map.put(job_id,stat); 
         }
         System.out.println("*INFO* statuses found=" + map);
   
	 synchronized (this)
         {
            this.map = map;
            this.timeStamp = System.currentTimeMillis();
         }
         }
      catch (IOException x)
      {
         throw new JobControlException("IOException during job submission",x);
      }
      catch (InterruptedException x)
      {
         throw new JobControlException("Job submission interrupted",x);
      }
      catch (JAXBException e) 
      {
         System.out.println("*DEBUG* "+STATUS_COMMAND);
         throw new JobControlException("caught JAXB Exception",e);
      }
      catch (ParseException e){
          throw new JobControlException("caught Exception parsing date",e);
      }
          
   }
   private JobStatus.Status toStatus(String status)
   {
      System.out.println("status found=" + status);
      // statii queried by dirac-status command
      //'Done','Completed','Stalled','Failed','Killed','Waiting','Running','Checking'
      if      ("Done".equals(status)) {
           return JobStatus.Status.DONE;
       }
      else if ("Completed".equals(status)) {
           return JobStatus.Status.DONE;
       }
      else if ("Stalled".equals(status)) {
           return JobStatus.Status.SUSPENDED;
       }
      else if ("Failed".equals(status)) {
           return JobStatus.Status.FAILED;
       }
      else if ("Killed".equals(status)) {
           return JobStatus.Status.FAILED;
       }
      else if ("Waiting".equals(status)) {
           return JobStatus.Status.WAITING;
       }
      else if ("Running".equals(status)) {
           return JobStatus.Status.RUNNING;
       }
      else if ("Checking".equals(status)) {
           return JobStatus.Status.PENDING;
       }
      else {
           return JobStatus.Status.UNKNOWN;
       }
   }
   Map<String, JobStatus> getStatus() throws JobControlException
   {
      synchronized (this)
      {
         long now = System.currentTimeMillis();
         boolean updateNeeded = now-timeStamp > CACHE_TIME;
         logger.log(Level.FINE, "status: now={0} timeStamp={1} cache={2} update needed: {3}", new Object[]{now, timeStamp, CACHE_TIME, updateNeeded});
         if (updateNeeded) {
              updateStatus();
          }
      }
      return map;
   }
}
