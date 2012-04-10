package ejava.examples.restintro.dmv.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is used to represent a collection of persons as a root
 * element.
 */
@SuppressWarnings("serial")
@XmlRootElement(namespace=Representation.DMV_NAMESPACE)
@XmlType(namespace="http://dmv.ejava.info", name="PersonsType")
public class Persons extends ArrayList<Person>{
    private int start;
    private int count;
    public Persons() {}
    public Persons(List<Person> residents, int start, int count) {
        this.addAll(residents);
        this.start = start;
        this.count = count;
    }

    @XmlElement(name="resident")
    public List<Person> getPersons() {
        return this;
    }
    
    @XmlAttribute
    public int getSize() { return super.size(); }

    @XmlAttribute
    public int getStart() { return start; }
    public void setStart(int start) {
        this.start = start;
    }

    @XmlAttribute
    public int getCount() { return count; }
    public void setCount(int count) {
        this.count = count;
    }
}
