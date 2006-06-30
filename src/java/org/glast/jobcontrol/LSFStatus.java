package org.glast.jobcontrol;

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
class LSFStatus
{
   private final static long CACHE_TIME = 60*1000; // Needed to avoid excessive calls to bjobs
   private final static String STATUS_COMMAND = "/usr/local/bin/bjobs -W -a -p -u glast";
   private final static Pattern pattern = Pattern.compile("(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(.*)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s*");
   private final static Pattern timePattern = Pattern.compile("(\\d+):(\\d+):(\\d+).(\\d+)");
   private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
   private final static Logger logger = Logger.getLogger("org.glast.jobcontrol.LSFStatus");
   
   private Map<Integer,JobStatus> map;
   private long timeStamp;
   
   /** Creates a new instance of LSFStatus */
   LSFStatus()
   {
   }
   
   private void updateStatus() throws JobControlException
   {
      try
      {
         List<String> commands = new ArrayList<String>(Arrays.asList(STATUS_COMMAND.split("\\s+")));
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
         
         Map<Integer,JobStatus> map = new HashMap<Integer,JobStatus>();
         
         for (int i=0; i<result.size(); i++)
         {
            Matcher match = pattern.matcher(result.get(i));
            if (match.matches())
            {
               try
               {
                  LSFJobStatus stat = new LSFJobStatus();
                  int id = Integer.parseInt(match.group(1));
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
            //else System.err.println("NoMatch: \""+result.get(i)+"\"");
         }
         synchronized (this)
         {
            this.map = map;
            this.timeStamp = System.currentTimeMillis();
         }
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
      else if ("SUSP".contains(status)) return JobStatus.Status.SUSPENDED;
      else return JobStatus.Status.UNKNOWN;
   }
   Map<Integer, JobStatus> getStatus() throws JobControlException
   {
      synchronized (this)
      {
         long now = System.currentTimeMillis();
         boolean updateNeeded = now-timeStamp > CACHE_TIME;
         logger.fine("status: now="+now+" timeStamp="+timeStamp+" cache="+CACHE_TIME+" update needed: "+updateNeeded);
         if (updateNeeded) updateStatus();
      }
      return map;
   }
}
