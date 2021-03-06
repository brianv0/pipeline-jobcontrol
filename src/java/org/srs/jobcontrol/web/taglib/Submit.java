package org.srs.jobcontrol.web.taglib;

import org.srs.jobcontrol.Job;
import org.srs.jobcontrol.JobControlClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 *
 * @author tonyj
 * @version $Id: Submit.java,v 1.4 2007-09-29 19:09:08 tonyj Exp $
 */

public class Submit extends SimpleTagSupport implements DynamicAttributes
{
   private String var;
   private String dir;
   private String command;
   private int time = 90;
   private int memory = 0;
   private String extraOptions;
   private String serviceName;
   private String host;
   private String user;
   private int port = 1099;
   
   private Map<String,String> env = new HashMap<String,String>();
   private Map<String,String> files = new HashMap<String,String>();
   
   /**Called by the container to invoke this tag.
    * The implementation of this method is provided by the tag library developer,
    * and handles all tag processing, body iteration, etc.
    */
   public void doTag() throws JspException
   {
      try
      {
         JspFragment fragment = getJspBody();
         if (fragment != null) fragment.invoke(null);
         
         Job job = new Job();
         job.setFiles(files);
         job.setCommand(command);
         job.setWorkingDirectory(dir);
         job.setMaxCPU(time);
         if (memory != 0) job.setMaxMemory(memory);
         if (extraOptions != null) job.setExtraOptions(extraOptions);
         
         job.setEnv(env);
         JobControlClient client = new JobControlClient(user, host, port, serviceName);
         String id = client.submit(job);
         if (var != null) getJspContext().setAttribute(var,id);
      }
      catch (Exception ex)
      {
         throw new JspException("Error submitting job",ex);
      }
   }
   void addFile(String name, String file)
   {
      files.put(name,file);
   }
   public void setVar(String value)
   {
      this.var = value;
   }
   public void setCommand(String value)
   {
      this.command = value;
   }
   public void setDir(String value)
   {
      this.dir = value;
   }
   public void setExtraOptions(String value)
   {
      this.extraOptions = value;
   }
   
   public void setEnvVariables(HashMap<String,String> envVariables)
   {
      env.putAll(envVariables);
   }
   
   public void setTime(int value)
   {
      this.time = value;
   }
   public void setMemory(int value)
   {
      this.memory = value;
   }
   public void setDynamicAttribute(String uri, String name, Object value) throws JspException
   {
      env.put(name, value == null ? "" : value.toString());
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
