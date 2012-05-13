package ejava.examples.jaxrsrep.dmv.lic.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is used to represent a collection of results with paging values
 * included.
 */
@SuppressWarnings("serial")
@XmlType(namespace=DrvLicRepresentation.DRVLIC_NAMESPACE, name="ResultsType")
public class Results<T> implements List<T>{
    private ArrayList<T> impl = new ArrayList<T>();
    private int start;
    private int count;
    public Results() {}
    public Results(List<T> items, int start, int count) {
        this.addAll(items);
        this.start = start;
        this.count = count;
    }

    @XmlAttribute
    public int getSize() { return impl.size(); }

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
    
    @Override
    public boolean add(T arg0) {
        return impl.add(arg0);
    }
    @Override
    public void add(int arg0, T arg1) {
        impl.add(arg0, arg1);
    }
    @Override
    public boolean addAll(Collection<? extends T> arg0) {
        return impl.addAll(arg0);
    }
    @Override
    public boolean addAll(int arg0, Collection<? extends T> arg1) {
        return impl.addAll(arg0, arg1);
    }
    @Override
    public void clear() {
        impl.clear();
    }
    @Override
    public boolean contains(Object arg0) {
        return impl.contains(arg0);
    }
    @Override
    public boolean containsAll(Collection<?> arg0) {
        return impl.containsAll(arg0);
    }
    @Override
    public T get(int arg0) {
        return impl.get(arg0);
    }
    @Override
    public int indexOf(Object arg0) {
        return impl.indexOf(arg0);
    }
    @Override
    public boolean isEmpty() {
        return impl.isEmpty();
    }
    @Override
    public Iterator<T> iterator() {
        return impl.iterator();
    }
    @Override
    public int lastIndexOf(Object arg0) {
        return impl.lastIndexOf(arg0);
    }
    @Override
    public ListIterator<T> listIterator() {
        return impl.listIterator();
    }
    @Override
    public ListIterator<T> listIterator(int arg0) {
        return impl.listIterator(arg0);
    }
    @Override
    public boolean remove(Object arg0) {
        return impl.remove(arg0);
    }
    @Override
    public T remove(int arg0) {
        return impl.remove(arg0);
    }
    @Override
    public boolean removeAll(Collection<?> arg0) {
        return impl.removeAll(arg0);
    }
    @Override
    public boolean retainAll(Collection<?> arg0) {
        return impl.retainAll(arg0);
    }
    @Override
    public T set(int arg0, T arg1) {
        return impl.set(arg0, arg1);
    }
    @Override
    public int size() {
        return impl.size();
    }
    @Override
    public List<T> subList(int arg0, int arg1) {
        return impl.subList(arg0, arg1);
    }
    @Override
    public Object[] toArray() {
        return impl.toArray();
    }
    @Override
    public <T> T[] toArray(T[] arg0) {
        return impl.toArray(arg0);
    }
}
