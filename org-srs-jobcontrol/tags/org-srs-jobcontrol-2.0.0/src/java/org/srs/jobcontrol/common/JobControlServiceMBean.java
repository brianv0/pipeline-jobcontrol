package org.srs.jobcontrol.common;

import java.util.Date;
import java.util.Map;

/**
 * MBean interface for JobControlService
 * @author tonyj
 */
public interface JobControlServiceMBean
{
   Date getStartTime();
   int getJobSubmissionCount();
   Date getLastSuccessfulJobSubmissionTime();
   Date getLastFailedJobSubmissionTime();
   String getStatus();
   Map<String,Integer> getJobCounts();
}