package org.glast.jobcontrol;

/**
 * Thrown when attempting to query the status of a job with an invalid job id.
 * @author Tony Johnson
 */
public class NoSuchJobException extends Exception
{
   public NoSuchJobException(String message)
   {
      super(message);
   }
   public NoSuchJobException(String message, Throwable cause)
   {
      super(message);
      initCause(cause);
   }
}
