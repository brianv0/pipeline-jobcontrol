package org.srs.jobcontrol.simulator;

import com.healthmarketscience.rmiio.GZIPRemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamServer;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import org.srs.jobcontrol.Job;
import org.srs.jobcontrol.JobControl;
import org.srs.jobcontrol.JobControlException;
import org.srs.jobcontrol.JobStatus;
import org.srs.jobcontrol.JobSubmissionException;
import org.srs.jobcontrol.NoSuchJobException;
import java.util.concurrent.atomic.AtomicInteger;
import org.srs.jobcontrol.common.CommonJobStatus;

/**
 *
 * @author tonyj
 */
public class DummyJobControlService implements JobControl {

    private static final Logger logger = Logger.getLogger("org.srs.jobcontrol.simulator");
    private Map<String, FakeJob> jobs = Collections.synchronizedMap(new WeakHashMap<String, FakeJob>());
    private static AtomicInteger nextJobId = new AtomicInteger(1);
    private JobExecutor executor;

    public DummyJobControlService(JobExecutor executor) {
        this.executor = executor;
    }

    public void start() throws RemoteException {
        JobControl stub = (JobControl) UnicastRemoteObject.exportObject(this, 0);

        String user = System.getProperty("user.name");
        // Bind the remote object's stub in the registry
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("JobControlService-" + user, stub);
        DummyJobControlService.logger.info("Server ready, user " + user);
    }

    @Override
    public String submit(Job job) throws RemoteException, JobSubmissionException, JobControlException {
        String id = generateJobId();
        FakeJob fakeJob = new FakeJob(id, job);
        jobs.put(id, fakeJob);
        executor.submit(fakeJob);
        return id;
    }

    @Override
    public JobStatus status(String id) throws RemoteException, NoSuchJobException, JobControlException {
        FakeJob job = jobs.get(id);
        if (job == null) {
            throw new NoSuchJobException(id);
        }
        return newStatus(job);
    }
    
    private CommonJobStatus newStatus(FakeJob job){
        synchronized(job) {
            CommonJobStatus status = new CommonJobStatus();
            status.setComment( "" );
            status.setHost( "dummyHost" );
            status.setSubmitted( job.getSubmitted() );
            status.setStarted( job.getStarted() );
            status.setEnded( job.getEnded() );
            status.setUser( job.getUser() );
            status.setStatus( job.getStatus() );
            return status;
        }
    }
    @Override
    public void cancel(String id) throws RemoteException, NoSuchJobException, JobControlException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String generateJobId() {
        return String.valueOf(nextJobId.getAndIncrement());
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

    public Map<String, JobStatus> arrayStatus(List<String> jobIDs) throws RemoteException, JobControlException{
        LinkedHashMap<String, JobStatus> map = new LinkedHashMap<String, JobStatus>();
        for(String jobID: jobIDs){
            FakeJob job = jobs.get(jobID);
            if(job == null){
                map.put( jobID, null );
            } else {
                map.put( jobID, newStatus( job ));
            }
        }
        return map;
    }

}
