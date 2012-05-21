package ejava.examples.jaxrsscale.concurrency.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This data class is used to demonstrate and test the values being accessed
 * concurrently.
 */
@XmlRootElement(name="concurrencyCheck", namespace="http://dmv.ejava.info/concurrency")
@XmlType(name="ConcurrencyCheckType", namespace="http://dmv.ejava.info/concurrency")
public class ConcurrencyCheck {
    private int token;
    private String modifier;
    private Date modifiedDate;
    
    public int getToken() { return token; }
    public void setToken(int token) { 
        this.token = token; 
    }

    public Date getModifiedDate() { return new Date(1000*(modifiedDate.getTime()/1000)); }
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    
    public String getModifier() { return modifier; }
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
    
    @Override
    public int hashCode() {
        return token + modifiedDate.hashCode() + modifier.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("token=").append(token)
               .append(", modifier=").append(modifier)
               .append(", modifiedDate=").append(modifiedDate);
        return builder.toString();
    }
}