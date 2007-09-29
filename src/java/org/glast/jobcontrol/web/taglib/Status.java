package org.glast.jobcontrol.web.taglib;

import org.glast.jobcontrol.JobControlClient;
import org.glast.jobcontrol.JobStatus;
import org.glast.jobcontrol.NoSuchJobException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 *
 * @author tonyj
 * @version $Id: Status.java,v 1.3 2007-09-29 19:09:08 tonyj Exp $
 */
public class Status extends SimpleTagSupport
{
   private String id;
   private String var;
   private String serviceName;
   private String host;
   private String user;
   private int port = 1099;
   
   public void doTag() throws JspException
   {  
      JobStatus status;
      try
      {
         JobControlClient client = new JobControlClient(user, host, port, serviceName);
         status = client.status(id);
      }
      catch (NoSuchJobException ex)
      {
         status = null;
      }
      catch (Exception ex)
      {
         throw new JspException("Error getting status for id "+id,ex);
      }
      getJspContext().setAttribute(var,status);
   }

   public void setVar(String value)
   {
      this.var = value;
   }
   public void setId(String id)
   {
      this.id = id;
   }
   public void setHost(String host)
   {
      this.host = host;
   }
   public void setUser(String user)
   {
      this.user = user;
   }
   public void setServiceName(String serviceName)
   {
      this.serviceName = serviceName;
   }
   public void setPort(int port)
   {
      this.port = port;
   }
}