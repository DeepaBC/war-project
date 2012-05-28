package ejava.examples.ejbear6.dmv.lic.dto;


import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a request to the DMV for a resident ID. The 
 * successful product of this process will be an ID card as well as a
 * registration in the residents DB.
 */
@XmlRootElement(namespace=DrvLicRepresentation.DRVLIC_NAMESPACE)
@XmlType(namespace=DrvLicRepresentation.DRVLIC_NAMESPACE, name="ResidentIDAppType")
public class ResidentIDApplication extends Application {
    private Person identity;
    private ResidentID resid;
    
    public Person getIdentity() {
        return identity;
    }
    public ResidentIDApplication setIdentity(Person identity) {
        this.identity = identity;
        return this;
    }
    
    @XmlTransient
    public ResidentID getResid() { return resid; }
    public void setResid(ResidentID resid) {
        this.resid = resid;
    }
    
    
}
