package ejava.examples.jaxrsrep.dmv.dto;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

import ejava.util.rest.Link;
import ejava.util.rest.Representation;

/**
 * This class contains provides a base implementation and namespace class
 * for DMV representations
 */
public class DmvRepresentation extends Representation {
    public static final String DMV_NAMESPACE="http://dmv.ejava.info";
    public static final String DMV_MEDIA_TYPE = "application/vnd.dmv.ejava+xml";
    public static final String DMV_DAP_NAMESPACE = DMV_NAMESPACE + "/dap";
    
    public static final String SELF_REL = makeRel("self");
    public static final String RESID_APP_REL = makeRel("residApp");

    @XmlElement(name="link", namespace=DMV_DAP_NAMESPACE)
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
        return Representation.makeRel(DMV_DAP_NAMESPACE, name);
    }
}
