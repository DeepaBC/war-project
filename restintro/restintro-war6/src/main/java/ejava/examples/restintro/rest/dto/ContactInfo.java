package ejava.examples.restintro.rest.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(namespace="http://dmv.ejava.info")
@XmlType(namespace="http://dmv.ejava.info", name="ContactInfoType")
public class ContactInfo {
    public static final String HOME = "Home";
    private String name;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String email;
    private String phone;
    
    public String getName() {
        return name;
    }
    public ContactInfo setName(String name) {
        this.name = name;
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
