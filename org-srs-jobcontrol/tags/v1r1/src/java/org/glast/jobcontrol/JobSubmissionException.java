package org.glast.jobcontrol;

/**
 * Thrown if a permanent error occurs during job submission.
 * @author Tony Johnson
 */
public class JobSubmissionException extends Exception
{
   public JobSubmissionException(String message)
   {
      super(message);
   }
   public JobSubmissionException(String message, Throwable cause)
   {
      super(message);
      initCause(cause);
   }
}
