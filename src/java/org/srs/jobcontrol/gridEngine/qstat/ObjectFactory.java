//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.10.23 at 01:13:06 PM PDT 
//


package org.srs.jobcontrol.gridEngine.qstat;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.sun.grid.xml.qstat package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.sun.grid.xml.qstat
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JobInfo }
     * 
     */
    public JobInfo createJobInfo() {
        return new JobInfo();
    }

    /**
     * Create an instance of {@link QueueInfoT }
     * 
     */
    public QueueInfoT createQueueInfoT() {
        return new QueueInfoT();
    }

    /**
     * Create an instance of {@link JobInfoT }
     * 
     */
    public JobInfoT createJobInfoT() {
        return new JobInfoT();
    }

    /**
     * Create an instance of {@link CqueueSummaryT }
     * 
     */
    public CqueueSummaryT createCqueueSummaryT() {
        return new CqueueSummaryT();
    }

    /**
     * Create an instance of {@link RequestT }
     * 
     */
    public RequestT createRequestT() {
        return new RequestT();
    }

    /**
     * Create an instance of {@link JobListT }
     * 
     */
    public JobListT createJobListT() {
        return new JobListT();
    }

    /**
     * Create an instance of {@link ResourceT }
     * 
     */
    public ResourceT createResourceT() {
        return new ResourceT();
    }

    /**
     * Create an instance of {@link QueueListT }
     * 
     */
    public QueueListT createQueueListT() {
        return new QueueListT();
    }

    /**
     * Create an instance of {@link GrantedPeT }
     * 
     */
    public GrantedPeT createGrantedPeT() {
        return new GrantedPeT();
    }

    /**
     * Create an instance of {@link RequestedPeT }
     * 
     */
    public RequestedPeT createRequestedPeT() {
        return new RequestedPeT();
    }

}
