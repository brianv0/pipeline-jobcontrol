package org.glast.jobcontrol.lsf;

import org.glast.jobcontrol.*;
import org.glast.jobcontrol.JobStatus.Status;
import java.io.Serializable;
import java.util.Date;

/**
 * Summary of the status of a job.
 * @author tonyj
 */

class LSFJobStatus implements Serializable, JobStatus
{
   static final long serialVersionUID = 6311542340392104385L;
   
   private int id;
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
   
   public int getId()
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
   
   void setId(int id)
   {
      this.id = id;
   }
   
   void setStatus(Status status)
   {
      this.status = status;
   }
   
   void setHost(String host)
   {
      this.host = host;
   }
   
   void setQueue(String queue)
   {
      this.queue = queue;
   }
   
   void setCpuUsed(int cpuUsed)
   {
      this.cpuUsed = cpuUsed;
   }
   
   void setSwapUsed(int swapUsed)
   {
      this.swapUsed = swapUsed;
   }
   
   void setSubmitted(Date submitted)
   {
      this.submitted = submitted;
   }
   
   void setStarted(Date started)
   {
      this.started = started;
   }
   
   void setEnded(Date ended)
   {
      this.ended = ended;
   }
   
   void setComment(String comment)
   {
      this.comment = comment;
   }
   
   public String getUser()
   {
      return user;
   }
   void setUser(String user)
   {
      this.user = user;
   }
   public String toString()
   {
      return "Job "+id+" "+status+" "+host;
   }
}
