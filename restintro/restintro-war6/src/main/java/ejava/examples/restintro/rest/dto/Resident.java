package ejava.examples.restintro.rest.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(namespace="http://dmv.ejava.info")
@XmlType(namespace="http://dmv.ejava.info", name="ResidentType", propOrder={
        "id", "firstName", "lastName", "contactInfo"
})
public class Resident {
    private long id;
    private String firstName;
    private String lastName;
    private List<ContactInfo> contactInfo=new ArrayList<ContactInfo>();
        
    public Resident() {}
    public Resident(String firstName, String lastName) {
        this.firstName=firstName;
        this.lastName=lastName;
    }
    public long getId() {
        return id;
    }
    public Resident setId(long id) {
        this.id = id;
        return this;
    }
    public String getFirstName() {
        return firstName;
    }
    public Resident setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    public String getLastName() {
        return lastName;
    }
    public Resident setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    @XmlElementWrapper(name="contacts")
    @XmlElement(name="contact")
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
