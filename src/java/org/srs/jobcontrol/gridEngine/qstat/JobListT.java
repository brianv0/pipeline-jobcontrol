//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.10.23 at 01:13:06 PM PDT 
//


package org.srs.jobcontrol.gridEngine.qstat;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for job_list_t complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="job_list_t">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="JB_job_number" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="JAT_prio" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="JAT_ntix" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="JB_nppri" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="JB_nurg" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="JB_urg" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="JB_rrcontr" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="JB_wtcontr" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="JB_dlcontr" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="JB_priority" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="JB_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="JB_owner" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="JB_project" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="JB_department" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="JB_submission_time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="JAT_start_time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="JB_deadline" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="cpu_usage" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="mem_usage" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="io_usage" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="tickets" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="JB_override_tickets" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="JB_jobshare" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="otickets" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="ftickets" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="stickets" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="JAT_share" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="queue_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="master" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="slots" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="tasks" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requested_pe" type="{http://gridengine.sunsource.net/source/browse/*checkout*<!---->/gridengine/source/dist/util/resources/schemas/qstat/qstat.xsd?revision=1.11}requested_pe_t" minOccurs="0"/>
 *         &lt;element name="granted_pe" type="{http://gridengine.sunsource.net/source/browse/*checkout*<!---->/gridengine/source/dist/util/resources/schemas/qstat/qstat.xsd?revision=1.11}granted_pe_t" minOccurs="0"/>
 *         &lt;element name="JB_checkpoint_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hard_request" type="{http://gridengine.sunsource.net/source/browse/*checkout*<!---->/gridengine/source/dist/util/resources/schemas/qstat/qstat.xsd?revision=1.11}request_t" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="def_hard_request" type="{http://gridengine.sunsource.net/source/browse/*checkout*<!---->/gridengine/source/dist/util/resources/schemas/qstat/qstat.xsd?revision=1.11}request_t" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="soft_request" type="{http://gridengine.sunsource.net/source/browse/*checkout*<!---->/gridengine/source/dist/util/resources/schemas/qstat/qstat.xsd?revision=1.11}request_t" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="hard_req_queue" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="soft_req_queue" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="master_hard_req_queue" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="predecessor_jobs_req" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="predecessor_jobs" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="state" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "job_list_t", propOrder = {
    "jbJobNumber",
    "jatPrio",
    "jatNtix",
    "jbNppri",
    "jbNurg",
    "jbUrg",
    "jbRrcontr",
    "jbWtcontr",
    "jbDlcontr",
    "jbPriority",
    "jbName",
    "jbOwner",
    "jbProject",
    "jbDepartment",
    "state",
    "jbSubmissionTime",
    "jatStartTime",
    "jbDeadline",
    "cpuUsage",
    "memUsage",
    "ioUsage",
    "tickets",
    "jbOverrideTickets",
    "jbJobshare",
    "otickets",
    "ftickets",
    "stickets",
    "jatShare",
    "queueName",
    "master",
    "slots",
    "tasks",
    "requestedPe",
    "grantedPe",
    "jbCheckpointName",
    "hardRequest",
    "defHardRequest",
    "softRequest",
    "hardReqQueue",
    "softReqQueue",
    "masterHardReqQueue",
    "predecessorJobsReq",
    "predecessorJobs"
})
public class JobListT {

    @XmlElement(name = "JB_job_number")
    @XmlSchemaType(name = "unsignedInt")
    protected long jbJobNumber;
    @XmlElement(name = "JAT_prio")
    protected float jatPrio;
    @XmlElement(name = "JAT_ntix")
    protected Float jatNtix;
    @XmlElement(name = "JB_nppri")
    protected Float jbNppri;
    @XmlElement(name = "JB_nurg")
    protected Float jbNurg;
    @XmlElement(name = "JB_urg")
    @XmlSchemaType(name = "unsignedInt")
    protected Long jbUrg;
    @XmlElement(name = "JB_rrcontr")
    @XmlSchemaType(name = "unsignedInt")
    protected Long jbRrcontr;
    @XmlElement(name = "JB_wtcontr")
    @XmlSchemaType(name = "unsignedInt")
    protected Long jbWtcontr;
    @XmlElement(name = "JB_dlcontr")
    @XmlSchemaType(name = "unsignedInt")
    protected Long jbDlcontr;
    @XmlElement(name = "JB_priority")
    @XmlSchemaType(name = "unsignedInt")
    protected Long jbPriority;
    @XmlElement(name = "JB_name", required = true)
    protected String jbName;
    @XmlElement(name = "JB_owner", required = true)
    protected String jbOwner;
    @XmlElement(name = "JB_project")
    protected String jbProject;
    @XmlElement(name = "JB_department")
    protected String jbDepartment;
    @XmlElement(required = true)
    protected String state;
    @XmlElement(name = "JB_submission_time")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar jbSubmissionTime;
    @XmlElement(name = "JAT_start_time")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar jatStartTime;
    @XmlElement(name = "JB_deadline")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar jbDeadline;
    @XmlElement(name = "cpu_usage")
    protected Float cpuUsage;
    @XmlElement(name = "mem_usage")
    protected Float memUsage;
    @XmlElement(name = "io_usage")
    protected Float ioUsage;
    @XmlSchemaType(name = "unsignedInt")
    protected Long tickets;
    @XmlElement(name = "JB_override_tickets")
    @XmlSchemaType(name = "unsignedInt")
    protected Long jbOverrideTickets;
    @XmlElement(name = "JB_jobshare")
    @XmlSchemaType(name = "unsignedInt")
    protected Long jbJobshare;
    @XmlSchemaType(name = "unsignedInt")
    protected Long otickets;
    @XmlSchemaType(name = "unsignedInt")
    protected Long ftickets;
    @XmlSchemaType(name = "unsignedInt")
    protected Long stickets;
    @XmlElement(name = "JAT_share")
    protected Float jatShare;
    @XmlElement(name = "queue_name")
    protected String queueName;
    protected String master;
    @XmlSchemaType(name = "unsignedInt")
    protected long slots;
    protected String tasks;
    @XmlElement(name = "requested_pe")
    protected RequestedPeT requestedPe;
    @XmlElement(name = "granted_pe")
    protected GrantedPeT grantedPe;
    @XmlElement(name = "JB_checkpoint_name")
    protected String jbCheckpointName;
    @XmlElement(name = "hard_request")
    protected List<RequestT> hardRequest;
    @XmlElement(name = "def_hard_request")
    protected List<RequestT> defHardRequest;
    @XmlElement(name = "soft_request")
    protected List<RequestT> softRequest;
    @XmlElement(name = "hard_req_queue")
    protected List<String> hardReqQueue;
    @XmlElement(name = "soft_req_queue")
    protected List<String> softReqQueue;
    @XmlElement(name = "master_hard_req_queue")
    protected List<String> masterHardReqQueue;
    @XmlElement(name = "predecessor_jobs_req")
    protected List<String> predecessorJobsReq;
    @XmlElement(name = "predecessor_jobs", type = Long.class)
    @XmlSchemaType(name = "unsignedInt")
    protected List<Long> predecessorJobs;
    @XmlAttribute(name = "state", required = true)
    protected String stateAttribute;

    /**
     * Gets the value of the jbJobNumber property.
     * 
     */
    public long getJBJobNumber() {
        return jbJobNumber;
    }

    /**
     * Sets the value of the jbJobNumber property.
     * 
     */
    public void setJBJobNumber(long value) {
        this.jbJobNumber = value;
    }

    /**
     * Gets the value of the jatPrio property.
     * 
     */
    public float getJATPrio() {
        return jatPrio;
    }

    /**
     * Sets the value of the jatPrio property.
     * 
     */
    public void setJATPrio(float value) {
        this.jatPrio = value;
    }

    /**
     * Gets the value of the jatNtix property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getJATNtix() {
        return jatNtix;
    }

    /**
     * Sets the value of the jatNtix property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setJATNtix(Float value) {
        this.jatNtix = value;
    }

    /**
     * Gets the value of the jbNppri property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getJBNppri() {
        return jbNppri;
    }

    /**
     * Sets the value of the jbNppri property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setJBNppri(Float value) {
        this.jbNppri = value;
    }

    /**
     * Gets the value of the jbNurg property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getJBNurg() {
        return jbNurg;
    }

    /**
     * Sets the value of the jbNurg property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setJBNurg(Float value) {
        this.jbNurg = value;
    }

    /**
     * Gets the value of the jbUrg property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getJBUrg() {
        return jbUrg;
    }

    /**
     * Sets the value of the jbUrg property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setJBUrg(Long value) {
        this.jbUrg = value;
    }

    /**
     * Gets the value of the jbRrcontr property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getJBRrcontr() {
        return jbRrcontr;
    }

    /**
     * Sets the value of the jbRrcontr property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setJBRrcontr(Long value) {
        this.jbRrcontr = value;
    }

    /**
     * Gets the value of the jbWtcontr property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getJBWtcontr() {
        return jbWtcontr;
    }

    /**
     * Sets the value of the jbWtcontr property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setJBWtcontr(Long value) {
        this.jbWtcontr = value;
    }

    /**
     * Gets the value of the jbDlcontr property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getJBDlcontr() {
        return jbDlcontr;
    }

    /**
     * Sets the value of the jbDlcontr property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setJBDlcontr(Long value) {
        this.jbDlcontr = value;
    }

    /**
     * Gets the value of the jbPriority property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getJBPriority() {
        return jbPriority;
    }

    /**
     * Sets the value of the jbPriority property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setJBPriority(Long value) {
        this.jbPriority = value;
    }

    /**
     * Gets the value of the jbName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJBName() {
        return jbName;
    }

    /**
     * Sets the value of the jbName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJBName(String value) {
        this.jbName = value;
    }

    /**
     * Gets the value of the jbOwner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJBOwner() {
        return jbOwner;
    }

    /**
     * Sets the value of the jbOwner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJBOwner(String value) {
        this.jbOwner = value;
    }

    /**
     * Gets the value of the jbProject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJBProject() {
        return jbProject;
    }

    /**
     * Sets the value of the jbProject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJBProject(String value) {
        this.jbProject = value;
    }

    /**
     * Gets the value of the jbDepartment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJBDepartment() {
        return jbDepartment;
    }

    /**
     * Sets the value of the jbDepartment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJBDepartment(String value) {
        this.jbDepartment = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setState(String value) {
        this.state = value;
    }

    /**
     * Gets the value of the jbSubmissionTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getJBSubmissionTime() {
        return jbSubmissionTime;
    }

    /**
     * Sets the value of the jbSubmissionTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setJBSubmissionTime(XMLGregorianCalendar value) {
        this.jbSubmissionTime = value;
    }

    /**
     * Gets the value of the jatStartTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getJATStartTime() {
        return jatStartTime;
    }

    /**
     * Sets the value of the jatStartTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setJATStartTime(XMLGregorianCalendar value) {
        this.jatStartTime = value;
    }

    /**
     * Gets the value of the jbDeadline property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getJBDeadline() {
        return jbDeadline;
    }

    /**
     * Sets the value of the jbDeadline property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setJBDeadline(XMLGregorianCalendar value) {
        this.jbDeadline = value;
    }

    /**
     * Gets the value of the cpuUsage property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getCpuUsage() {
        return cpuUsage;
    }

    /**
     * Sets the value of the cpuUsage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setCpuUsage(Float value) {
        this.cpuUsage = value;
    }

    /**
     * Gets the value of the memUsage property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getMemUsage() {
        return memUsage;
    }

    /**
     * Sets the value of the memUsage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setMemUsage(Float value) {
        this.memUsage = value;
    }

    /**
     * Gets the value of the ioUsage property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getIoUsage() {
        return ioUsage;
    }

    /**
     * Sets the value of the ioUsage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setIoUsage(Float value) {
        this.ioUsage = value;
    }

    /**
     * Gets the value of the tickets property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTickets() {
        return tickets;
    }

    /**
     * Sets the value of the tickets property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTickets(Long value) {
        this.tickets = value;
    }

    /**
     * Gets the value of the jbOverrideTickets property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getJBOverrideTickets() {
        return jbOverrideTickets;
    }

    /**
     * Sets the value of the jbOverrideTickets property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setJBOverrideTickets(Long value) {
        this.jbOverrideTickets = value;
    }

    /**
     * Gets the value of the jbJobshare property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getJBJobshare() {
        return jbJobshare;
    }

    /**
     * Sets the value of the jbJobshare property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setJBJobshare(Long value) {
        this.jbJobshare = value;
    }

    /**
     * Gets the value of the otickets property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOtickets() {
        return otickets;
    }

    /**
     * Sets the value of the otickets property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOtickets(Long value) {
        this.otickets = value;
    }

    /**
     * Gets the value of the ftickets property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFtickets() {
        return ftickets;
    }

    /**
     * Sets the value of the ftickets property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFtickets(Long value) {
        this.ftickets = value;
    }

    /**
     * Gets the value of the stickets property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getStickets() {
        return stickets;
    }

    /**
     * Sets the value of the stickets property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setStickets(Long value) {
        this.stickets = value;
    }

    /**
     * Gets the value of the jatShare property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getJATShare() {
        return jatShare;
    }

    /**
     * Sets the value of the jatShare property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setJATShare(Float value) {
        this.jatShare = value;
    }

    /**
     * Gets the value of the queueName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueueName() {
        return queueName;
    }

    /**
     * Sets the value of the queueName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueueName(String value) {
        this.queueName = value;
    }

    /**
     * Gets the value of the master property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaster() {
        return master;
    }

    /**
     * Sets the value of the master property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaster(String value) {
        this.master = value;
    }

    /**
     * Gets the value of the slots property.
     * 
     */
    public long getSlots() {
        return slots;
    }

    /**
     * Sets the value of the slots property.
     * 
     */
    public void setSlots(long value) {
        this.slots = value;
    }

    /**
     * Gets the value of the tasks property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTasks() {
        return tasks;
    }

    /**
     * Sets the value of the tasks property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTasks(String value) {
        this.tasks = value;
    }

    /**
     * Gets the value of the requestedPe property.
     * 
     * @return
     *     possible object is
     *     {@link RequestedPeT }
     *     
     */
    public RequestedPeT getRequestedPe() {
        return requestedPe;
    }

    /**
     * Sets the value of the requestedPe property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestedPeT }
     *     
     */
    public void setRequestedPe(RequestedPeT value) {
        this.requestedPe = value;
    }

    /**
     * Gets the value of the grantedPe property.
     * 
     * @return
     *     possible object is
     *     {@link GrantedPeT }
     *     
     */
    public GrantedPeT getGrantedPe() {
        return grantedPe;
    }

    /**
     * Sets the value of the grantedPe property.
     * 
     * @param value
     *     allowed object is
     *     {@link GrantedPeT }
     *     
     */
    public void setGrantedPe(GrantedPeT value) {
        this.grantedPe = value;
    }

    /**
     * Gets the value of the jbCheckpointName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJBCheckpointName() {
        return jbCheckpointName;
    }

    /**
     * Sets the value of the jbCheckpointName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJBCheckpointName(String value) {
        this.jbCheckpointName = value;
    }

    /**
     * Gets the value of the hardRequest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hardRequest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHardRequest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RequestT }
     * 
     * 
     */
    public List<RequestT> getHardRequest() {
        if (hardRequest == null) {
            hardRequest = new ArrayList<RequestT>();
        }
        return this.hardRequest;
    }

    /**
     * Gets the value of the defHardRequest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the defHardRequest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDefHardRequest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RequestT }
     * 
     * 
     */
    public List<RequestT> getDefHardRequest() {
        if (defHardRequest == null) {
            defHardRequest = new ArrayList<RequestT>();
        }
        return this.defHardRequest;
    }

    /**
     * Gets the value of the softRequest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the softRequest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSoftRequest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RequestT }
     * 
     * 
     */
    public List<RequestT> getSoftRequest() {
        if (softRequest == null) {
            softRequest = new ArrayList<RequestT>();
        }
        return this.softRequest;
    }

    /**
     * Gets the value of the hardReqQueue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hardReqQueue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHardReqQueue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getHardReqQueue() {
        if (hardReqQueue == null) {
            hardReqQueue = new ArrayList<String>();
        }
        return this.hardReqQueue;
    }

    /**
     * Gets the value of the softReqQueue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the softReqQueue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSoftReqQueue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSoftReqQueue() {
        if (softReqQueue == null) {
            softReqQueue = new ArrayList<String>();
        }
        return this.softReqQueue;
    }

    /**
     * Gets the value of the masterHardReqQueue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the masterHardReqQueue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMasterHardReqQueue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getMasterHardReqQueue() {
        if (masterHardReqQueue == null) {
            masterHardReqQueue = new ArrayList<String>();
        }
        return this.masterHardReqQueue;
    }

    /**
     * Gets the value of the predecessorJobsReq property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the predecessorJobsReq property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPredecessorJobsReq().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPredecessorJobsReq() {
        if (predecessorJobsReq == null) {
            predecessorJobsReq = new ArrayList<String>();
        }
        return this.predecessorJobsReq;
    }

    /**
     * Gets the value of the predecessorJobs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the predecessorJobs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPredecessorJobs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getPredecessorJobs() {
        if (predecessorJobs == null) {
            predecessorJobs = new ArrayList<Long>();
        }
        return this.predecessorJobs;
    }

    /**
     * Gets the value of the stateAttribute property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStateAttribute() {
        return stateAttribute;
    }

    /**
     * Sets the value of the stateAttribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStateAttribute(String value) {
        this.stateAttribute = value;
    }

}
