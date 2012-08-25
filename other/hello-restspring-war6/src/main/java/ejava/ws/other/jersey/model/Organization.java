package ejava.ws.other.jersey.model;

import info.ejava.organization.Org;
import info.ejava.organization.Person;

import java.util.Date;
import java.util.List;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlTransient;

@XmlTransient //don't marshall the child -- only the JAXB parent
@Entity
public class Organization extends Org {
    private Date insertDate; //this property is exclusive to DB and not in XML

    @Id @GeneratedValue
    @Column(name="ID")
    @Override
    public int getId() { return super.getId(); }
    @Override
    public void setId(int value) { super.setId(value); }

    @Column(name="NAME")
    @Override
    public String getName() { return super.getName(); }
    @Override
    public void setName(String value) { super.setName(value); }

    @Column(name="INSERT_DATE", insertable=true, updatable=false)    
    public Date getInsertDate() { return insertDate; }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }
    
    @OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.DETACH})
    @JoinColumn
    @Override
    public List<Person> getMembers() {
        return super.getMembers();
    }
    public void setMembers(List<Person> members) {
        super.members = members;
    }
    
    @Override
    public String toString() {
        return new StringBuilder()
            .append("id=").append(id)
            .append(", name=").append(name)
            .append(", insertDate=").append(insertDate)
            .toString();
    }
    
    
}
