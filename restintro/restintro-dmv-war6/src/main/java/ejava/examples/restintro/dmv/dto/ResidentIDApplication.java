package ejava.examples.restintro.dmv.dto;


import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a request to the DMV for a resident ID. The 
 * successful product of this process will be an ID card as well as a
 * registration in the residents DB.
 */
@XmlRootElement(namespace=Representation.DMV_NAMESPACE)
@XmlType(namespace=Representation.DMV_NAMESPACE, name="ResidentIDAppType")
public class ResidentIDApplication extends Application {
    private Person identity;
    
    public Person getIdentity() {
        return identity;
    }
    public ResidentIDApplication setIdentity(Person identity) {
        this.identity = identity;
        return this;
    }
}
