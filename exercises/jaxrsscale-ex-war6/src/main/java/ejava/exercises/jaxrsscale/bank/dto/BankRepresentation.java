package ejava.exercises.jaxrsscale.bank.dto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class provides a base implementation and namespace class
 * for Bank representations
 */
@XmlType(name="BankRepresentationType", namespace=BankRepresentation.BANK_NAMESPACE, propOrder={
        "updated", "links"
})
@XmlAccessorType(XmlAccessType.PROPERTY)
public class BankRepresentation {
    public static final String BANK_NAMESPACE="http://dmv.ejava.info";
    
    @XmlType(name="LinkType", namespace=BANK_NAMESPACE)
    public static class Link {
        private String rel;
        private URI href;
        public Link() {}
        public Link(String rel) { this.rel = rel; }
        public Link(String rel, URI href) { this(rel); this.href = href; }
        
        public String getRel() { return rel; }
        public void setRel(String rel) {
            this.rel = rel;
        }
        public URI getHref() { return href; }
        public void setHref(URI href) {
            this.href = href;
        }
        
    }
    public static final String SELF_REL = "self";
    public static final String ACCOUNTS_REL = "accounts";
    public static final String DEPOSIT_REL = "deposit";
    public static final String WITHDRAW_REL = "withdraw";
    public static final String TRANSFER_REL = "transfer";
    public static final String FIRST_REL = "first";
    public static final String NEXT_REL = "next";
    
    private List<Link> links = new ArrayList<BankRepresentation.Link>();
    private Date updated;
    
    public BankRepresentation() {
        resetLinks();
    }

    @XmlElement(required=false)
    public Date getUpdated() { return updated; }
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @XmlElement(name="link", namespace=BANK_NAMESPACE)
    public List<Link> getLinks() { return links; }
    protected void setLinks(List<Link> links) {
        this.links = links;
    }
    public Link getLink(String rel) {
        if (rel == null) { return null; }
        for (Link link: links) {
            if (rel.equals(link.getRel())) {
                return link;
            }
        }
        return null;
    }
    
    public void resetLinks() {
        links.clear();
        links.add(new Link(SELF_REL));
    }

    public static String makeRel(String name) {
        return name;
    }
    
    public String toXML() {
        try {
            return marshall(this);
        } catch (JAXBException ex) {
            throw new RuntimeException("error marshalling XML representation", ex);
        } 
    }

    /**
     * This helper function will marshall the provided object as an XML
     * String.
     * @param object
     * @param classes
     * @return
     * @throws JAXBException
     */
    public static String marshall(Object object, Class<?>...classes) 
            throws JAXBException {
        if (object == null) { return null; }
        Class<?>[] clazzes= new Class[classes.length+1];
        clazzes[0] = object.getClass();
        System.arraycopy(classes, 0, clazzes, 1, classes.length);
        JAXBContext ctx = JAXBContext.newInstance(clazzes);
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(object, bos);
        return bos.toString();
    }
    
    public static <T> T unmarshall(
            InputStream is, Class<T> type, Class<?>...classes) 
            throws JAXBException, IOException {
        try {
            Class<?>[] clazzes= new Class[classes.length+1];
            clazzes[0] = type;
            System.arraycopy(classes, 0, clazzes, 1, classes.length);
            JAXBContext ctx = JAXBContext.newInstance(clazzes);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            @SuppressWarnings("unchecked")
            T object = (T) unmarshaller.unmarshal(is);
            return object;
        }
        finally {
            is.close();
        }
    }
}
