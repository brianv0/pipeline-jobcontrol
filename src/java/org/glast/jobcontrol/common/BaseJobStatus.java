package org.glast.jobcontrol.common;

import org.glast.jobcontrol.*;
import org.glast.jobcontrol.JobStatus.Status;
import java.io.Serializable;
import java.util.Date;

/**
 * Summary of the status of a job.
 * @author tonyj
 */

public class BaseJobStatus implements Serializable, JobStatus
{
   static final long serialVersionUID = 6311542340392104385L;
   
   private String id;
   private Status status;
   private String host;
   private String queue;
   private int cpuUsed;
   private int memoryUsed;
   private int swapUsed;
   private Date submitted;
   private Date started;
   private Date ended;
   private String comment;
   private String user;
   
   public String getId()
   {
      return id;
   }
   
   public Status getStatus()
   {
      return status;
   }
   
   public String getHost()
   {
      return host;
   }
   
   public String getQueue()
   {
      return queue;
   }
   
   public int getCpuUsed()
   {
      return cpuUsed;
   }
   
   public int getMemoryUsed()
   {
      return memoryUsed;
   }
   
   public void setMemoryUsed(int memoryUsed)
   {
      this.memoryUsed = memoryUsed;
   }
   
   public int getSwapUsed()
   {
      return swapUsed;
   }
   
   public Date getSubmitted()
   {
      return submitted;
   }
   
   public Date getStarted()
   {
      return started;
   }
   
   public Date getEnded()
   {
      return ended;
   }
   
   public String getComment()
   {
      return comment;
   }
   
   public void setId(String id)
   {
      this.id = id;
   }
   
   public void setStatus(Status status)
   {
      this.status = status;
   }
   
   public void setHost(String host)
   {
      this.host = host;
   }
   
   public void setQueue(String queue)
   {
      this.queue = queue;
   }
   
   public void setCpuUsed(int cpuUsed)
   {
      this.cpuUsed = cpuUsed;
   }
   
   public void setSwapUsed(int swapUsed)
   {
      this.swapUsed = swapUsed;
   }
   
   public void setSubmitted(Date submitted)
   {
      this.submitted = submitted;
   }
   
   public void setStarted(Date started)
   {
      this.started = started;
   }
   
   public void setEnded(Date ended)
   {
      this.ended = ended;
   }
   
   public void setComment(String comment)
   {
      this.comment = comment;
   }
   
   public String getUser()
   {
      return user;
   }
   public void setUser(String user)
   {
      this.user = user;
   }
   public String toString()
   {
      return "Job "+id+" "+status+" "+host;
   }
}
