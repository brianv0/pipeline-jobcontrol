package org.glast.jobcontrol.web.taglib;

import org.glast.jobcontrol.JobControlClient;
import org.glast.jobcontrol.JobStatus;
import org.glast.jobcontrol.NoSuchJobException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 *
 * @author tonyj
 * @version $Id: Status.java,v 1.2 2007-05-14 20:43:17 tonyj Exp $
 */
public class Status extends SimpleTagSupport
{
   private String id;
   private String var;
   
   public void doTag() throws JspException
   {  
      JobStatus status;
      try
      {
         JobControlClient client = new JobControlClient();
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
}