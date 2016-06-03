package org.srs.jobcontrol;

import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the main class clients should use to interact with the job submission system.
 * @author Tony Johnson
 */
public class JobControlClient
{
   protected static final Logger logger = Logger.getLogger("org.srs.jobcontrol");
   private final String user;
   private final String host; 
   private final String serviceName;
   private final int port;
   private JobControl jcReference;
   
   public JobControlClient()
   {
      this(null);
   }
   public JobControlClient(String user)
   {
     this(user,null);
   }
   public JobControlClient(String user, String host)
   {
      this(user,host,1099);
   }
   public JobControlClient(String user, String host, int port)
   {
      this(user,host,port,null);
   }
   public JobControlClient(String user, String host, int port, String serviceName)
   {
      this.user = user == null ? "glast" : user;
      this.host = host == null ? "glast-jobcontrol01.slac.stanford.edu" : host;
      this.port = port;
      this.serviceName = serviceName == null ? "JobControlService" : serviceName;
   }
   
    private JobControl getJobControlRef() throws NotBoundException, RemoteException{
        if(jcReference == null){
            logger.log( Level.INFO, "Connecting to job control service " + serviceName + " at " + host );
            Registry registry = LocateRegistry.getRegistry( host, port );
            jcReference = (JobControl) registry.lookup( serviceName + "-" + user );
        }
        return jcReference;
    }

    /**
     * Submit a job.
     * @param job The job to be submitted
     * @throws org.srs.jobcontrol.JobSubmissionException Thrown if an error occurs during job submission
     * @return The job ID
     * @throws org.srs.jobcontrol.JobControlException
     */
   public String submit(Job job) throws JobSubmissionException, JobControlException {
       String jobId = null;
       for(int retry = 0; retry < 2; retry++){
           try {
               jobId = getJobControlRef().submit( job );
               break;
           } catch(RemoteException | NotBoundException ex) {
               checkException( ex, retry );
           }
       }
       return jobId;
   }
   
    /**
     * Get a map of statuses from a list of jobs of interest
     * @param jobIDs List of all jobIDs we are interested in.
     * @throws org.srs.jobcontrol.NoSuchJobException Thrown if the specified ID is unknown, or if any other error occurs.
     * @return The jobs status
     * @throws org.srs.jobcontrol.JobControlException
     */
    public Map<String, JobStatus> arrayStatus(List<String> jobIDs) throws NoSuchJobException, JobControlException{
        Map<String, JobStatus> smap = null;
        for(int retry = 0; retry < 2; retry++){
            try {
                smap = getJobControlRef().arrayStatus( jobIDs );
                break;
            } catch(RemoteException | NotBoundException ex) {
                checkException( ex, retry );
            }
        }
        return smap;
    }
   
    /**
     * Get the status of a job.
     * @param jobID The jobID for which status should be returned
     * @throws org.srs.jobcontrol.NoSuchJobException Thrown if the specified ID is unknown, or if any other error occurs.
     * @return The jobs status
     * @throws org.srs.jobcontrol.JobControlException
     */
   public JobStatus status(String jobID) throws NoSuchJobException, JobControlException
   {
       JobStatus stat = null;
       for(int retry = 0; retry < 2; retry++){
           try {
               stat = getJobControlRef().status( jobID );
               break;
           } catch(RemoteException | NotBoundException ex) {
               checkException( ex, retry );
           }
       }
       return stat;
   }
   
    /**
     * Attempt to get a remote file as a string. Files should be small, otherwise you the stream.
     * @param spID String in the form of "streamPk:processInstancePk"
     * @param workingDir Working dir of this process instance
     * @param fileName name of file relative to workingDir
     * @return A string representation of the file.
     * @throws FileNotFoundException
     * @throws TimeoutException
     * @throws JobControlException 
    */
   public String getFile(String spID, File workingDir, String fileName) 
           throws FileNotFoundException, TimeoutException, JobControlException
   {
       String fContent = null;
       for(int retry = 0; retry < 2; retry++){
           try {
               fContent = getJobControlRef().getFile( spID, workingDir, fileName );
               break;
           } catch(RemoteException | NotBoundException ex) {
               checkException( ex, retry );
           }
       }
      return fContent;
   }
   
    /**
     * Get an InputStream from a remote file to be transferred over RMI.
     * You should do everything you can to close this InputStream when you are done.
     * 
     * @param spID String in the form of "streamPk:processInstancePk"
     * @param workingDir Working dir of this process instance
     * @param fileName name of file relative to workingDir
     * @return A InputStream to a remote file
     * @throws java.io.IOException
     * @throws org.srs.jobcontrol.JobControlException
     */
   public InputStream getFileStream(String spID, File workingDir, String fileName) 
           throws IOException, JobControlException {
       InputStream is = null;
       for(int retry = 0; retry < 2; retry++){
           try {
               is = RemoteInputStreamClient.wrap(
                       getJobControlRef().getFileStream( spID, workingDir, fileName ) );
               break;
           } catch(RemoteException | NotBoundException ex) {
               checkException( ex, retry );
           }
       }
       return is;
   }
   
   /**
    * Cancels a job. If the job is already running it will be killed.
     * @param jobID
     * @throws org.srs.jobcontrol.NoSuchJobException
    */
    public void cancel(String jobID) throws NoSuchJobException, JobControlException {
        for(int retry = 0; retry < 2; retry++){
            try {
                getJobControlRef().cancel( jobID );
                break;
            } catch(RemoteException | NotBoundException ex) {
                checkException( ex, retry );
            }
            return;
        }
   }
    
   /*
      Check the exception. If it's appropriate, throw it. If not, refresh the reference so we
      can try again.
    */
   private void checkException(Exception ex, int retry) throws JobControlException {
       if(ex instanceof NotBoundException){
           throw new JobControlException("Server not running while",ex);
       } else if(ex instanceof RemoteException){
           try{
               // Reset the reference, try to get the reference again.
               logger.log( Level.WARNING, "Reference to Service likely died. Attempting to renew ref", ex);
               synchronized(this){
                   jcReference = null;
                   getJobControlRef();
               }
           } catch(NotBoundException | RemoteException e){}
           if(retry == 1){
               logger.log( Level.SEVERE, "Unable to contact job control daemon", ex.getCause());
               throw new JobControlException("Remote Exception performing operation", ex.getCause());
           }
       }
   }
}
