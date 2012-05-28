package ejava.examples.ejbear6.dmv.lic.dto;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is used to represent a collection of applications as a root
 * element.
 */
@XmlRootElement(namespace=DrvLicRepresentation.DRVLIC_NAMESPACE)
@XmlType(namespace=DrvLicRepresentation.DRVLIC_NAMESPACE, name="ApplicationsType")
public class Applications extends Results<Application>{
    public Applications() {}
    public Applications(List<Application> apps, int start, int count) {
        super(apps, start, count);
    }

    @XmlElement(name="application")
    public List<Application> getApplications() {
        return this;
    }
}
