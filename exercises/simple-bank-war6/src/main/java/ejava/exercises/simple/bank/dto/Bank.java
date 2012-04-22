package ejava.exercises.simple.bank.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a DTO representation of the overall bank operation.
 */
@XmlRootElement(name="bank", namespace=BankRepresentation.BANK_NAMESPACE)
@XmlType(name="BankType", namespace=BankRepresentation.BANK_NAMESPACE)
public class Bank extends BankRepresentation {
    private String name;
    private float totalAssets;
    
    public Bank() {
        super.getLinks().add(new Link(BankRepresentation.ACCOUNTS_REL));
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public float getTotalAssets() { return totalAssets; }
    public void setTotalAssets(float totalAssets) {
        this.totalAssets = totalAssets;
    }
}
