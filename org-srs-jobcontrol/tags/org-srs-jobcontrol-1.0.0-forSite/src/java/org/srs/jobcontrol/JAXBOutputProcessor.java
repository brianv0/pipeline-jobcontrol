package org.srs.jobcontrol;

import java.io.InputStream;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * An InputStream based output processor for JAXB
 * @author Brian Van Klaveren
 */
public class JAXBOutputProcessor<T> extends Thread
{
   private T result;
   JAXBContext jc;
   private JAXBException status;
   private Logger logger;
   private InputStream in;
   
   public JAXBOutputProcessor(InputStream in, String jaxbContext, Logger logger) throws JAXBException
   {
      this.in = in;
      this.logger = logger;
      JAXBContext jc = JAXBContext.newInstance(jaxbContext);
      start();
   }
   
   public void run()
   {
        try {
            Unmarshaller um = jc.createUnmarshaller();
            result = (T) um.unmarshal(in);
        } catch (JAXBException ex) {
            status = ex;
        }
   }
   
   public T getResult()
   {
      return result;
   }
   public JAXBException getStatus()
   {
      return status;
   }
}