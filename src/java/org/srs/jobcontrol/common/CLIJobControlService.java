package org.srs.jobcontrol.common;

import java.util.List;
import org.srs.jobcontrol.JobSubmissionException;
import org.srs.jobcontrol.OutputProcessor;

/**
 *
 * @author bvan
 */
public abstract class CLIJobControlService extends JobControlService {

    /**
     * A call to this method should extract the Job ID of a newly-submitted job from the a list
     * (typically suppled from OutputProcessor.getResult() ) This method is called from
     * processSubmittedJobOutput
     *
     * @param result
     * @return
     * @throws JobSubmissionException
     */
    public abstract String extractJobId(List<String> result) throws JobSubmissionException;

    /**
     * Takes the OutputProcessor and process return code, and tries to return a String
     * representation of the job ID. If it fails, it throws a JobSubmissionException
     * @param output
     * @param rc
     * @return
     * @throws JobSubmissionException
     */
    protected String processSubmittedJobOutput(OutputProcessor output, int rc)
            throws JobSubmissionException{
        List<String> result = output.getResult();
        if(rc != 0){
            StringBuilder message = new StringBuilder( "Process failed rc=" + rc );
            if(!result.isEmpty()) {
                message.append( " output was:" );
            }
            for(String line: result){
                message.append( '\n' ).append( line );
            }
            throw new JobSubmissionException( message.toString() );
        }
        if(output.getStatus() != null){
            String msg = "Unexpected exception processing batch submission output";
            throw new JobSubmissionException( msg, output.getStatus() );
        }

        if(result.isEmpty()){
            throw new JobSubmissionException( "Unexpected output length " + output.getResult().
                    size() );
        }

        return extractJobId( output.getResult() );
    }

}
