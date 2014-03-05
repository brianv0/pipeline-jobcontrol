package org.srs.jobcontrol;

/**
 * Thrown if a transitory error (server down, batch system unavailable etc) occurs.
 * @author tonyj
 */
public class JobControlException extends Exception
{
   public JobControlException(String message)
   {
      super(message);
   }
   public JobControlException(String message, Throwable cause)
   {
      super(message);
      initCause(cause);
   }
   
}
