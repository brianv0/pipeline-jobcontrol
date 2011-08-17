/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glast.jobcontrol.gridEngine;

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
import java.util.regex.Pattern;
import org.glast.jobcontrol.*;
/**
 *
 * @author zimmer
 */
class gridEngineStatus
{
   private final static long CACHE_TIME = 60*1000; // Needed to avoid excessive calls to bjobs
   //private final static String STATUS_COMMAND = "/usr/local/bin/bjobs -W -a -p -u";
   private final static String STATUS_COMMAND = "/glast_data/Pipeline2/gridEngine/ge-qselect/ge-qselect"; // change to new mapping
   //jobname   uname   status     worker            worker             qtime               stime               etime               cputime     cur_mem     cur_scratch
   //I3432967 clavalle ENDED      ccwl0494.in2p3.fr ccwl0494.in2p3.fr  09/26/2006-15:49:04 09/26/2006-15:53:10 09/26/2006-19:04:56 10300       274         0
   // (\\d+)\\s+(\\S+)\\s+(\\S+)  \\s+(\\S+)        \\s+(\\S+)         \\s+(\\S+)          \\s+(\\S+)          \\s+(\\S+)           \\s+(\\d+) \\s+(\\d+)  \\s+(\\d+)
   //INFO: NoMatch: "I6462026 clavalle QUEUED - - 02/27/2007-15:27:51 - - - - -"
	    
   private final static Pattern pattern = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");
   private final static Pattern patternQueued = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\W)\\s+(\\W)\\s+(\\S+)\\s+(\\W)\\s+(\\W)\\s+(\\W)\\s+(\\W)\\s+(\\W)");

   private final static Pattern timePattern = Pattern.compile("(\\d+):(\\d+):(\\d+).(\\d+)");
   private final static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss");
   private final static Logger logger = Logger.getLogger("org.glast.jobcontrol.gridEngineStatus");
   
   private Map<String,JobStatus> map;
   private long timeStamp;
   
   /** Creates a new instance of gridEngineStatus */
   gridEngineStatus()
   {
   }
   
   private void updateStatus() throws JobControlException
   {
      try
      {
         String command = STATUS_COMMAND;
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
	    logger.info("result line # " + i +": " + result.get(i));

            //Matcher matchNormal = pattern.matcher(result.get(i));
            Matcher matchQueued = patternQueued.matcher(result.get(i));
	    //Matcher match=patternQueued.matcher(result.get(i));
	    //boolean matchFound=false;
	    //boolean matchQueuedFound=false;
	    
	    /**if(matchNormal.matches()){
	      logger.info("Match normal pattern: \""+result.get(i)+"\"");
	      match = pattern.matcher(result.get(i));
	      matchFound = true;
	    }
	    else{ 
	      logger.info("NoMatch normal pattern: \""+result.get(i)+"\"");
	      if (matchQueued.matches()){
	        logger.info("Match queued pattern");
		match = patternQueued.matcher(result.get(i));
		logger.info("id=" + match.group(1));
		matchQueuedFound = true;
	      }
	      else 
	        logger.info("No Match either normal nor queued pattern");
	    }
	   
            if (matchFound || matchQueuedFound)
            {*/

	    Matcher match = pattern.matcher(result.get(i));
	    if(match.matches()){   
               try
               {
	         // logger.info("matchFound, matchQueuedFound:" + matchFound + "," + matchQueuedFound);
                  gridEngineJobStatus stat = new gridEngineJobStatus();
                  String id = match.group(1);
		  logger.info("id=" + id);
                  stat.setId(id);
                  stat.setUser(match.group(2));
                  stat.setStatus(toStatus(match.group(3)));
		  
		  
                    stat.setQueue(match.group(4));
                    stat.setHost(match.group(5));
                    stat.setSubmitted(toDate(match.group(6)));
                    stat.setStarted(toDate(match.group(7)));               
                    stat.setEnded(toDate(match.group(8)));  
                    //stat.setCpuUsed(toTime(match.group(9)));
                    stat.setCpuUsed(Integer.parseInt(match.group(9))); // time in seconds 
                    stat.setMemoryUsed(Integer.parseInt(match.group(10)));
                    stat.setSwapUsed(Integer.parseInt(match.group(11)));
                  
		  

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
	    else{ 
	      logger.info("NoMatch normal pattern: \""+result.get(i)+"\"");
	      if (matchQueued.matches()){
	        logger.info("Match queued pattern, number of groups=" + matchQueued.groupCount());
		logger.info("group1=" + matchQueued.group(1));
		
		try
        	{
	          // logger.info("matchFound, matchQueuedFound:" + matchFound + "," + matchQueuedFound);
                   gridEngineJobStatus stat = new gridEngineJobStatus();
                   String id = matchQueued.group(1);
		   logger.info("id=" + id);
                   stat.setId(id);
                   stat.setUser(matchQueued.group(2));
                   stat.setStatus(toStatus(matchQueued.group(3)));


                     //stat.setQueue(matchQueued.group(4));
                     //stat.setHost(matchQueued.group(5));
                     stat.setSubmitted(toDate(matchQueued.group(6)));
                     //stat.setStarted(toDate(matchQueued.group(7)));               
                     //stat.setEnded(toDate(matchQueued.group(8)));  
                     //stat.setCpuUsed(toTime(match.group(9)));
                     //stat.setCpuUsed(Integer.parseInt(match.group(9))); // time in seconds 
                     //stat.setMemoryUsed(Integer.parseInt(match.group(10)));
                     //stat.setSwapUsed(Integer.parseInt(match.group(11)));



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
	      else 
	        logger.info("No Match either normal nor queued pattern");
	    }     
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
      //Date d = dateFormat.parse(year+"/"+date);
      Date d = dateFormat.parse(date);
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
      System.out.println("status found=" + status);
      if      ("QUEUED".equals(status)) return JobStatus.Status.PENDING;
      else if ("HOLD".equals(status))  return JobStatus.Status.WAITING;
      else if ("SUBMITTED".equals(status)) return JobStatus.Status.PENDING;
      else if ("STARTED".equals(status)) return JobStatus.Status.RUNNING;
      else if ("RUNNING".equals(status))  return JobStatus.Status.RUNNING;
      else if ("ENDED".equals(status)) return JobStatus.Status.DONE;
      else if ("DELETED".contains(status)) return JobStatus.Status.FAILED;
      else if ("KILLED".contains(status)) return JobStatus.Status.FAILED;
      else return JobStatus.Status.UNKNOWN;
   }
   Map<String, JobStatus> getStatus() throws JobControlException
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
