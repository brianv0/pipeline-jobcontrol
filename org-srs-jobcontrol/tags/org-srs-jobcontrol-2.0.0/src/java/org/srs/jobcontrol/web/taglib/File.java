package org.srs.jobcontrol.web.taglib;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * 
 * @author tonyj
 * @version $Id: File.java,v 1.1 2005-11-04 23:56:10 tonyj Exp $
 */

public class File extends SimpleTagSupport
{
   private String name;
   
   /**Called by the container to invoke this tag.
    * The implementation of this method is provided by the tag library developer,
    * and handles all tag processing, body iteration, etc.
    */
   public void doTag() throws JspException
   {
      StringWriter writer = new StringWriter();
      try
      {
         JspFragment fragment = getJspBody();
         if (fragment != null) fragment.invoke(writer); 
         JspTag parent = getParent();
         if (parent instanceof Submit)
         {
            // In case the file contains windows line separators we reprocess
            // it to substitute plain unix line separators ('\n')
            BufferedReader reader = new BufferedReader(new StringReader(writer.toString()));
            StringBuffer unix = new StringBuffer();
            for (;;)
            {
                String line = reader.readLine();
                if (line == null) break;
                unix.append(line).append('\n');
            }
            ((Submit) parent).addFile(name,unix.toString());
         }
      }
      catch (IOException x)
      {
         throw new JspException("Error processing file body",x);
      }
   }
   
   public void setName(String value)
   {
      this.name = value;
   }
}
