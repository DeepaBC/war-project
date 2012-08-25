package ejava.ws.other.jersey.xml;

import static org.junit.Assert.*;


import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import info.ejava.organization.Org;
import info.ejava.organization.Person;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.ws.other.jersey.model.Organization;

/**
 * Provides an example/test of using a custom class derived from a JAXB-class
 * and being able to marshall and unmarshall using the custom class.
 */
public class MyOjectFactoryTest {
    private static final Logger log = LoggerFactory.getLogger(MyOjectFactoryTest.class);

    public static Org createOrg() {
        Org org = new Org();
        org.setName("exampleOrg");
        for (int i=0; i<3; i++) {
            info.ejava.organization.Person person = new info.ejava.organization.Person();
            person.setFirstName("examplePerson");
            person.setLastName("" +i);
            org.getMembers().add(person);
        }
        return org;
    }

    private static Organization createOrganization() {
        Organization org = new Organization();
        org.setName("exampleOrg");
        for (int i=0; i<3; i++) {
            Person person = new Person();
            person.setFirstName("examplePerson");
            person.setLastName("" +i);
            org.getMembers().add(person);
        }
        return org;
    }
    
    /**
     * This test verifies that the XSD-generated classes marshall/demarshall
     * out-of-the-box.
     * @throws Exception
     */
    @Test
    public void testDefaultFactory() throws Exception {
        log.info("*** testDefaultFactory ***");
        
        Org org = createOrg();
        
        JAXBContext ctx = JAXBContext.newInstance(Org.class);
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(org, writer);
        String xml = writer.toString();
        log.debug(xml);
        
        ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        Org org2=(Org) unmarshaller.unmarshal(bis);
        //we should get our XJC-generated Org
        assertEquals("unexpected implementation class", Org.class, org2.getClass());
        for (info.ejava.organization.Person p : org2.getMembers()) {
            assertEquals("unexpected implementation class", 
                    info.ejava.organization.Person.class, 
                    p.getClass());
        }
    }
    
    
    /**
     * This tests our implementation of the JAXBContext will provide a 
     * marshaller and demarshaller that can process our custom Organization.
     * @throws Exception
     */
    @Test
    public void testMyObjectFactory() throws Exception {
        log.info("*** testCustom ***");

        Organization org = createOrganization();
        
        JAXBContext ctx = new MyJAXBContext();
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(org, writer);
        String xml = writer.toString();
        log.debug(xml);
        
        ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        unmarshaller.setProperty("com.sun.xml.bind.ObjectFactory", new MyObjectFactory());
        Org org2=(Org) unmarshaller.unmarshal(bis);
        //we should get our custom class and not the XJC-generated Org
        assertEquals("unexpected implementation class", Organization.class, org2.getClass());
        for (Person p : org2.getMembers()) {
            assertEquals("unexpected implementation class", 
                    info.ejava.organization.Person.class, 
                    p.getClass());
        }
    }
}
