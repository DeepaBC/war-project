package ejava.examples.jaxrsrep.dmv.lic.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import ejava.util.rest.Link;


/**
 * This class represents an ID card for a resident
 */
@XmlRootElement(name="residentID", namespace=DrvLicRepresentation.DRVLIC_NAMESPACE)
@XmlType(name="ResidentIDType", namespace=DrvLicRepresentation.DRVLIC_NAMESPACE, propOrder={
        "id", "updated", "issueDate", "expirationDate", "identity", "physicalDetails"
})
public class ResidentID extends DrvLicRepresentation {
    private long id;
    private Date updated;
    private Date issueDate;
    private Date expirationDate;
    private Person identity;
    private PhysicalDetails physicalDetails=new PhysicalDetails();    
    private Link photo;
    
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
    
    @XmlTransient
    public boolean isReady() {
        return getIssueDate() != null ||
                (getIdentity() != null && 
                 getPhysicalDetails().isComplete() && 
                 getPhoto()!=null);
    }
    
    @XmlTransient
    public Link getPhoto() {
        return photo;
    }
    public void setPhoto(Link photo) {
        this.photo = photo;
    }
}
