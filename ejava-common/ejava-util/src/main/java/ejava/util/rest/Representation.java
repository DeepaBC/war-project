package ejava.util.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlType;


/**
 * This class provides a base representation and helper classes for all
 * representations.
 */
@XmlType(name="RepresentationType", namespace="http://ejava.info")
public class Representation {
    protected static final String COMMON_NAMESPACE = "http://ejava.info";
    protected static final String SELF="self";
    protected static final String SELF_FRAGMENT = "#" + SELF; 
    protected List<Link> links = new ArrayList<Link>();

    /**
     * Provides access to representation links.
     * @return
     */
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
     * This method will return the self link for this representation.
     * @return
     */
    public Link getSelf() {
        for (Link link : links) {
            if (link.getRel().endsWith(SELF_FRAGMENT)) {
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
    public static String makeRel(String dap, String name) {
        return new StringBuilder(dap)
            .append("#")
            .append(name)
            .toString();
    }
}
