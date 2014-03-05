package org.srs.jobcontrol.lsf;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;/**
 * Keeps track of job status
 * @author tonyj
 */
import org.srs.jobcontrol.*;
import org.srs.jobcontrol.common.CommonJobStatus;
class LSFStatus
{
   private final static String STATUS_COMMAND = System.getProperty("org.srs.jobcontrol.lsf.statusCommand", "/usr/local/bin/bjobs -W -a -p -u");
   private final static Pattern pattern = Pattern.compile("(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(.*)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s*");
   private final static Pattern timePattern = Pattern.compile("(\\d+):(\\d+):(\\d+).(\\d+)");
   private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
   private final static Logger logger = Logger.getLogger("org.srs.jobcontrol.LSFStatus");
      
   /** Creates a new instance of LSFStatus */
   LSFStatus()
   {
   }
   
   public Map<String, JobStatus> getStatus() throws JobControlException{
      try
      {
         long now = System.currentTimeMillis();
         logger.fine( "status: now=" + now + " timeStamp=" );
         String command = STATUS_COMMAND + " " + System.getProperty("user.name");
         List<String> commands = new ArrayList<String>(Arrays.asList(command.split("\\s+")));
         ProcessBuilder builder = new ProcessBuilder();
         builder.redirectErrorStream(true);
         builder.command(commands);
         Process process = builder.start();
         OutputProcessor output = new OutputProcessor(process.getInputStream(),logger);
         process.waitFor();
         output.join();
         int rc = process.exitValue();
         if (rc != 0) throw new JobControlException("Process failed, rc="+rc);
         
         if (output.getStatus() != null) throw output.getStatus();
         
         List<String> result = output.getResult();
         if (result.size() == 0) throw new JobControlException("Unexpected output length "+result.size());
         logger.info("Status returned "+result.size()+" lines");
         
         Map<String,JobStatus> map = new HashMap<String,JobStatus>();
         
         for (int i=0; i<result.size(); i++)
         {
            Matcher match = pattern.matcher(result.get(i));
            if (match.matches())
            {
               try
               {
                  CommonJobStatus stat = new CommonJobStatus();
                  String id = match.group(1);
                  stat.setId(id);
                  stat.setUser(match.group(2));
                  stat.setStatus(toStatus(match.group(3)));
                  stat.setQueue(match.group(4));
                  stat.setHost(match.group(6));
                  stat.setSubmitted(toDate(match.group(8)));
                  stat.setStarted(toDate(match.group(14)));               
                  stat.setEnded(toDate(match.group(15)));  
                  stat.setCpuUsed(toTime(match.group(10)));
                  stat.setMemoryUsed(Integer.parseInt(match.group(11)));
                  stat.setSwapUsed(Integer.parseInt(match.group(12)));
                  
                  StringBuffer comment = null;
                  while ((i+1)<result.size() && result.get(i+1).charAt(0) == ' ')
                  {
                     i++;
                     if (comment == null) comment = new StringBuffer();
                     else comment.append('\n');
                     comment.append(result.get(i).substring(1));
                  }
                  if (comment != null) stat.setComment(comment.toString());
                  
                  map.put(id,stat);
               }
               catch (ParseException x)
               {
                  x.printStackTrace();
               }
            }
         }
        return map;
      }
      catch (IOException x)
      {
         throw new JobControlException("IOException during job submission",x);
      }
      catch (InterruptedException x)
      {
         throw new JobControlException("Job submission interrupted",x);
      }
   }
   
   private Date toDate(String date) throws ParseException
   {
      if (date.equals("-")) return null;
      Calendar now = Calendar.getInstance();
      int year = now.get(Calendar.YEAR);
      // FIXME: What about new year?
      Date d = dateFormat.parse(year+"/"+date);
      return d;
   }
   
   private int toTime(String date) throws ParseException
   {
      Matcher match = timePattern.matcher(date);
      if (match.matches())
      {
         int hour = Integer.parseInt(match.group(1));
         int minute = Integer.parseInt(match.group(2));
         int second = Integer.parseInt(match.group(3));
         return (hour*60+minute)*60+second;
      }
      else return 0;
   }
   
   private JobStatus.Status toStatus(String status)
   {
      if      ("DONE".equals(status)) return JobStatus.Status.DONE;
      else if ("RUN".equals(status))  return JobStatus.Status.RUNNING;
      else if ("PEND".equals(status)) return JobStatus.Status.PENDING;
      else if ("WAIT".equals(status)) return JobStatus.Status.WAITING;
      else if ("EXIT".equals(status)) return JobStatus.Status.FAILED;
      else if ("SUSP".contains(status)) return JobStatus.Status.SUSPENDED;
      else return JobStatus.Status.UNKNOWN;
   }
   
}
