package org.srs.jobcontrol.common;

import com.healthmarketscience.rmiio.GZIPRemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamServer;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.srs.jobcontrol.Job;
import org.srs.jobcontrol.JobControl;
import org.srs.jobcontrol.JobControlException;
import org.srs.jobcontrol.JobStatus;
import org.srs.jobcontrol.JobSubmissionException;
import org.srs.jobcontrol.NoSuchJobException;

/**
 *
 * @author tonyj
 */
public abstract class JobControlService implements JobControl, JobControlServiceMBean
{
   protected static final Logger logger = Logger.getLogger("org.srs.jobcontrol");
   protected final int[] retryDelays = { 1000, 2000, 4000, 8000, 0 };
   protected static final Pattern tokenizer = Pattern.compile("\\s*(?:\"([^\"]*)\"|(\\S+))");
   private static final boolean obliterate = Boolean.getBoolean("org.srs.jobcontrol.obliterate");
   private final Date startTime = new Date();
   protected AtomicInteger nSubmitted = new AtomicInteger();
   protected long lastSuccessfulJobSubmissionTime;
   protected long lastFailedJobSubmissionTime;
   Map<String,JobStatus> cacheMap = new HashMap<String,JobStatus>();
   private long timeStamp;
   private final static long CACHE_TIME = 60*1000; // Needed to avoid excessive calls to bjobs
   
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
   
   /**
    * Summary will return the pipeline_summary file as a string representation.
    * It must be read and returned within 5 seconds, otherwise we will time
    * out the thread and return an exception, in which case a job will be 
    * terminated.
    * @param spID String in the form of "streamPK:piPK"
    * @param workingDir Working directory of the job
    * @param fileName valid file under the canonical path of workingDir
    * @return String representation of the getFile.
    * @throws FileNotFoundException
    * @throws TimeoutException
    * @throws JobControlException 
    */
    public String getFile(String spID, File workingDir, String fileName) 
            throws FileNotFoundException, TimeoutException, JobControlException {
        return getFileWithTimeout(spID, workingDir, fileName, 5);
    }

    /**
     * Return a GZIPRemoteInputStream for large file access over RMI.
     * 
     * @param spID
     * @param workingDir
     * @param fileName
     * @return Stream representation of the full file name
     * @throws JobControlException
     * @throws IOException 
     */
    public RemoteInputStream getFileStream(String spID, File workingDir, String fileName)
            throws JobControlException, IOException {
        File targetFile = new File(workingDir, fileName);
        validateAccess( spID, workingDir, targetFile );

        RemoteInputStreamServer istream = null;
        try {
            istream = new GZIPRemoteInputStream( new BufferedInputStream(
                    new FileInputStream( targetFile ) ) );
            // export the final stream for returning to the client
            RemoteInputStream result = istream.export();
            istream = null;
            return result;
        } finally {
            if(istream != null) {
                istream.close();
            }
        }
    }
   
    private String getFileWithTimeout(String spID, File workingDir, String fileName, int timeout) 
            throws FileNotFoundException, TimeoutException, JobControlException {
        // Validate files
        final File targetFile = new File(workingDir, fileName);
        validateAccess(spID, workingDir, targetFile);
        
        
        // Callable so I can set a timeout on reading this file.
        Callable<String> readFile = new Callable<String>(){
            public String call() throws Exception {
                // Keep this in here, because even trying to see if the file is
                // there could take a long time.
                if( !targetFile.exists() ){
                    throw new FileNotFoundException(
                            "Summary file does not exist.");
                }
                FileChannel channel = new FileInputStream(targetFile).getChannel();
                int len = (int) targetFile.length();
                ByteBuffer buf = ByteBuffer.allocate( len );
                // Not sure if read will always fill buffer...
                for(int read = 0; read < len; read += channel.read(buf));
                return new String(buf.array());
            }
        };
        
        // Read, but keep a timeout because we'd rather just kill it than 
        // lock up a thread on the server.
        Future<String> info = 
                Executors.newSingleThreadExecutor().submit( readFile );
        
        try {
            return info.get( timeout , TimeUnit.SECONDS );
        } catch (InterruptedException ex) {
            throw new JobControlException(
                    "Unknown exception occurred when reading summary", ex);
        } catch (ExecutionException ex) {
            if(ex.getCause() instanceof FileNotFoundException){
                throw (FileNotFoundException) ex.getCause();
            }
            throw new JobControlException(
                    "Unknown exception occurred when reading summary", ex);
        }
    }
    
    // Validate access to files. Throw an exception otherwise.
    private void validateAccess(String spID, File workingDir, File maybeChild) 
            throws JobControlException {
        // Validate files
        BufferedReader spid_rd = null;
        try {
            workingDir = workingDir.getCanonicalFile();
            // Check pipeline_spid to verify they are the same
            File spid_file = new File(workingDir, "pipeline_spid");
            spid_rd = new BufferedReader(new FileReader(spid_file));
            if( !spid_rd.readLine().equals( spID ) ){
                throw new SecurityException("The spid is invalid");
            }

            if( !maybeChild.getCanonicalPath().startsWith( workingDir.getCanonicalPath() ) ){
                throw new SecurityException("The filename is invalid");
            }
        } catch (Exception ex) {
            throw new JobControlException("Unable to validate access to file", ex);
        } finally {
            try {spid_rd.close();} catch (Exception e){}
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
         logger.log( Level.WARNING, "Deleting job file: " + file.getAbsolutePath());
         file.delete();
      }
   }
   
   
   protected void storeFiles(final File dir, final Map<String, String> files, final List<Runnable> undoList) throws JobSubmissionException, IOException
   {
      logger.log( Level.FINE, "Storing files in dir " + dir.getAbsolutePath());
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
            logger.log( Level.FINE, "Writing file: " + file.getAbsolutePath());
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
   
    public Map<String, Integer> getJobCounts() {
        try {
            return computeJobCounts( getCachedStatus() );
        } catch (JobControlException x) {
            logger.log(Level.SEVERE, "Error getting job counts", x);
            return null;
        }
    }
   
    /**
     * A call should return an up-to-date (uncached) map of the current running jobs.
     * @return
     * @throws JobControlException 
     */
    public abstract Map<String, JobStatus> getCurrentStatus() throws JobControlException;

    public Map<String, JobStatus> getCachedStatus() throws JobControlException{
        synchronized(this) {
            long now = System.currentTimeMillis();
            boolean updateNeeded = now - timeStamp > CACHE_TIME;
            logger.log( Level.FINE, "status: now={0} timeStamp={1} cache={2} update needed: {3}",
                    new Object[]{now, timeStamp, CACHE_TIME, updateNeeded} );
            if(updateNeeded){
                this.cacheMap = getCurrentStatus();
                this.timeStamp = System.currentTimeMillis();
            }
        }
        return cacheMap;
    }
    
    public Map<String, JobStatus> arrayStatus(List<String> jobIDs) throws JobControlException{
        logger.log(Level.INFO, "auery to arrayStatus");
        LinkedHashMap<String, JobStatus> statii = new LinkedHashMap<String, JobStatus>();
        for(String jobID: jobIDs){
            statii.put( jobID, getCachedStatus().get( jobID) );
        }
        return statii;
    }
    
    public String getStatus(){
        try {
            getCachedStatus();
            return "OK";
        } catch(JobControlException x) {
            logger.log( Level.SEVERE, "Error getting status", x );
            return "Bad " + (x.getMessage());
        }
    }
    
   protected void checkPermission(String ip) throws SecurityException {
      if (!ip.startsWith("134.79") && !ip.startsWith("198.129")) throw new SecurityException();
   }
   
    public JobStatus status(String jobID) throws NoSuchJobException, JobControlException {
        logger.log(Level.FINEST, "In ".concat(getClass().getCanonicalName()) );
        try {
            String ip = RemoteServer.getClientHost();
            logger.log(Level.INFO, "status: "+jobID+" from "+ip);
            checkPermission(ip);
            JobStatus result = getCachedStatus().get(jobID);
            if (result == null) throw new NoSuchJobException("Job id "+jobID);
            return result;
           
        } catch (ServerNotActiveException t) {
            logger.log(Level.SEVERE,"Unexpected error",t);
            throw new JobControlException("Unexpected error",t);
        } catch (NoSuchJobException t) {
            logger.log(Level.SEVERE,"job status failed",t);
            throw t;
        } catch (JobControlException t) {
            logger.log(Level.SEVERE,"job status failed",t);
            throw t;
        }
    }
}