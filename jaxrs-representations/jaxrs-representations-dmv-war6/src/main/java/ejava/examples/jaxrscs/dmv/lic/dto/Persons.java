package ejava.examples.jaxrscs.dmv.lic.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is used to represent a collection of persons as a root
 * element.
 */
@SuppressWarnings("serial")
@XmlRootElement(namespace=DrvLicRepresentation.DRVLIC_NAMESPACE)
@XmlType(namespace=DrvLicRepresentation.DRVLIC_NAMESPACE, name="PersonsType")
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
