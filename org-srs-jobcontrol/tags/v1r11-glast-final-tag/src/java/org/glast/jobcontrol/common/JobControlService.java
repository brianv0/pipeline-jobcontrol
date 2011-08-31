package org.glast.jobcontrol.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.glast.jobcontrol.JobControl;
import org.glast.jobcontrol.JobStatus;
import org.glast.jobcontrol.JobSubmissionException;

/**
 *
 * @author tonyj
 */
public abstract class JobControlService implements JobControl, JobControlServiceMBean
{
   protected static final Logger logger = Logger.getLogger("org.glast.jobcontrol");
   protected final int[] retryDelays = { 1000, 2000, 4000, 8000, 0 };
   protected static final Pattern tokenizer = Pattern.compile("\\s*(?:\"([^\"]*)\"|(\\S+))");
   private static final boolean obliterate = Boolean.getBoolean("org.glast.jobcontrol.obliterate");
   private final Date startTime = new Date();
   protected AtomicInteger nSubmitted = new AtomicInteger();
   protected long lastSuccessfulJobSubmissionTime;
   protected long lastFailedJobSubmissionTime;
   
   /** Creates a new instance of JobControlService */
   protected JobControlService()
   {
   }
   
   protected void archiveOldWorkingDir(final File dir, final String archivePrefix, List<Runnable> undoList) throws JobSubmissionException
   {
      File[] oldFiles = dir.listFiles();
      if (oldFiles.length > 0)
      {
         File archiveDir = new File(dir,"archive/"+archivePrefix);
         if (!archiveDir.exists())
         {
            boolean rc = archiveDir.mkdirs();
            if (!rc) throw new JobSubmissionException("Could not create archive directory "+archiveDir);
            undoList.add(new DeleteFile(archiveDir));
         }
         
         for(final File oldFile : oldFiles)
         {
            if (!oldFile.getName().startsWith("archive") || !oldFile.isDirectory())
            {
               final File newFile = new File(archiveDir,oldFile.getName());
               boolean rc = oldFile.renameTo(newFile);
               if (!rc) throw new JobSubmissionException("Could not move file to archive directory: "+oldFile);
               undoList.add(new Runnable()
               {
                  public void run()
                  {
                     newFile.renameTo(oldFile);
                  }
               });
            }
         }
      }
   }
   
   protected String sanitize(String option)
   {
      return option.replaceAll("\\s+","_");
   }
   
   
   protected String toFullCommand(List<String> bsub)
   {
      StringBuilder builder = new StringBuilder();
      for(String token : bsub)
      {
         if (builder.length()>0) builder.append(' ');
         boolean needQuotes = token.contains(" ");
         if (needQuotes) builder.append('"');
         builder.append(token);
         if (needQuotes) builder.append('"');
      }
      return builder.toString();
   }
   
   
   protected List<String> tokenizeExtraOption(String string)
   {
      List<String> result = new ArrayList<String>();
      Matcher matcher = tokenizer.matcher(string);
      while (matcher.find())
      {
         result.add(matcher.group(2) == null ? matcher.group(1) : matcher.group(2));
      }
      return result;
   }
   public static class DeleteFile implements Runnable
   {
      private File file;
      public DeleteFile(File file)
      {
         this.file = file;
      }
      public void run()
      {
         file.delete();
      }
   }
   
   
   protected void storeFiles(final File dir, final Map<String, String> files, final List<Runnable> undoList) throws JobSubmissionException, IOException
   {
      if (files != null)
      {
         for(Map.Entry<String, String> entry : files.entrySet())
         {
            File file = new File(dir,entry.getKey());
            if (file.exists())
            {
               if (obliterate)
               {
                  logger.log(Level.WARNING,"File "+file+" obliterated");
               }
               else
               {
                  throw new JobSubmissionException("File "+file+" already exists, not replaced");
               }
            }
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            undoList.add(new DeleteFile(file));
            writer.print(entry.getValue());
            writer.close();
         }
      }
   }
   
   protected Map<String, Integer> computeJobCounts(Map<String,JobStatus> statii)
   {
      Map<String,Integer> result = new HashMap<String,Integer>();
      for (JobStatus status : statii.values())
      {
         String stat = status.getStatus().toString();
         Integer value = result.get(stat);
         if (value == null)
         {
            result.put(stat,Integer.valueOf(1));
         }
         else
         {
            result.put(stat,Integer.valueOf(value.intValue()+1));
         }
      }
      return result;
   }
   public Date getStartTime()
   {
      return startTime;
   }
   
   public int getJobSubmissionCount()
   {
      return nSubmitted.get();
   }
   
   public Date getLastSuccessfulJobSubmissionTime()
   {
      return new Date(lastSuccessfulJobSubmissionTime);
   }
   
   public Date getLastFailedJobSubmissionTime()
   {
      return new Date(lastFailedJobSubmissionTime);
   }
}
