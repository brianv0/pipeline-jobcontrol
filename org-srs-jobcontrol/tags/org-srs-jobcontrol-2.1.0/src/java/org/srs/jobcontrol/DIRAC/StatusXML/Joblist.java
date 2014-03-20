// pastebin: http://pastebin.com/r2qAXK6v

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.10.31 at 08:19:20 PM CET 
//


package org.srs.jobcontrol.DIRAC.StatusXML;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="job" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="StandardOutput" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="CPUMHz" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                 &lt;attribute name="CPUNormalizationFactor" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                 &lt;attribute name="CPUScalingFactor" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                 &lt;attribute name="CacheSizekB" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="Ended" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="HostName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="JobID" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="JobPath" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="JobSanityCheck" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="JobWrapperPID" type="{http://www.w3.org/2001/XMLSchema}short" />
 *                 &lt;attribute name="LocalAccount" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="LocalBatchID" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="LocalJobID" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="MemorykB" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="MinorStatus" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="ModelName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="NormCPUTimes" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                 &lt;attribute name="OK" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="OutputSandboxMissingFiles" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="PayloadPID" type="{http://www.w3.org/2001/XMLSchema}short" />
 *                 &lt;attribute name="PilotAgent" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="Pilot_Reference" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="ScaledCPUTime" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                 &lt;attribute name="Site" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="Started" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="Status" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="Submitted" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="TotalCPUTimes" type="{http://www.w3.org/2001/XMLSchema}float" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "job"
})
@XmlRootElement(name = "joblist")
public class Joblist {

    protected List<Joblist.Job> job;

    /**
     * Gets the value of the job property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the job property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getJob().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Joblist.Job }
     * 
     * 
     */
    public List<Joblist.Job> getJob() {
        if (job == null) {
            job = new ArrayList<Joblist.Job>();
        }
        return this.job;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="StandardOutput" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *       &lt;attribute name="CPUMHz" type="{http://www.w3.org/2001/XMLSchema}float" />
     *       &lt;attribute name="CPUNormalizationFactor" type="{http://www.w3.org/2001/XMLSchema}float" />
     *       &lt;attribute name="CPUScalingFactor" type="{http://www.w3.org/2001/XMLSchema}float" />
     *       &lt;attribute name="CacheSizekB" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="Ended" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="HostName" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="JobID" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="JobPath" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="JobSanityCheck" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="JobWrapperPID" type="{http://www.w3.org/2001/XMLSchema}short" />
     *       &lt;attribute name="LocalAccount" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="LocalBatchID" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="LocalJobID" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="MemorykB" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="MinorStatus" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="ModelName" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="NormCPUTimes" type="{http://www.w3.org/2001/XMLSchema}float" />
     *       &lt;attribute name="OK" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="OutputSandboxMissingFiles" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="PayloadPID" type="{http://www.w3.org/2001/XMLSchema}short" />
     *       &lt;attribute name="PilotAgent" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="Pilot_Reference" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="ScaledCPUTime" type="{http://www.w3.org/2001/XMLSchema}float" />
     *       &lt;attribute name="Site" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="Started" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="Status" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="Submitted" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="TotalCPUTimes" type="{http://www.w3.org/2001/XMLSchema}float" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "standardOutput"
    })
    public static class Job {

        @XmlElement(name = "StandardOutput", required = true)
        protected String standardOutput;
        @XmlAttribute(name = "CPUMHz")
        protected Float cpumHz;
        @XmlAttribute(name = "CPUNormalizationFactor")
        protected Float cpuNormalizationFactor;
        @XmlAttribute(name = "CPUScalingFactor")
        protected Float cpuScalingFactor;
        @XmlAttribute(name = "CacheSizekB")
        protected String cacheSizekB;
        @XmlAttribute(name = "Ended")
        protected String ended;
        @XmlAttribute(name = "HostName")
        protected String hostName;
        @XmlAttribute(name = "JobID")
        protected Integer jobID;
        @XmlAttribute(name = "JobPath")
        protected String jobPath;
        @XmlAttribute(name = "JobSanityCheck")
        protected String jobSanityCheck;
        @XmlAttribute(name = "JobWrapperPID")
        protected Short jobWrapperPID;
        @XmlAttribute(name = "LocalAccount")
        protected String localAccount;
        @XmlAttribute(name = "LocalBatchID")
        protected String localBatchID;
        @XmlAttribute(name = "LocalJobID")
        protected String localJobID;
        @XmlAttribute(name = "MemorykB")
        protected String memorykB;
        @XmlAttribute(name = "MinorStatus")
        protected String minorStatus;
        @XmlAttribute(name = "ModelName")
        protected String modelName;
        @XmlAttribute(name = "NormCPUTimes")
        protected Float normCPUTimes;
        @XmlAttribute(name = "OK")
        protected String ok;
        @XmlAttribute(name = "OutputSandboxMissingFiles")
        protected String outputSandboxMissingFiles;
        @XmlAttribute(name = "PayloadPID")
        protected Short payloadPID;
        @XmlAttribute(name = "PilotAgent")
        protected String pilotAgent;
        @XmlAttribute(name = "Pilot_Reference")
        protected String pilotReference;
        @XmlAttribute(name = "ScaledCPUTime")
        protected Float scaledCPUTime;
        @XmlAttribute(name = "Site")
        protected String site;
        @XmlAttribute(name = "Started")
        protected String started;
        @XmlAttribute(name = "Status")
        protected String status;
        @XmlAttribute(name = "Submitted")
        protected String submitted;
        @XmlAttribute(name = "TotalCPUTimes")
        protected Float totalCPUTimes;

        /**
         * Gets the value of the standardOutput property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStandardOutput() {
            return standardOutput;
        }

        /**
         * Sets the value of the standardOutput property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStandardOutput(String value) {
            this.standardOutput = value;
        }

        /**
         * Gets the value of the cpumHz property.
         * 
         * @return
         *     possible object is
         *     {@link Float }
         *     
         */
        public Float getCPUMHz() {
            return cpumHz;
        }

        /**
         * Sets the value of the cpumHz property.
         * 
         * @param value
         *     allowed object is
         *     {@link Float }
         *     
         */
        public void setCPUMHz(Float value) {
            this.cpumHz = value;
        }

        /**
         * Gets the value of the cpuNormalizationFactor property.
         * 
         * @return
         *     possible object is
         *     {@link Float }
         *     
         */
        public Float getCPUNormalizationFactor() {
            return cpuNormalizationFactor;
        }

        /**
         * Sets the value of the cpuNormalizationFactor property.
         * 
         * @param value
         *     allowed object is
         *     {@link Float }
         *     
         */
        public void setCPUNormalizationFactor(Float value) {
            this.cpuNormalizationFactor = value;
        }

        /**
         * Gets the value of the cpuScalingFactor property.
         * 
         * @return
         *     possible object is
         *     {@link Float }
         *     
         */
        public Float getCPUScalingFactor() {
            return cpuScalingFactor;
        }

        /**
         * Sets the value of the cpuScalingFactor property.
         * 
         * @param value
         *     allowed object is
         *     {@link Float }
         *     
         */
        public void setCPUScalingFactor(Float value) {
            this.cpuScalingFactor = value;
        }

        /**
         * Gets the value of the cacheSizekB property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCacheSizekB() {
            return cacheSizekB;
        }

        /**
         * Sets the value of the cacheSizekB property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCacheSizekB(String value) {
            this.cacheSizekB = value;
        }

        /**
         * Gets the value of the ended property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEnded() {
            return ended;
        }

        /**
         * Sets the value of the ended property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEnded(String value) {
            this.ended = value;
        }

        /**
         * Gets the value of the hostName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHostName() {
            return hostName;
        }

        /**
         * Sets the value of the hostName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHostName(String value) {
            this.hostName = value;
        }

        /**
         * Gets the value of the jobID property.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getJobID() {
            return jobID;
        }

        /**
         * Sets the value of the jobID property.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setJobID(Integer value) {
            this.jobID = value;
        }

        /**
         * Gets the value of the jobPath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getJobPath() {
            return jobPath;
        }

        /**
         * Sets the value of the jobPath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setJobPath(String value) {
            this.jobPath = value;
        }

        /**
         * Gets the value of the jobSanityCheck property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getJobSanityCheck() {
            return jobSanityCheck;
        }

        /**
         * Sets the value of the jobSanityCheck property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setJobSanityCheck(String value) {
            this.jobSanityCheck = value;
        }

        /**
         * Gets the value of the jobWrapperPID property.
         * 
         * @return
         *     possible object is
         *     {@link Short }
         *     
         */
        public Short getJobWrapperPID() {
            return jobWrapperPID;
        }

        /**
         * Sets the value of the jobWrapperPID property.
         * 
         * @param value
         *     allowed object is
         *     {@link Short }
         *     
         */
        public void setJobWrapperPID(Short value) {
            this.jobWrapperPID = value;
        }

        /**
         * Gets the value of the localAccount property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLocalAccount() {
            return localAccount;
        }

        /**
         * Sets the value of the localAccount property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLocalAccount(String value) {
            this.localAccount = value;
        }

        /**
         * Gets the value of the localBatchID property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLocalBatchID() {
            return localBatchID;
        }

        /**
         * Sets the value of the localBatchID property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLocalBatchID(String value) {
            this.localBatchID = value;
        }

        /**
         * Gets the value of the localJobID property.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public String getLocalJobID() {
            return localJobID;
        }

        /**
         * Sets the value of the localJobID property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLocalJobID(String value) {
            this.localJobID = value;
        }

        /**
         * Gets the value of the memorykB property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMemorykB() {
            return memorykB;
        }

        /**
         * Sets the value of the memorykB property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMemorykB(String value) {
            this.memorykB = value;
        }

        /**
         * Gets the value of the minorStatus property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMinorStatus() {
            return minorStatus;
        }

        /**
         * Sets the value of the minorStatus property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMinorStatus(String value) {
            this.minorStatus = value;
        }

        /**
         * Gets the value of the modelName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getModelName() {
            return modelName;
        }

        /**
         * Sets the value of the modelName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setModelName(String value) {
            this.modelName = value;
        }

        /**
         * Gets the value of the normCPUTimes property.
         * 
         * @return
         *     possible object is
         *     {@link Float }
         *     
         */
        public Float getNormCPUTimes() {
            return normCPUTimes;
        }

        /**
         * Sets the value of the normCPUTimes property.
         * 
         * @param value
         *     allowed object is
         *     {@link Float }
         *     
         */
        public void setNormCPUTimes(Float value) {
            this.normCPUTimes = value;
        }

        /**
         * Gets the value of the ok property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOK() {
            return ok;
        }

        /**
         * Sets the value of the ok property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOK(String value) {
            this.ok = value;
        }

        /**
         * Gets the value of the outputSandboxMissingFiles property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOutputSandboxMissingFiles() {
            return outputSandboxMissingFiles;
        }

        /**
         * Sets the value of the outputSandboxMissingFiles property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOutputSandboxMissingFiles(String value) {
            this.outputSandboxMissingFiles = value;
        }

        /**
         * Gets the value of the payloadPID property.
         * 
         * @return
         *     possible object is
         *     {@link Short }
         *     
         */
        public Short getPayloadPID() {
            return payloadPID;
        }

        /**
         * Sets the value of the payloadPID property.
         * 
         * @param value
         *     allowed object is
         *     {@link Short }
         *     
         */
        public void setPayloadPID(Short value) {
            this.payloadPID = value;
        }

        /**
         * Gets the value of the pilotAgent property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPilotAgent() {
            return pilotAgent;
        }

        /**
         * Sets the value of the pilotAgent property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPilotAgent(String value) {
            this.pilotAgent = value;
        }

        /**
         * Gets the value of the pilotReference property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPilotReference() {
            return pilotReference;
        }

        /**
         * Sets the value of the pilotReference property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPilotReference(String value) {
            this.pilotReference = value;
        }

        /**
         * Gets the value of the scaledCPUTime property.
         * 
         * @return
         *     possible object is
         *     {@link Float }
         *     
         */
        public Float getScaledCPUTime() {
            return scaledCPUTime;
        }

        /**
         * Sets the value of the scaledCPUTime property.
         * 
         * @param value
         *     allowed object is
         *     {@link Float }
         *     
         */
        public void setScaledCPUTime(Float value) {
            this.scaledCPUTime = value;
        }

        /**
         * Gets the value of the site property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSite() {
            return site;
        }

        /**
         * Sets the value of the site property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSite(String value) {
            this.site = value;
        }

        /**
         * Gets the value of the started property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStarted() {
            return started;
        }

        /**
         * Sets the value of the started property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStarted(String value) {
            this.started = value;
        }

        /**
         * Gets the value of the status property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStatus() {
            return status;
        }

        /**
         * Sets the value of the status property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStatus(String value) {
            this.status = value;
        }

        /**
         * Gets the value of the submitted property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSubmitted() {
            return submitted;
        }

        /**
         * Sets the value of the submitted property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSubmitted(String value) {
            this.submitted = value;
        }

        /**
         * Gets the value of the totalCPUTimes property.
         * 
         * @return
         *     possible object is
         *     {@link Float }
         *     
         */
        public Float getTotalCPUTimes() {
            return totalCPUTimes;
        }

        /**
         * Sets the value of the totalCPUTimes property.
         * 
         * @param value
         *     allowed object is
         *     {@link Float }
         *     
         */
        public void setTotalCPUTimes(Float value) {
            this.totalCPUTimes = value;
        }

    }

}