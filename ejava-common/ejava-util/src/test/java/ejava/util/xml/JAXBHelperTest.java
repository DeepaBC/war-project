package ejava.util.xml;

import static org.junit.Assert.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a test of the JAXBHelper.
 */
public class JAXBHelperTest {
    private static final Logger log = LoggerFactory.getLogger(JAXBHelperTest.class);

    @XmlRootElement(name="person", namespace="http://ejava.info/util/test")
    @XmlType(name="PersonType", namespace="http://ejava.info/util/test")
    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class Person {
        private String firstName;
        private String lastName;
        
        public Person() {}
        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
        public String getFirstName() {
            return firstName;
        }
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        public String getLastName() {
            return lastName;
        }
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }        
    }
    
    @Test
    public void testMarshall() throws Exception {
        log.info("*** testMarshall ***");
        
        Person p1 = new Person("cat", "inhat");
        byte[] data = JAXBHelper.marshall(p1, null, Person.class);
        log.info("marshalled person={}", new String(data));
        
        Person p2 = JAXBHelper.unmarshall(data, Person.class, null, Person.class);
        assertEquals("unexpected firstName", p1.getFirstName(), p2.getFirstName());
        assertEquals("unexpected lastName", p1.getLastName(), p2.getLastName());
    }
}
