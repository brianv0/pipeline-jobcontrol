
package org.srs.jobcontrol.web.taglib;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.srs.jobcontrol.JobControlClient;
import org.srs.jobcontrol.JobControlException;

/**
 *
 * @author bvan
 */
public class RemoteFile extends SimpleTagSupport {
    private String var;
    private String stream;
    private String processInstance;
    private Path filePath;
    private String serviceName;
    private String host;
    private String user;
    private int port = 1099;

    @Override
    public void doTag() throws JspException{
        String spid = stream + ":" + processInstance;
        try {
            Path workingDir = filePath.getParent();
            JobControlClient client = new JobControlClient(user, host, port, serviceName);   
            String result = client.getFile(spid, workingDir.toFile(), filePath.getFileName().toString());
            getJspContext().setAttribute(var, result);
        } catch(JobControlException | TimeoutException | IOException ex) {
            throw new JspException("Error getting status for id " + spid, ex);
        }
    }

    public void setVar(String value){
        this.var = value;
    }

    public void setStream(String stream){
        this.stream = stream;
    }

    public void setProcessInstance(String processInstance){
        this.processInstance = processInstance;
    }
    
    public void setFilePath(String filePath){
        this.filePath = Paths.get(filePath);
    }

    public void setHost(String host){
        this.host = host;
    }

    public void setUser(String user){
        this.user = user;
    }

    public void setServiceName(String serviceName){
        this.serviceName = serviceName;
    }

    public void setPort(int port){
        this.port = port;
    }

}