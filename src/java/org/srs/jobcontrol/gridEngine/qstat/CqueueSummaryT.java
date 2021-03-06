//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.10.23 at 01:13:06 PM PDT 
//


package org.srs.jobcontrol.gridEngine.qstat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cqueue_summary_t complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cqueue_summary_t">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="load" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="used" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="resv" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="available" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="total" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="temp_disabled" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="manual_intervention" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="suspend_manual" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="suspend_threshold" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="suspend_on_subordinate" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="suspend_calendar" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="unknown" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="load_alarm" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="disabled_manual" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="disabled_calendar" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="ambiguous" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="orphaned" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="error" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cqueue_summary_t", propOrder = {
    "name",
    "load",
    "used",
    "resv",
    "available",
    "total",
    "tempDisabled",
    "manualIntervention",
    "suspendManual",
    "suspendThreshold",
    "suspendOnSubordinate",
    "suspendCalendar",
    "unknown",
    "loadAlarm",
    "disabledManual",
    "disabledCalendar",
    "ambiguous",
    "orphaned",
    "error"
})
public class CqueueSummaryT {

    @XmlElement(required = true)
    protected String name;
    protected Float load;
    @XmlSchemaType(name = "unsignedInt")
    protected long used;
    @XmlSchemaType(name = "unsignedInt")
    protected long resv;
    @XmlSchemaType(name = "unsignedInt")
    protected long available;
    @XmlSchemaType(name = "unsignedInt")
    protected long total;
    @XmlElement(name = "temp_disabled")
    @XmlSchemaType(name = "unsignedInt")
    protected long tempDisabled;
    @XmlElement(name = "manual_intervention")
    @XmlSchemaType(name = "unsignedInt")
    protected long manualIntervention;
    @XmlElement(name = "suspend_manual")
    @XmlSchemaType(name = "unsignedInt")
    protected Long suspendManual;
    @XmlElement(name = "suspend_threshold")
    @XmlSchemaType(name = "unsignedInt")
    protected Long suspendThreshold;
    @XmlElement(name = "suspend_on_subordinate")
    @XmlSchemaType(name = "unsignedInt")
    protected Long suspendOnSubordinate;
    @XmlElement(name = "suspend_calendar")
    @XmlSchemaType(name = "unsignedInt")
    protected Long suspendCalendar;
    @XmlSchemaType(name = "unsignedInt")
    protected Long unknown;
    @XmlElement(name = "load_alarm")
    @XmlSchemaType(name = "unsignedInt")
    protected Long loadAlarm;
    @XmlElement(name = "disabled_manual")
    @XmlSchemaType(name = "unsignedInt")
    protected Long disabledManual;
    @XmlElement(name = "disabled_calendar")
    @XmlSchemaType(name = "unsignedInt")
    protected Long disabledCalendar;
    @XmlSchemaType(name = "unsignedInt")
    protected Long ambiguous;
    @XmlSchemaType(name = "unsignedInt")
    protected Long orphaned;
    @XmlSchemaType(name = "unsignedInt")
    protected Long error;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the load property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getLoad() {
        return load;
    }

    /**
     * Sets the value of the load property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setLoad(Float value) {
        this.load = value;
    }

    /**
     * Gets the value of the used property.
     * 
     */
    public long getUsed() {
        return used;
    }

    /**
     * Sets the value of the used property.
     * 
     */
    public void setUsed(long value) {
        this.used = value;
    }

    /**
     * Gets the value of the resv property.
     * 
     */
    public long getResv() {
        return resv;
    }

    /**
     * Sets the value of the resv property.
     * 
     */
    public void setResv(long value) {
        this.resv = value;
    }

    /**
     * Gets the value of the available property.
     * 
     */
    public long getAvailable() {
        return available;
    }

    /**
     * Sets the value of the available property.
     * 
     */
    public void setAvailable(long value) {
        this.available = value;
    }

    /**
     * Gets the value of the total property.
     * 
     */
    public long getTotal() {
        return total;
    }

    /**
     * Sets the value of the total property.
     * 
     */
    public void setTotal(long value) {
        this.total = value;
    }

    /**
     * Gets the value of the tempDisabled property.
     * 
     */
    public long getTempDisabled() {
        return tempDisabled;
    }

    /**
     * Sets the value of the tempDisabled property.
     * 
     */
    public void setTempDisabled(long value) {
        this.tempDisabled = value;
    }

    /**
     * Gets the value of the manualIntervention property.
     * 
     */
    public long getManualIntervention() {
        return manualIntervention;
    }

    /**
     * Sets the value of the manualIntervention property.
     * 
     */
    public void setManualIntervention(long value) {
        this.manualIntervention = value;
    }

    /**
     * Gets the value of the suspendManual property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSuspendManual() {
        return suspendManual;
    }

    /**
     * Sets the value of the suspendManual property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSuspendManual(Long value) {
        this.suspendManual = value;
    }

    /**
     * Gets the value of the suspendThreshold property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSuspendThreshold() {
        return suspendThreshold;
    }

    /**
     * Sets the value of the suspendThreshold property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSuspendThreshold(Long value) {
        this.suspendThreshold = value;
    }

    /**
     * Gets the value of the suspendOnSubordinate property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSuspendOnSubordinate() {
        return suspendOnSubordinate;
    }

    /**
     * Sets the value of the suspendOnSubordinate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSuspendOnSubordinate(Long value) {
        this.suspendOnSubordinate = value;
    }

    /**
     * Gets the value of the suspendCalendar property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSuspendCalendar() {
        return suspendCalendar;
    }

    /**
     * Sets the value of the suspendCalendar property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSuspendCalendar(Long value) {
        this.suspendCalendar = value;
    }

    /**
     * Gets the value of the unknown property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getUnknown() {
        return unknown;
    }

    /**
     * Sets the value of the unknown property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setUnknown(Long value) {
        this.unknown = value;
    }

    /**
     * Gets the value of the loadAlarm property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLoadAlarm() {
        return loadAlarm;
    }

    /**
     * Sets the value of the loadAlarm property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLoadAlarm(Long value) {
        this.loadAlarm = value;
    }

    /**
     * Gets the value of the disabledManual property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDisabledManual() {
        return disabledManual;
    }

    /**
     * Sets the value of the disabledManual property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDisabledManual(Long value) {
        this.disabledManual = value;
    }

    /**
     * Gets the value of the disabledCalendar property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDisabledCalendar() {
        return disabledCalendar;
    }

    /**
     * Sets the value of the disabledCalendar property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDisabledCalendar(Long value) {
        this.disabledCalendar = value;
    }

    /**
     * Gets the value of the ambiguous property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAmbiguous() {
        return ambiguous;
    }

    /**
     * Sets the value of the ambiguous property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAmbiguous(Long value) {
        this.ambiguous = value;
    }

    /**
     * Gets the value of the orphaned property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOrphaned() {
        return orphaned;
    }

    /**
     * Sets the value of the orphaned property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOrphaned(Long value) {
        this.orphaned = value;
    }

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setError(Long value) {
        this.error = value;
    }

}
