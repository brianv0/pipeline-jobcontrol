package org.glast.jobcontrol.simulator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;
import javax.mail.Transport;

/**
 *
 * @author tonyj
 */
public class MailSender {

    private Session session;
    private static DateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    private Random random = new Random();
    private String to;
    
    MailSender(String smtpHost, String from, String to) {
        this(smtpHost,0,from,to);
    }
    MailSender(String smtpHost, int smtpPort, String from, String to) {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", smtpHost);
        if (smtpPort != 0) props.setProperty("mail.smtp.port", String.valueOf(smtpPort));
        props.setProperty("mail.from", from);
        session = Session.getInstance(props, null);
        this.to = to;
    }

    void sendStartMessage(long id, String workDir, Date startDate) throws MessagingException
    {
        Properties props = createStartMessage(id, startDate, workDir);
        props.put("Status", "Started");
        send(id,to,props);
    }
    void sendEndMessage(long id, String workDir, Date startDate, Date endDate, int rc) throws MessagingException
    {
        Properties props = createStartMessage(id, startDate, workDir);
        float elapsed = (endDate.getTime()-startDate.getTime())/1000.f;
        props.put("Elapsed",String.valueOf(elapsed));
        props.put("User",String.valueOf(elapsed*0.9f));
        props.put("System",String.valueOf(elapsed*0.05f));
        props.put("ExitCode", String.valueOf(rc));
        props.put("EndTime",dateFormatter.format(endDate));
        props.put("Status", "Ended");
        generateFakeMetaData(props);
        send(id,to,props);
    }
    private void generateFakeMetaData(Properties props)
    {
        for (int i=0; i<100; i++)
        {
            String key = String.format("Pipeline.fake%04d",i);
            int value = random.nextInt(); 
            props.setProperty(key,String.valueOf(value));
        }
        props.setProperty("Pipeline.startTime","257731200");
        props.setProperty("Pipeline.endTime","257731202");
    }
    
    private void send(long id, String to, Properties message) throws MessagingException {
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom();
        msg.setRecipients(Message.RecipientType.TO, to);
        msg.setSubject(String.valueOf(id));
        msg.setSentDate(new Date());
        msg.setText(propertiesToString(message));
        Transport.send(msg);
    }

    private Properties createStartMessage(long id, Date startDate, String workDir) {
        Properties props = new Properties();
        props.setProperty("ProcessInstance", String.valueOf(id));
        props.setProperty("Host", "dummy999");
        props.setProperty("StartTime", dateFormatter.format(startDate));
        props.setProperty("WorkDir", workDir);
        props.setProperty("LogFile", workDir + "/logfile.txt");
        return props;
    }

    private String propertiesToString(Properties message) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object,Object> entry : (Set<Map.Entry<Object,Object>>) message.entrySet())
        {
            builder.append(entry.getKey());
            builder.append(": ");
            builder.append(entry.getValue());
            builder.append('\n');
        }
        return builder.toString();
    }
    
    public static void main(String[] args) throws MessagingException, InterruptedException
    {
        MailSender sender = new MailSender("smtp.jaws.com","pipeline-test@slac.stanford.edu","tonyj321@jaws.com");
        Date start = new Date();
        sender.sendStartMessage(12345, "workDir", start);
        Thread.sleep(10000);
        sender.sendEndMessage(12345, "workDir", start, new Date(), 999);

    }
}
