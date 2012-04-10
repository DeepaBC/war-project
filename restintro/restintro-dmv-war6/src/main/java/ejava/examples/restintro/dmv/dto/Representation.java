package ejava.examples.restintro.dmv.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;


public class Representation {
    public static final String DMV_NAMESPACE="http://dmv.ejava.info";
    public static final String DMVLIC_DAP_NAMESPACE = DMV_NAMESPACE + "/dap";
    public static final String DMVLIC_MEDIA_TYPE = "application/dmvlic.ejava+xml";
    
    public static final String SELF_REL = makeRel("self");
    public static final String APPROVE_REL = makeRel("approve");
    public static final String REJECT_REL = makeRel("reject");
    public static final String CANCEL_REL = makeRel("cancel");
    public static final String PAYMENT_REL = makeRel("payment");
    public static final String REFUND_REL = makeRel("refund");

    private List<Link> links = new ArrayList<Link>();
    
    
    @XmlElement(name="link", namespace=DMVLIC_DAP_NAMESPACE)
    public List<Link> getLinks() { return links; }
    protected void setLinks(List<Link> links) {
        this.links = links;
    }

    /**
     * Removes all existing links from representation
     */
    public void clearLinks() { links.clear(); }
    
    /**
     * adds new or updates existing link for representation
     * @param link
     */
    public void addLink(Link link) {
        Link existingLink = getLink(link.getRel());
        if (existingLink != null) {
            existingLink.setHref(link.getHref());
        }
        else {
            links.add(link);
        }
    }
    
    /**
     * This method returns the link matching the provided name
     * @param rel
     * @return
     */
    public Link getLink(String rel) {
        for (Link link : links) {
            if (link.getRel().equalsIgnoreCase(rel)) {
                return link;
            }
        }
        return null;
    }    
    
    /**
     * This helper method builds a fully qualified relationship name from a
     * unique name within the DAP.
     * @param name
     * @return
     */
    public static String makeRel(String name) {
        return new StringBuilder(Representation.DMVLIC_DAP_NAMESPACE)
            .append("#")
            .append(name)
            .toString();
    }
}
