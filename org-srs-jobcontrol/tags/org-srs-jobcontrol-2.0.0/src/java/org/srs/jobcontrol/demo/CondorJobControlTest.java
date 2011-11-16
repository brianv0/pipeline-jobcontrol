package org.srs.jobcontrol.demo;

import org.srs.jobcontrol.Job;
import org.srs.jobcontrol.JobControlClient;
import org.srs.jobcontrol.JobControlException;
import org.srs.jobcontrol.JobSubmissionException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tony Johnson
 */
public class CondorJobControlTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws JobSubmissionException, JobControlException {
        if (args.length < 1) {
            usage();
        }
        String command = args[0];
        List<String> arguments = new ArrayList<String>();
        for (int i = 1; i < args.length; i++) {
            arguments.add(args[i]);
        }

        Job job = new Job();
        job.setCommand(command);
        job.setArguments(arguments);
	job.setWorkingDirectory("users/tjohnson/test");	
        JobControlClient client = new JobControlClient("tjohnson", "smuhpc.smu.edu", 1099, "JobControlService");
        String id = client.submit(job);
        System.out.println("Job " + id + " submitted");
    }

    private static void usage() {
        System.out.println("usage: java " + CondorJobControlTest.class.getName() + "command [args...]");
        System.exit(0);
    }
}
