package ejava.examples.restintro.dmv.dto;

import java.net.URI;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class provides a common base class for all applications in the system.
 */
@XmlRootElement(namespace="http://dmv.ejava.info")
@XmlType(namespace="http://dmv.ejava.info", name="ApplicationType", propOrder={
        "id", "updated", "created", "approved", "completed", "cancel", "reject", "approve", "links"
})
public class Application {
    private long id;
    private Date created;
    private Date updated;
    private Date approved;
    private Date completed;
    private URI cancel;
    private URI reject;
    private URI approve;
    private List<Link> links=new ArrayList<Link>();

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
    
    public Date getCompleted() { return completed; }
    public Application setCompleted(Date completed) {
        this.completed = completed;
        return this;
    }
    
    public void resetLinks() {
        cancel = approve = reject = null;
        links.clear();
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
    
    @XmlElement(name="link")
    public List<Link> getLinks() { return links; }
    public void setLinks(List<Link> links) {
        this.links = links;
    }
    
    
}