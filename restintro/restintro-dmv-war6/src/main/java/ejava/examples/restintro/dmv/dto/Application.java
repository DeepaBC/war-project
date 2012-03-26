package ejava.examples.restintro.dmv.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class provides a common base class for all applications in the system.
 */
@XmlRootElement(namespace="http://dmv.ejava.info")
@XmlType(namespace="http://dmv.ejava.info", name="ApplicationType", propOrder={
        "id", "updated", "created", "approved", "completed"
})
public abstract class Application {
    private long id;
    private Date created;
    private Date updated;
    private Date approved;
    private Date completed;

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
}