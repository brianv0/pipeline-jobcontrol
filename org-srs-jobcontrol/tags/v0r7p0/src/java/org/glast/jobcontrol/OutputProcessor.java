package org.glast.jobcontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Tony Johnson
 */
class OutputProcessor extends Thread
{
   private BufferedReader reader;
   private List<String> result = new ArrayList<String>();
   private IOException status;
   private Logger logger;
   
   OutputProcessor(InputStream in, Logger logger)
   {
      this.logger = logger;
      reader = new BufferedReader(new InputStreamReader(in));
      start();
   }
   public void run()
   {
      try
      {
         for (;;)
         {
            String line = reader.readLine();
            if (line == null) break;
            logger.fine(line);
            result.add(line);
         }
      }
      catch (IOException x)
      {
         status = x;
      }
   }
   List<String> getResult()
   {
      return result;
   }
   IOException getStatus()
   {
      return status;
   }
}