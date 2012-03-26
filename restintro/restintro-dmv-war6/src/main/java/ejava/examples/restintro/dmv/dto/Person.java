package ejava.examples.restintro.dmv.dto;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a person within DMV. A Person can have many contexts/
 * roles. For example -- they are residents, vehicle owners, drivers, traffic
 * law violators, etc.
 */
@XmlRootElement(namespace="http://dmv.ejava.info")
@XmlType(namespace="http://dmv.ejava.info", name="PersonType", propOrder={
        "id", "firstName", "lastName", "contactInfo", "lastModified"
})
public class Person {
    private long id;
    private Date lastModified;
    private String firstName;
    private String lastName;
    private List<ContactInfo> contactInfo=new ArrayList<ContactInfo>();
        
    public Person() {}
    public Person(String firstName, String lastName) {
        this.firstName=firstName;
        this.lastName=lastName;
    }
    public long getId() {
        return id;
    }
    public Person setId(long id) {
        this.id = id;
        return this;
    }
    public String getFirstName() {
        return firstName;
    }
    public Person setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    public String getLastName() {
        return lastName;
    }
    public Person setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Date getLastModified() {
        return lastModified;
    }
    public Person setLastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }
    
    @XmlElementWrapper(name="contacts") //creates/names a wrapper element
    @XmlElement(name="contact")         //names each member element
    public List<ContactInfo> getContactInfo() {
        return contactInfo;
    }
    public void setContactInfo(List<ContactInfo> contactInfo) {
        this.contactInfo = contactInfo;
    }    
    
    public ContactInfo addContactInfo() {
        ContactInfo info = new ContactInfo();
        getContactInfo().add(info);
        return info;
    }
}
