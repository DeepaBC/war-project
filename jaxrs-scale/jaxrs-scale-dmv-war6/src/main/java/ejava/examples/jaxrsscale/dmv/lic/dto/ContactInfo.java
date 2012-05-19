package ejava.examples.jaxrsscale.dmv.lic.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents the contact information for an individual relative
 * to their residence, work, etc.
 */
@XmlRootElement(namespace=DrvLicRepresentation.DRVLIC_NAMESPACE)
@XmlType(namespace=DrvLicRepresentation.DRVLIC_NAMESPACE, name="ContactInfoType", propOrder={
        "type", "street", "city", "state", "zip", "email", "phone"
})
public class ContactInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private ContactType type;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String email;
    private String phone;
    
    public ContactType getType() {
        return type;
    }
    public ContactInfo setType(ContactType type) {
        this.type = type;
        return this;
    }
    public String getStreet() {
        return street;
    }
    public ContactInfo setStreet(String street) {
        this.street = street;
        return this;
    }
    public String getCity() {
        return city;
    }
    public ContactInfo setCity(String city) {
        this.city = city;
        return this;
    }
    public String getState() {
        return state;
    }
    public ContactInfo setState(String state) {
        this.state = state;
        return this;
    }
    public String getZip() {
        return zip;
    }
    public ContactInfo setZip(String zip) {
        this.zip = zip;
        return this;
    }
    public String getEmail() {
        return email;
    }
    public ContactInfo setEmail(String email) {
        this.email = email;
        return this;
    }
    public String getPhone() {
        return phone;
    }
    public ContactInfo setPhone(String phone) {
        this.phone = phone;
        return this;
    }
}
