package org.srs.jobcontrol.condor;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.srs.jobcontrol.JobControlException;
import org.srs.jobcontrol.JobStatus;
import org.srs.jobcontrol.OutputProcessor;
import org.srs.jobcontrol.common.CommonJobStatus;

/**
 * Keeps track of job status
 * @author tonyj
 */
class CondorStatus {

    private final static String STATUS_COMMAND = "condor_q -long -format \\n\\nid=%d ClusterId -format \\nrh=%s RemoteHost -format \\nlrh=%s LastRemoteHost -format \\nstatus=%s JobStatus -format \\nrc=%d ExitStatus -format \\nend=%d CompletionDate -format \\nstart=%d JobStartDate -format \\nsubmit=%d QDate -format \\nuser=%s Owner -format \\ncomment=%s HoldReason -submitter ";
    private final static Pattern pattern = Pattern.compile("(\\S+)=(\\S+)");
    private final static Logger logger = Logger.getLogger(CondorStatus.class.getName());
    

    /** Creates a new instance of LSFStatus */
    CondorStatus() {
    }

    Map<String, JobStatus> getStatus() throws JobControlException {
        try {
            String user = System.getProperty("user.name");
            String command = STATUS_COMMAND + user;
            List<String> commands = new ArrayList<String>(Arrays.asList(command.split("\\s+")));
            ProcessBuilder builder = new ProcessBuilder();
            builder.redirectErrorStream(true);
            builder.command(commands);
            Process process = builder.start();
            OutputProcessor output = new OutputProcessor(process.getInputStream(), logger);
            process.waitFor();
            output.join();
            int rc = process.exitValue();
            if (rc != 0) {
                throw new JobControlException("Process failed, rc=" + rc);
            }

            if (output.getStatus() != null) {
                throw output.getStatus();
            }

            List<String> result = output.getResult();
            if (result.size() == 0) {
                throw new JobControlException("Unexpected output length " + result.size());
            }
            logger.info("Status returned " + result.size() + " lines");

            Map<String, JobStatus> map = new HashMap<String, JobStatus>();

            CommonJobStatus stat = null;
            for (int i = 0; i < result.size(); i++) {
                Matcher match = pattern.matcher(result.get(i));
                if (match.matches()) {
                    try {
                        if (stat == null) {
                            stat = new CommonJobStatus();

                            stat.setQueue("unknown");
                        }
                        String key = match.group(1);
                        String value = match.group(2);
                        if ("id".equals(key)) {
                            stat.setId(value);
                        } else if ("rh".equals(key) || "lrh".equals(key)) {
                            stat.setHost(value);
                        } else if ("status".equals(key)) {
                            stat.setStatus(toStatus(value));
                        } else if ("start".equals(key)) {
                            stat.setStarted(toDate(value));
                        } else if ("end".equals(key)) {
                            stat.setEnded(toDate(value));
                        } else if ("submit".equals(key)) {
                            stat.setSubmitted(toDate(value));
                        } else if ("user".equals(key)) {
                            stat.setUser(value);
                        } else if ("comment".equals(key)) {
                            stat.setComment(value);
                        }

                        //stat.setCpuUsed(0);
                        //stat.setMemoryUsed(Integer.parseInt(match.group(11)));
                        //stat.setSwapUsed(Integer.parseInt(match.group(12)));


                    } catch (ParseException x) {
                        x.printStackTrace();
                    }
                } else if (stat != null) {
                    map.put(stat.getId(), stat);
                    stat = null;
                }
            }
            if (stat != null) {
                map.put(stat.getId(), stat);
            }
            return map;
        } catch (IOException x) {
            throw new JobControlException("IOException during job submission", x);
        } catch (InterruptedException x) {
            throw new JobControlException("Job submission interrupted", x);
        }
    }

    private Date toDate(String date) throws ParseException {
        if ("0".equals(date)) {
            return null;
        }
        return new Date(Long.parseLong(date) * 1000);
    }

    private JobStatus.Status toStatus(String status) {
        if ("0".equals(status)) {
            return JobStatus.Status.PENDING;
        } else if ("1".equals(status)) {
            return JobStatus.Status.PENDING;
        } else if ("2".equals(status)) {
            return JobStatus.Status.RUNNING;
        } else if ("4".equals(status)) {
            return JobStatus.Status.DONE;
        } else if ("5".equals(status)) {
            return JobStatus.Status.FAILED;
        } else {
            return JobStatus.Status.UNKNOWN;
        }
    }

}
