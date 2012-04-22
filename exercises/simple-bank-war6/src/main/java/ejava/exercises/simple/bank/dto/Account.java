package ejava.exercises.simple.bank.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="account", namespace=BankRepresentation.BANK_NAMESPACE)
@XmlType(name="AccountType", namespace=BankRepresentation.BANK_NAMESPACE)
public class Account extends BankRepresentation {
    private int id;
    private String ownerName;
    private float balance;

    
    @Override
    public void resetLinks() {
        super.resetLinks();
        getLinks().add(new BankRepresentation.Link(BankRepresentation.ACCOUNTS_REL));
        getLinks().add(new BankRepresentation.Link(BankRepresentation.DEPOSIT_REL));
        if (balance < 0) {
            getLinks().add(new BankRepresentation.Link(BankRepresentation.WITHDRAW_REL));
        }
        getLinks().add(new BankRepresentation.Link(BankRepresentation.TRANSFER_REL));
    }
    
    
    public int getId() { return id; }
    public void setId(int id) {
        this.id = id;
    }
    
    @XmlElement(required=true)
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public float getBalance() { return balance; }
    public void setBalance(float balance) {
        this.balance = balance;
    }
    
    public void deposit(float amount) {
        balance += amount;
    }
    public void withdraw(float amount) {
        balance -= amount;
    }
}
