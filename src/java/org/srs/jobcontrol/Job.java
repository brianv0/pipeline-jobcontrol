package org.srs.jobcontrol;

import java.io.Serializable;
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
   private Priority priority = Priority.NORMAL;
   private String command;
   private int maxCPU;
   private String currentDirectory;
   private String logFile;
   private int maxMemory;
   private String extraOptions;
   private String name;
   private String archiveOldWorkingDir;
   
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
   
   public String getArchiveOldWorkingDir()
   {
      return archiveOldWorkingDir;
   }
   
   public void setArchiveOldWorkingDir(String archiveName)
   {
      this.archiveOldWorkingDir = archiveName;
   }
}
