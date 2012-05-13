package ejava.util.rest;

import java.net.URI;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a link for a specific hyperlink.
 */
@XmlRootElement(name="link", namespace=Representation.COMMON_NAMESPACE)
@XmlType(name="LinkType", namespace=Representation.COMMON_NAMESPACE)
public class Link {
    /**
     * A unique name for the hyperlink 
     */
    private String rel;
    /**
     * A web-addressible URL for the resource
     */
    private URI href;
    /**
     * Representation type when accessing the resource.
     */
    private String type;
  
    public Link(){}
    public Link(String rel) {
        this.rel = rel;
    }
    public Link(String rel, URI href) {
        this.rel = rel;
        this.href = href;
    }
    public Link(String rel, URI href, String type) {
        this(rel, href);
        this.type = type;
    }    
    public Link(String rel, String type) {
        this(rel);
        this.type = type;
    }    
    
    @XmlAttribute
    public String getRel() { return rel; }
    public void setRel(String rel) {
        this.rel = rel;
    }

    @XmlAttribute
    public URI getHref() { return href; }
    public void setHref(URI href) {
        this.href = href;
    }
    
    @XmlAttribute
    public String getType() { return type; }
    public void setType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("rel=").append(rel)
               .append(", href=").append(href)
               .append(", type=").append(type);
        return builder.toString();
    }   
}
