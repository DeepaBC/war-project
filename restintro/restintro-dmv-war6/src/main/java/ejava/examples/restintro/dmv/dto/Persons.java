package ejava.examples.restintro.dmv.dto;

import java.util.List;

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
public class Persons  extends Results<Person>{
    public Persons() {}
    public Persons(List<Person> residents, int start, int count) {
        super(residents, start, count);
    }

    @XmlElement(name="resident")
    public List<Person> getPersons() {
        return this;
    }
}
