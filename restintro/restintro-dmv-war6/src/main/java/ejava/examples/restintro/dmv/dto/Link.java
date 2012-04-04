package ejava.examples.restintro.dmv.dto;

import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a link for an application-specific hyperlink
 */
@XmlRootElement(namespace="http://dmv.ejava.info")
@XmlType(namespace="http://dmv.ejava.info", name="LinkType", propOrder={
        "rel", "href"
})
public class Link {
    private String rel;
    private URI href;
  
    public Link(){}
    public Link(String rel, URI href) {
        this.rel = rel;
        this.href = href;
    }

    public String getRel() { return rel; }
    public void setRel(String rel) {
        this.rel = rel;
    }

    public URI getHref() { return href; }
    public void setHref(URI href) {
        this.href = href;
    }
    
}
