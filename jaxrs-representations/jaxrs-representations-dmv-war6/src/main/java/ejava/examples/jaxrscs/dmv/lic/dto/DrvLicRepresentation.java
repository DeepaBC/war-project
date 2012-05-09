package ejava.examples.jaxrscs.dmv.lic.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import ejava.util.rest.Link;
import ejava.util.rest.Representation;

/**
 * This class provides the base representation properties and helper classes
 * for all driver licensing representations.
 */
public class DrvLicRepresentation extends Representation {
    public static final String DRVLIC_NAMESPACE="http://dmv.ejava.info/drvlic";
    public static final String DRVLIC_DAP_NAMESPACE = DRVLIC_NAMESPACE + "/dap";
    public static final String DRVLIC_MEDIA_TYPE = "application/vnd.dmvlic.ejava+xml";
    
    public static final String SELF_REL = makeRel("self");
    public static final String APPROVE_REL = makeRel("approve");
    public static final String REJECT_REL = makeRel("reject");
    public static final String CANCEL_REL = makeRel("cancel");
    public static final String PAYMENT_REL = makeRel("payment");
    public static final String REFUND_REL = makeRel("refund");
    public static final String RESID_REL = makeRel("residentID");
    
    public static final String DRVLIC_REL = makeRel("driverLicenses");
    public static final String PHOTO_REL = makeRel("photo");
    public static final String CREATE_PHOTO_REL = makeRel("createPhoto");
    public static final String SET_PHOTO_REL = makeRel("setPhoto");

    @XmlElement(name="link", namespace=DRVLIC_DAP_NAMESPACE)
    public List<Link> getLinks() { return links; }
    protected void setLinks(List<Link> links) {
        this.links = links;
    }

    /**
     * This helper method builds a fully qualified relationship name from a
     * unique name within the DAP.
     * @param name
     * @return
     */
    public static String makeRel(String name) {
        return makeRel(DRVLIC_DAP_NAMESPACE, name);
    }
}
