package org.glast.jobcontrol;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the object that gets sent across the wire to the server which
 * submits the batch jobs.
 *
 * @author tonyj
 */
public class Job implements Serializable
{
   public static enum Priority { LOWEST, LOW, NORMAL, HIGH, HIGHEST };
   private Map<String,String> files = new HashMap<String,String>();
   private Map<String,String> env = new HashMap<String,String>();
   private List<String> arguments = new ArrayList<String>();
   private Date start;
   private Priority priority = Priority.NORMAL;
   private String command;
   private String user;
   private int maxCPU;
   private String architecture;
   private String currentDirectory;
   private String logFile;
   private int maxMemory;
   private String extraOptions;
   
   static final long serialVersionUID = -362830341176400263L;
   
   public String getCommand()
   {
      return command;
   }
   
   public Map<String, String> getFiles()
   {
      return files;
   }
   
   public void setFiles(Map<String, String> files)
   {
      this.files = files;
   }
   
   public Map<String, String> getEnv()
   {
      return env;
   }
   
   public void setEnv(Map<String, String> env)
   {
      this.env = env;
   }
   
   public List<String> getArguments()
   {
      return arguments;
   }
   
   public void setArguments(List<String> arguments)
   {
      this.arguments = arguments;
   }
   
   public Date getStart()
   {
      return start;
   }
   
   public void setStart(Date start)
   {
      this.start = start;
   }
   
   public Priority getPriority()
   {
      return priority;
   }
   
   public void setPriority(Priority priority)
   {
      this.priority = priority;
   }
   
   public void setCommand(String command)
   {
      this.command = command;
   }
   
   public String getUser()
   {
      return user;
   }
   
   public void setUser(String user)
   {
      this.user = user;
   }
   
   public int getMaxCPU()
   {
      return maxCPU;
   }
   /**
    * Set max CPU for batch job (in seconds)
    */
   public void setMaxCPU(int maxCPU)
   {
      this.maxCPU = maxCPU;
   }
   
   public int getMaxMemory()
   {
      return maxMemory;
   }
   /**
    * Set max memory for batch job (in kB)
    */
   public void setMaxMemory(int maxMemory)
   {
      this.maxMemory = maxMemory;
   }
   
   public String getArchitecture()
   {
      return architecture;
   }
   
   public void setArchitecture(String architecture)
   {
      this.architecture = architecture;
   }
   
   public String getWorkingDirectory()
   {
      return currentDirectory;
   }
   
   public void setWorkingDirectory(String currentDirectory)
   {
      this.currentDirectory = currentDirectory;
   }
   
   public String getLogFile()
   {
      return logFile;
   }
   
   public void setLogFile(String logFile)
   {
      this.logFile = logFile;
   }
   
   public String getExtraOptions()
   {
      return extraOptions;
   }
   
   public void setExtraOptions(String options)
   {
      this.extraOptions = options;
   }
}
