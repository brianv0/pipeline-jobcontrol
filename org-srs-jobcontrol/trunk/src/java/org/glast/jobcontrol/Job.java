package org.glast.jobcontrol;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
   private Set<Integer> runAfter;
   private Date start;
   private Priority priority = Priority.NORMAL;
   private String command;
   private int maxCPU;
   private String architecture;
   private String currentDirectory;
   private String logFile;
   private int maxMemory;
   private String extraOptions;
   private String name;
   
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
   /**
    * Force this job to run after the specified job has completed.
    * This method can be called multiple times to force the job to run
    * after a set of other jobs.
    */
   public void addRunAfter(int jobID)
   {
      if (runAfter == null) runAfter = new HashSet<Integer>();
      runAfter.add(jobID);
   }
   
   public Set<Integer> getRunAfter()
   {
      return runAfter == null ? Collections.EMPTY_SET : runAfter;
   }
   /**
    * Returns the job name, or <code>null</code> if it is not explicitly set.
    */
   public String getName()
   {
      return name;
   }
   /**
    * Sets the name of the submitted job. If not set (or set to <code>null</code>) the job
    * name will be set arbitrarily by the job submission system.
    */
   public void setName(String name)
   {
      this.name = name;
   }
}
