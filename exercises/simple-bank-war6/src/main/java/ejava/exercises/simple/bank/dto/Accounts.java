package ejava.exercises.simple.bank.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.sun.xml.txw2.annotation.XmlAttribute;

/**
 * This class represents a query response for a collection of accounts.
 */
@SuppressWarnings("serial")
@XmlRootElement(name="accounts", namespace=BankRepresentation.BANK_NAMESPACE)
@XmlType(name="AccountsType", namespace=BankRepresentation.BANK_NAMESPACE)
public class Accounts extends ArrayList<Account>{
    private int start;
    private int count;
    
    public Accounts() {}
    public Accounts(int start, int count) {
        this.start = start;
        this.count = count;
    }
    
    @XmlElementWrapper(name="account", namespace=BankRepresentation.BANK_NAMESPACE)
    public List<Account> getAccounts() { return this; }
    public void setAccounts(List<Account> accounts) {
        super.clear();
        super.addAll(accounts);
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
}
