//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.2-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.10.04 at 03:08:16 PM PDT 
//


package org.apache.openjpa.persistence.xmlmapping.xmlbindings.myaddress;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for USA_Address complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="USA_Address">
 *   &lt;complexContent>
 *     &lt;extension base="{}Address">
 *       &lt;sequence>
 *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ZIP" type="{}USPS_ZIP"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "USA_Address", propOrder = {
    "state",
    "zip"
})
public class USAAddress
    extends Address
{

    @XmlElement(name = "State")
    protected String state;
    @XmlElement(name = "ZIP")
    protected int zip;

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
     * Gets the value of the zip property.
     * 
     */
    public int getZIP() {
        return zip;
    }

    /**
     * Sets the value of the zip property.
     * 
     */
    public void setZIP(int value) {
        this.zip = value;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString())
            .append("\n         ")
            .append(this.state)
            .append(" ")
            .append(this.zip);
        return sb.toString();
    }
}
