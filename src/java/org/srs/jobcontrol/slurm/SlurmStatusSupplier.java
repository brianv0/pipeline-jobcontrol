
package org.srs.jobcontrol.slurm;

import com.google.common.base.Supplier;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.srs.jobcontrol.JobControlException;
import org.srs.jobcontrol.JobStatus;
import org.srs.jobcontrol.OutputProcessor;
import org.srs.jobcontrol.common.CommonJobStatus;

/**
 * Job status supplier for Slurm using both squeue and sacct.
 * @author bvan
 */
public class SlurmStatusSupplier implements Supplier<Map<String, JobStatus>> {
    
    private final String username;
    private final static List<String> STATUS_COMMAND = Arrays.asList(
            "/opt/slurm/default/bin/sacct",
            "-o",
            "jobname,state,jobid,submit,start,end,nodelist,cputimeraw,maxvmsize,exitcode",
            "-P",
            "-u"
    );
    
    private final static Logger LOGGER = Logger.getLogger("org.srs.jobcontrol");
            
    public static class StatusField {
        public static int JOBNAME = 0;
        public static int STATE = 1;
        public static int JOBID = 2;
        public static int SUBMIT = 3;
        public static int START = 4;
        public static int END = 5;
        public static int NODELIST = 6;
        public static int CPUTIMERAW = 7;
        public static int MAXVMSIZE = 8;
        public static int EXITCODE = 9;
    };
    
    
    
    public SlurmStatusSupplier(String username){
        this.username = username;
    }
    
    @Override
    public Map<String, JobStatus> get(){

        List<String> commands = new ArrayList<>();

        commands.addAll(STATUS_COMMAND);
        commands.add(username);

        ProcessBuilder builder = new ProcessBuilder();
        builder.redirectErrorStream(true);
        builder.command(commands);
        Process process;
        try {
            process = builder.start();
            OutputProcessor output = new OutputProcessor(process.getInputStream(), LOGGER);
            process.waitFor();
            output.join();
            int rc = process.exitValue();
            if(rc != 0){
                throw new RuntimeException(new JobControlException("Process failed, rc=" + rc));
            }
            if(output.getStatus() != null){
                throw output.getStatus();
            }

            List<String> result = output.getResult();

            return parseSacctOutput(result);
        } catch(IOException | InterruptedException ex) {
            throw new RuntimeException(new JobControlException("Unable to query jobs", ex));
        }
        
        
    }    

    public static HashMap<String, JobStatus> parseSacctOutput(List<String> lines){
        HashMap<String, JobStatus> jobStatii = new HashMap<>();
        for(int i = 1; i < lines.size(); i++){
            String line = lines.get(i);
            String[] fields = line.split("\\|");
            CommonJobStatus qs = new CommonJobStatus();
            qs.setId(fields[StatusField.JOBID]);
            qs.setHost(fields[StatusField.NODELIST]);
            qs.setComment(fields[StatusField.JOBNAME]);
            qs.setStatus(getStatus(fields[StatusField.STATE]));
            try {
                qs.setCpuUsed(Integer.parseInt(fields[StatusField.CPUTIMERAW]));
            } catch (NumberFormatException ex){
                qs.setCpuUsed(-1);
            }
            jobStatii.put(qs.getId(), qs);
        }
        return jobStatii;
    }
    
    public static JobStatus.Status getStatus(String status){
        JobStatus.Status stat;
        switch(status.toUpperCase()){
            case "PENDING":
                stat = JobStatus.Status.WAITING;
                break;
            case "RUNNING":
                stat = JobStatus.Status.RUNNING;
                break;
            case "COMPLETED":
                stat = JobStatus.Status.DONE;
                break;
            case "CANCELLED":
            case "FAILED":
            case "TIMEOUT":
            case "PREEMPTED":
            case "NODE_FAIL":
                stat = JobStatus.Status.FAILED;
                break;
            default:
                /* STOPPED,  SUSPENDED */
                stat = JobStatus.Status.UNKNOWN;
                break;
        }
        return stat;
    }

}
