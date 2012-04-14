package ejava.examples.restintro.dmv.lic.dto;

import java.net.URI;


import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * This class provides a common base class for all applications in the system.
 */
@XmlRootElement(namespace=DrvLicRepresentation.DRVLIC_NAMESPACE)
@XmlType(namespace=DrvLicRepresentation.DRVLIC_NAMESPACE, name="ApplicationType", propOrder={
        "id", "updated", "created", "approved", "payment", "completed", "cancel", "reject", "approve"
})
public class Application extends DrvLicRepresentation {
    private long id;
    private Date created;
    private Date updated;
    private Date approved;
    private Date payment;
    private Date completed;
    
    private URI cancel;
    private URI reject;
    private URI approve;

    public long getId() { return id; }
    public Application setId(long id) {
        this.id = id;
        return this;
    }

    public Date getCreated() { return created; }
    public Application setCreated(Date created) {
        this.created = created;
        return this;
    }

    public Date getUpdated() { return updated; }
    public Application setUpdated(Date updated) {
        this.updated = updated;
        return this;
    }

    public Date getApproved() { return approved; }
    public Application setApproved(Date approved) {
        this.approved = approved;
        return this;
    }
    
    public Date getPayment() { return payment; }
    public void setPayment(Date payment) {
        this.payment = payment;
    }

    public Date getCompleted() { return completed; }
    public Application setCompleted(Date completed) {
        this.completed = completed;
        return this;
    }
    
    public void clearLinks() {
        cancel = approve = reject = null;
        super.clearLinks();
    }
    
    public URI getCancel() { return cancel; }
    public void setCancel(URI cancel) {
        this.cancel = cancel;
    }
    
    public URI getReject() { return reject; }
    public void setReject(URI reject) {
        this.reject = reject;
    }
    
    public URI getApprove() { return approve; }
    public void setApprove(URI approve) {
        this.approve = approve;
    }
}