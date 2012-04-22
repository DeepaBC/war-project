package ejava.exercises.simple.bank.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import ejava.exercises.simple.bank.dto.BankRepresentation.Link;

/**
 * This class represents a query response for a collection of accounts.
 */
@SuppressWarnings("serial")
@XmlRootElement(name="accounts", namespace=BankRepresentation.BANK_NAMESPACE)
@XmlType(name="AccountsType", namespace=BankRepresentation.BANK_NAMESPACE)
public class Accounts extends ArrayList<Account>{
    private int start;
    private int count;
    private List<BankRepresentation.Link> links=new ArrayList<BankRepresentation.Link>();
    
    public Accounts() {
        resetLinks();
    }
    public Accounts(int start, int count) {
        this();
        this.start = start;
        this.count = count;
    }
    
    @XmlElementWrapper(name="account", namespace=BankRepresentation.BANK_NAMESPACE)
    public List<Account> getAccounts() { return this; }
    public void setAccounts(List<Account> accounts) {
        if (accounts != this) {
            super.clear();
            super.addAll(accounts);
        }
    }
    
    @XmlAttribute
    public int getStart() { return start; }
    public void setStart(int start) {
        this.start = start;
    }
    
    @XmlAttribute
    public int getCount() { return count; }
    public void setCount(int count) {
        this.count = count;
    }
    
    @XmlAttribute
    public int getSize() { return super.size(); }
    @SuppressWarnings("unused")
    private void setSize(int size) {}


    @XmlElementWrapper(name="links")
    public List<BankRepresentation.Link> getLinks() { return links; }
    public void setLinks(List<BankRepresentation.Link> links) {
        this.links = links;
    }
    public void resetLinks() { 
        links.clear();
        links.add(new Link(BankRepresentation.SELF_REL));
        links.add(new Link(BankRepresentation.FIRST_REL));
        links.add(new Link(BankRepresentation.NEXT_REL));
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
    public String toXML() {
        try {
            return BankRepresentation.marshall(this);
        } catch (JAXBException ex) {
            throw new RuntimeException("error marshalling accounts XML", ex);
        }
    }
}
