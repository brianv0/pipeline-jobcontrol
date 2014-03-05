
package org.srs.jobcontrol.gridEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import org.srs.jobcontrol.*;
import org.srs.jobcontrol.common.CommonJobStatus;
import org.srs.jobcontrol.gridEngine.qstat.*;
/*
 *
 * @author zimmer
 */
class gridEngineStatus
{
   private final static String STATUS_COMMAND = "qstat -s az -xml -ext"; 
   private final static Logger logger = Logger.getLogger("org.srs.jobcontrol.gridEngineStatus");
   
   
   /** Creates a new instance of gridEngineStatus */
   gridEngineStatus()
   {
   }
   
   public Map<String, JobStatus> getStatus() throws JobControlException {
      try
      {
         String command = STATUS_COMMAND;
         List<String> commands = new ArrayList<String>(Arrays.asList(command.split("\\s+")));
         ProcessBuilder builder = new ProcessBuilder();
         JAXBContext jc = JAXBContext.newInstance ("org.srs.jobcontrol.gridEngine.qstat");
         Unmarshaller um = jc.createUnmarshaller();
         
         builder.command(commands);
         Process process = builder.start();
         OutputProcessor output = new OutputProcessor(process.getErrorStream(),logger);         
         final JobInfo ji = (JobInfo)um.unmarshal(process.getInputStream());
         process.waitFor();
         output.join();
         int rc = process.exitValue();
         if (rc != 0) {
              throw new JobControlException("Process failed, rc="+rc);
          }
         
         Map<String,JobStatus> map = new HashMap<String,JobStatus>();
         // use a HashMap to assign the jobstatus with each job
        evaluateList( (List<JobListT>) ji.getQueueInfo().get( 0 ).getJobList(), map );
        evaluateList( ji.getJobInfo().get( 0 ).getJobList(), map );
   
        return map;
      }
      catch (IOException x)
      {
         throw new JobControlException("IOException during job submission",x);
      }
      catch (InterruptedException x)
      {
         throw new JobControlException("Job submission interrupted",x);
      }
      catch (JAXBException ex) 
      {
         throw new JobControlException("Error with JAXB deserialization, format changed?",ex);
      }
   }
   
    public void evaluateList(List<JobListT> jobList, Map<String, JobStatus> jobMap){
        if(jobList == null){
            return;
        }
        for(JobListT jlt: jobList){
            String job_id = String.valueOf( jlt.getJBJobNumber() );
            CommonJobStatus stat = new CommonJobStatus();
            stat.setId( job_id );
            if(jlt.getCpuUsage() != null){
                stat.setCpuUsed( Math.round( jlt.getCpuUsage() ) );
            }
            if(jlt.getMemUsage() != null){
                stat.setMemoryUsed( Math.round( jlt.getMemUsage() ) );
            }
            if(jlt.getQueueName() != null){
                String sge_queue_name = jlt.getQueueName();
                String[] splits = sge_queue_name.split( "@" );
                if(splits.length == 2){
                    stat.setHost( splits[1].replace( ".in2p3.fr", "" ) );
                    stat.setQueue( splits[0] );
                } else {
                    stat.setHost( jlt.getQueueName() );
                }
            }
            if(jlt.getIoUsage() != null){
                stat.setSwapUsed( Math.round( jlt.getIoUsage() ) );
            }
            if(jlt.getJBSubmissionTime() != null){
                XMLGregorianCalendar submit_time = jlt.getJBSubmissionTime();
                Date submit_date = submit_time.toGregorianCalendar().getTime();
                stat.setSubmitted( submit_date );
            }
            if(jlt.getJBOwner() != null){
                stat.setUser( jlt.getJBOwner() );
            }
            if(jlt.getStateAttribute() != null){
                stat.setStatus( gridEngineStatus.toStatus( jlt.getStateAttribute() ) );
            }
            if(jlt.getJBProject() != null){
                stat.setComment( "project: " + jlt.getJBProject() );
            }
            // assemble the hashmap
            jobMap.put( job_id, stat );
        }
    }
   
   protected static JobStatus.Status toStatus(String status)
   {
      System.out.println("status found=" + status);
      if      ("pending".equals(status)) return JobStatus.Status.PENDING;
      else if ("running".equals(status)) return JobStatus.Status.RUNNING;
      else if ("zombie".equals(status)) return JobStatus.Status.DONE;
      else return JobStatus.Status.UNKNOWN;
   }
      
}
