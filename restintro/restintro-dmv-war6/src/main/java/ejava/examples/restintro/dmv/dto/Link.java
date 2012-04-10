package ejava.examples.restintro.dmv.dto;

import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a link for an application-specific hyperlink. The
 * link relations are scoped within a DMV DAP namespace.
 */
@XmlRootElement(namespace=Representation.DMVLIC_DAP_NAMESPACE)
@XmlType(namespace=Representation.DMVLIC_DAP_NAMESPACE, name="LinkType", propOrder={
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
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("rel=").append(rel)
               .append(", href=").append(href);
        return builder.toString();
    }   
}
