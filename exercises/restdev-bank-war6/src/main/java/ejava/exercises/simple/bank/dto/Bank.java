package ejava.exercises.simple.bank.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a DTO representation of the overall bank operation.
 */
@XmlRootElement(name="bank", namespace=BankRepresentation.BANK_NAMESPACE)
@XmlType(name="BankType", namespace=BankRepresentation.BANK_NAMESPACE, propOrder={
        "name", "totalAssets"
})
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Bank extends BankRepresentation {
    private String name;
    private float totalAssets;
    
    @Override
    public void resetLinks() {
        super.resetLinks(); 
        if (name != null) { //we're open!
            super.getLinks().add(new Link(BankRepresentation.ACCOUNTS_REL));
        }
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
