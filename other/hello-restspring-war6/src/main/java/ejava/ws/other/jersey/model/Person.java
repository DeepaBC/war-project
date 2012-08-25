package ejava.ws.other.jersey.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="person", namespace="http://ejava.info/jersey/")
@XmlType(name="PersonType", namespace="http://ejava.info/jersey/", propOrder={
        "id", "firstName", "lastName", "created", "lastModified"
})
@Entity
@Table(name="PERSON", uniqueConstraints={
        @UniqueConstraint(columnNames={"FIRST_NAME","LAST_NAME", "CREATED"
        })
})
public class Person {
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    
    @Column(name="FIRST_NAME")
    private String firstName;
    
    @Column(name="LAST_NAME")
    private String lastName;
    
    @Column(name="CREATED", nullable=false, updatable=false)
    private Date created;
    
    @Column(name="MODIFIED", nullable=true) //to make compatable with JAXB class mapped to same table
    private Date lastModified;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public Date getCreated() { return created; }
    public void setCreated(Date created) {
        this.created = created;
    }
    
    @PrePersist
    public void supplyCreated() {
        if (created == null) {
            created = new Date();
            lastModified = created;
        }
    }
    
    public Date getLastModified() { return lastModified; }
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    
    @Override
    public boolean equals(Object obj) {
        try {
            if (obj == this) { return true; }
            Person rhs = (Person)obj;
            supplyCreated();
            return rhs.created.getTime() == created.getTime() &&
                    rhs.getFirstName().equals(firstName) &&
                    rhs.getLastName().equals(lastName);
        } catch (Exception ex) {
            return false;
        }
    }
    @Override
    public int hashCode() {
        return created.hashCode();
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(firstName)
               .append(", ").append(lastName);
        return builder.toString();
    }

    
    
}
