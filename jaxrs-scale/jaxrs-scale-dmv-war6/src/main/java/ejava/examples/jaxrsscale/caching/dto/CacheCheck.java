package ejava.examples.jaxrsscale.caching.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This data class is used to demonstrate and test the values being cached.
 */
@XmlRootElement(name="cacheCheck", namespace="http://dmv.ejava.info/caching")
@XmlType(name="CacheCheckType", namespace="http://dmv.ejava.info/caching")
public class CacheCheck {
    private int token;
    private Date calledDate;
    private Date expiresDate;
    
    public int getToken() { return token; }
    public void setToken(int token) { 
        this.token = token; 
    }

    public Date getCalledDate() { return calledDate; }
    public void setCalledDate(Date calledDate) {
        this.calledDate = calledDate;
    }
    
    public Date getExpiresDate() { return expiresDate; }
    public void setExpiresDate(Date expiresDate) {
        this.expiresDate = expiresDate;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("token=").append(token)
               .append(", calledDate=").append(calledDate)
               .append(", expiresDate=").append(expiresDate);
        return builder.toString();
    }
}
