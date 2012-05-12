package ejava.examples.jaxrsrep.dmv.lic.dto;

import java.util.ArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is used to represent a collection of results with paging values
 * included.
 */
@SuppressWarnings("serial")
@XmlType(namespace=DrvLicRepresentation.DRVLIC_NAMESPACE, name="ResultsType")
public class Results<T> extends ArrayList<T>{
    private int start;
    private int count;
    public Results() {}
    public Results(List<T> items, int start, int count) {
        this.addAll(items);
        this.start = start;
        this.count = count;
    }

    @XmlAttribute
    public int getSize() { return super.size(); }

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
}
