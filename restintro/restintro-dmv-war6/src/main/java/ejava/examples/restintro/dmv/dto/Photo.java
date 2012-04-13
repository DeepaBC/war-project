package ejava.examples.restintro.dmv.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a Photo for a person being identified within the 
 * DMV.
 */
@XmlRootElement(name="photo", namespace=Representation.DMV_NAMESPACE)
@XmlType(name="PhotoType", namespace=Representation.DMV_NAMESPACE, propOrder={
        "id", "timestamp", "image"
})
public class Photo extends Representation {
    private long id;
    private Date timestamp;
    private byte[] image;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }
}
