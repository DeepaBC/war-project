package ejava.examples.restintro.dmv.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents an ID card for a resident
 */
@XmlRootElement(name="residentID", namespace=Representation.DMV_NAMESPACE)
@XmlType(name="ResidentIDType", namespace=Representation.DMV_NAMESPACE, propOrder={
        "id", "updated", "issueDate", "expirationDate", "identity", "physicalDetails", "photo"
})
public class ResidentID extends Representation {
    private long id;
    private Date updated;
    private Date issueDate;
    private Date expirationDate;
    private Person identity;
    private PhysicalDetails physicalDetails;    
    private Photo photo;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public Date getUpdated() {
        return updated;
    }
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    public Date getIssueDate() {
        return issueDate;
    }
    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }
    public Date getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
    public Person getIdentity() {
        return identity;
    }
    public void setIdentity(Person identity) {
        this.identity = identity;
    }
    public PhysicalDetails getPhysicalDetails() {
        return physicalDetails;
    }
    public void setPhysicalDetails(PhysicalDetails physicalDetails) {
        this.physicalDetails = physicalDetails;
    }
    public Photo getPhoto() {
        return photo;
    }
    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
