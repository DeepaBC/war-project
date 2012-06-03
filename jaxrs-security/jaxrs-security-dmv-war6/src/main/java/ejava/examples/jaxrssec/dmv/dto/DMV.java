package ejava.examples.jaxrssec.dmv.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import ejava.examples.jaxrssec.dmv.lic.dto.DrvLicRepresentation;
import ejava.util.rest.Link;

/**
 * This class represents the highest level entry point into the DMV.
 * From this representation, clients can locate services and other resources 
 * available within the DMV.
 */
@XmlRootElement(name="dmv", namespace=DmvRepresentation.DMV_NAMESPACE)
@XmlType(name="DmvType", namespace=DmvRepresentation.DMV_NAMESPACE)
public class DMV extends DmvRepresentation {
    //properties here like address, hours of operations, etc.
    public DMV() {
        //normally this would be driven by a property file, DB table, etc.
        addLink(new Link(SELF_REL, DMV_MEDIA_TYPE));
        addLink(new Link(RESID_APP_REL, DrvLicRepresentation.DRVLIC_MEDIA_TYPE));
    }
}
