package org.glast.jobcontrol.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.glast.jobcontrol.JobControl;
import org.glast.jobcontrol.JobSubmissionException;

/**
 *
 * @author tonyj
 */
public abstract class JobControlService implements JobControl
{
   protected static final Logger logger = Logger.getLogger("org.glast.jobcontrol");
   protected final int[] retryDelays = { 1000, 2000, 4000, 8000, 0 };
   protected static final Pattern tokenizer = Pattern.compile("\\s*(?:\"([^\"]*)\"|(\\S+))");
   
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
}
