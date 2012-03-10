package ejava.examples.restintro.rest.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is used to represent a collection of residents as a root
 * element.
 */
@SuppressWarnings("serial")
@XmlRootElement(namespace="http://dmv.ejava.info")
@XmlType(namespace="http://dmv.ejava.info", name="ResidentsType")
public class Residents extends ArrayList<Resident>{
    private int start;
    private int count;
    public Residents() {}
    public Residents(List<Resident> residents, int start, int count) {
        this.addAll(residents);
        this.start = start;
        this.count = count;
    }

    @XmlElement(name="resident")
    public List<Resident> getResidents() {
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
