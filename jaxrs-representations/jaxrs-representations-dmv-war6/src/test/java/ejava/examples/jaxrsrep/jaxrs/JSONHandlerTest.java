package ejava.examples.jaxrsrep.jaxrs;

import static org.junit.Assert.*;


import java.io.ByteArrayOutputStream;
import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.jaxrsrep.dmv.lic.dto.ContactInfo;
import ejava.examples.jaxrsrep.dmv.lic.dto.ContactType;
import ejava.examples.jaxrsrep.dmv.lic.dto.Person;
import ejava.examples.jaxrsrep.dmv.lic.dto.ResidentID;
import ejava.examples.jaxrsrep.dmv.lic.dto.ResidentIDApplication;
import ejava.util.rest.Link;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a local unit test demonstration of JAX-RS Methods.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={RepresentationsTestConfig.class})
public class JSONHandlerTest {
	protected static final Logger log = LoggerFactory.getLogger(JSONHandlerTest.class);
	protected static Server server;
	@Inject protected Environment env;
    @Inject protected URI appURI; 
    @Inject protected URI xmlHandlerURI; 
	@Inject protected HttpClient httpClient;
	
    @Before
    public void setUp() throws Exception {  
        startServer();
    }
    
    protected void startServer() throws Exception {
        if (appURI.getPort()>=9092) {
            if (server == null) {
                String path=env.getProperty("servletContext", "/");
                server = new Server(9092);
                WebAppContext context = new WebAppContext();
                context.setResourceBase("src/test/resources/local-web");
                context.setContextPath(path);
                context.setParentLoaderPriority(true);
                server.setHandler(context);
                server.start();
            }
        }
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception {
        if (server != null) {
            server.stop();
            server.destroy();
            server = null;
        }
    }
    
    /**
     * This method tests the basic capability to marshal a JAXB object to/from
     * a resource method. The class being used has no external dependencies and
     * uses XML attributes for marshalling.
     * @throws Exception
     */
    @Test 
    public void testAttributesXML() throws Exception {
        log.info("*** testAttributesXML ***");
        doTestAttributesXML(new URI(xmlHandlerURI + "/attributes"));
    }
    @Test 
    public void testAttributesXMLBadgerfish() throws Exception {
        log.info("*** testAttributesXMLBadgerfish ***");
        doTestAttributesXML(new URI(xmlHandlerURI + "/attributes/badgerfish"));
    }
    public void doTestAttributesXML(URI uri) throws Exception {
        //marshal a JAXB object that uses attributes 
        Link link = new Link("self");
        JAXBContext ctx = JAXBContext.newInstance(Link.class);
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(link, bos);

        //build the HTTP PUT
        HttpPut put = new HttpPut(uri);
        put.setHeader("Content-Type", MediaType.APPLICATION_XML);
        put.setHeader("Accept", MediaType.APPLICATION_JSON);
        
        //put the XML into the entity of the PUT
        put.setEntity(new ByteArrayEntity(bos.toByteArray()));
        HttpResponse response = httpClient.execute(put);
        log.debug("sent:{}", bos.toString());
        try {
            assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
            log.debug("received:{}", EntityUtils.toString(response.getEntity(), "UTF-8"));
            assertEquals("unexpected Content-Type", 
                    MediaType.APPLICATION_JSON,
                    response.getFirstHeader("Content-Type").getValue());
            /*
            Link link2 = (Link) unmarshaller.unmarshal(response.getEntity().getContent());
            log.debug("received:{}", link2);
            assertEquals("unexpected link.rel", link.getRel(), link2.getRel());
            assertNotNull("unexpected link.href", link2.getHref());
            assertNotNull("unexpected link.type", link2.getType());
            */
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
    
    /**
     * This method will test the ability of the resource method to again 
     * accept and return a JAXB object. This one simply address the use of 
     * XML elements -- but nothing technically new.
     * @throws Exception
     */
    @Test
    public void testElementsXML() throws Exception {
        log.info("*** testElementsXML ***");
        doTestElementsXML(new URI(xmlHandlerURI + "/elements"));
    }
    @Test
    public void testElementsXMLBadgerfish() throws Exception {
        log.info("*** testElementsXMLBadgerfish ***");
        doTestElementsXML(new URI(xmlHandlerURI + "/elements/badgerfish"));
    }
    public void doTestElementsXML(URI uri) throws Exception {
        //marshal a JAXB object that uses elements 
        ContactInfo contact = new ContactInfo()
            .setStreet("328 Chauncey Street")
            .setCity("Brooklyn")
            .setState("NY");
        JAXBContext ctx = JAXBContext.newInstance(ContactInfo.class);
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(contact, bos);

        //build the HTTP PUT
        HttpPut put = new HttpPut(uri);
        put.setHeader("Content-Type", MediaType.APPLICATION_XML);
        put.setHeader("Accept", MediaType.APPLICATION_JSON);
        
        //put the XML into the entity of the PUT
        put.setEntity(new ByteArrayEntity(bos.toByteArray()));
        HttpResponse response = httpClient.execute(put);
        log.debug("sent:{}", bos.toString());
        try {
            assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
            log.debug("received:{}", EntityUtils.toString(response.getEntity(), "UTF-8"));
            assertEquals("unexpected Content-Type", 
                    MediaType.APPLICATION_JSON,
                    response.getFirstHeader("Content-Type").getValue());
            /*
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            ContactInfo contact2 = (ContactInfo) unmarshaller.unmarshal(response.getEntity().getContent());
            log.debug("received:{}", contact2);
            assertEquals("unexpected contact.street", contact.getStreet(), contact2.getStreet());
            assertEquals("unexpected contact.city", contact.getCity(), contact2.getCity());
            assertEquals("unexpected contact.state", contact.getState(), contact2.getState());
            */
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
    
    /**
     * This method provides an example test of a JAXB class that uses a 
     * collection where all members are wrapped in a parent element.
     * @throws Exception
     */
    @Test
    public void testCollectionXMLWrapped() throws Exception {
        log.info("*** testCollectionXMLWrapped ***");
        doTestCollectionXMLWrapped(new URI(xmlHandlerURI + "/collection"));
    }
    @Test
    public void testCollectionXMLWrappedBadgerfish() throws Exception {
        log.info("*** testCollectionXMLWrappedBadgerfish ***");
        doTestCollectionXMLWrapped(new URI(xmlHandlerURI + "/collection/badgerfish"));
    }
    public void doTestCollectionXMLWrapped(URI uri) throws Exception {
        //marshal a JAXB object that uses collection
        Person person = new Person("Peyton", "Manning");
        person.addContactInfo()
            .setType(ContactType.OTHER)
            .setStreet("6325 N. Guilford; Suite 201")
            .setCity("Indianapolis")
            .setState("IN");
        person.addContactInfo()
            .setType(ContactType.WORK)
            .setCity("Denver")
            .setState("CO");
        JAXBContext ctx = JAXBContext.newInstance(Person.class);
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(person, bos);

        //build the HTTP PUT
        HttpPut put = new HttpPut(uri);
        put.setHeader("Content-Type", MediaType.APPLICATION_XML);
        put.setHeader("Accept", MediaType.APPLICATION_JSON);
        
        //put the XML into the entity of the PUT
        put.setEntity(new ByteArrayEntity(bos.toByteArray()));
        HttpResponse response = httpClient.execute(put);
        log.debug("sent:{}", bos.toString());
        try {
            assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
            log.debug("received:{}", EntityUtils.toString(response.getEntity(), "UTF-8"));
            assertEquals("unexpected Content-Type", 
                    MediaType.APPLICATION_JSON,
                    response.getFirstHeader("Content-Type").getValue());
            /*
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            Person person2 = (Person) unmarshaller.unmarshal(response.getEntity().getContent());
            log.debug("received:{}", person2);
            assertEquals("unexpected person.firstName", person.getFirstName(), person2.getFirstName());
            assertEquals("unexpected person.lastName", person.getLastName(), person2.getLastName());
            assertEquals("unexpected person.contactInfo", 2, person2.getContactInfo().size());
            */
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
    
    /**
     * This method provides a test of an example JAXB class that uses a collection
     * where each element are at global scope with the other elements.
     * @throws Exception
     */
    @Test
    public void testCollectionXMLUnwrapped() throws Exception {
        log.info("*** testCollectionXMLUnwrapped ***");
        doTestCollectionXMLUnwrapped(new URI(xmlHandlerURI + "/collection"));
    }
    @Test
    public void testCollectionXMLUnwrappedBadgerfish() throws Exception {
        log.info("*** testCollectionXMLUnwrappedBadgerfish ***");
        doTestCollectionXMLUnwrapped(new URI(xmlHandlerURI + "/collection/badgerfish"));
    }
    public void doTestCollectionXMLUnwrapped(URI uri) throws Exception {
        //marshal a JAXB object that uses collection
        Person person = new Person("Peyton", "Manning");
        person.addLink(new Link("self"));
        person.addLink(new Link("center"));
        person.addLink(new Link("runningback"));
        person.addLink(new Link("receiver"));
        JAXBContext ctx = JAXBContext.newInstance(Person.class);
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(person, bos);

        //build the HTTP PUT
        HttpPut put = new HttpPut(uri);
        put.setHeader("Content-Type", MediaType.APPLICATION_XML);
        put.setHeader("Accept", MediaType.APPLICATION_JSON);
        
        //put the XML into the entity of the PUT
        put.setEntity(new ByteArrayEntity(bos.toByteArray()));
        HttpResponse response = httpClient.execute(put);
        log.debug("sent:{}", bos.toString());
        try {
            assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
            log.debug("received:{}", EntityUtils.toString(response.getEntity(), "UTF-8"));
            assertEquals("unexpected Content-Type", 
                    MediaType.APPLICATION_JSON,
                    response.getFirstHeader("Content-Type").getValue());
            /*
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            Person person2 = (Person) unmarshaller.unmarshal(response.getEntity().getContent());
            log.debug("received:{}", person2);
            assertEquals("unexpected person.firstName", person.getFirstName(), person2.getFirstName());
            assertEquals("unexpected person.lastName", person.getLastName(), person2.getLastName());
            assertEquals("unexpected person.links", 4, person2.getLinks().size());
            */
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
    
    /**
     * This method provides a test of JAXB objects with single references to one 
     * another.
     * @throws Exception
     */
    @Test
    public void testXMLReference() throws Exception {
        log.info("*** testXMLReference ***");
        doTestXMLReference(new URI(xmlHandlerURI + "/reference"));
    }
    @Test
    public void testXMLReferenceBadgerfish() throws Exception {
        log.info("*** testXMLReferenceBadgerfish ***");
        doTestXMLReference(new URI(xmlHandlerURI + "/reference/badgerfish"));
    }
    public void doTestXMLReference(URI uri) throws Exception {
        //marshal a JAXB object
        ResidentID residentId = new ResidentID();
        Person person = new Person("Greg", "Williams");
        residentId.setIdentity(person);
        JAXBContext ctx = JAXBContext.newInstance(ResidentID.class);
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(residentId, bos);

        //build the HTTP PUT
        HttpPut put = new HttpPut(uri);
        put.setHeader("Content-Type", MediaType.APPLICATION_XML);
        put.setHeader("Accept", MediaType.APPLICATION_JSON);
        
        //put the XML into the entity of the PUT
        put.setEntity(new ByteArrayEntity(bos.toByteArray()));
        HttpResponse response = httpClient.execute(put);
        log.debug("sent:{}", bos.toString());
        try {
            assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
            log.debug("received:{}", EntityUtils.toString(response.getEntity(), "UTF-8"));
            assertEquals("unexpected Content-Type", 
                    MediaType.APPLICATION_JSON,
                    response.getFirstHeader("Content-Type").getValue());
            /*
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            ResidentID residentId2 = (ResidentID) unmarshaller.unmarshal(response.getEntity().getContent());
            log.debug("received:{}", residentId2);
            assertEquals("unexpected residentId.identity.firstName", 
                    residentId.getIdentity().getFirstName(), 
                    residentId2.getIdentity().getFirstName());
                    */
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
    
    /**
     * This test will verify the functionality to register a JAXBContext
     * with the provider so that it can properly demarshal objects that are
     * more than the simple/default case.
     */
    @Test
    public void jaxbContextTest() throws Exception {
        log.info("*** jaxbContextTest ***");
        doJaxbContextTest(new URI(xmlHandlerURI + "/jaxbContext"));
    }    
    @Test
    public void jaxbContextTestBadgerfish() throws Exception {
        log.info("*** jaxbContextTestBadgerfish ***");
        doJaxbContextTest(new URI(xmlHandlerURI + "/jaxbContext/badgerfish"));
    }
    public void doJaxbContextTest(URI uri) throws Exception {
        HttpPut put = new HttpPut(uri);
        put.setHeader("Content-Type", MediaType.APPLICATION_XML);
        put.setHeader("Accept", MediaType.APPLICATION_JSON);
        ResidentIDApplication resId = new ResidentIDApplication();
        Person person = new Person("cat", "inhat");
        resId.setIdentity(person);
        put.setEntity(new StringEntity(JAXBHelper.toString(resId)));
        HttpResponse response = httpClient.execute(put);
        try {
            assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
            log.debug("received:{}", EntityUtils.toString(response.getEntity(), "UTF-8"));
            assertEquals("unexpected Content-Type", 
                    MediaType.APPLICATION_JSON,
                    response.getFirstHeader("Content-Type").getValue());
            /*
            Application app = JAXBHelper.unmarshall(
                    response.getEntity().getContent(), 
                    ResidentIDApplication.class, null,
                    ResidentIDApplication.class,
                    Application.class);
            assertEquals("unexpected firstName", 
                    resId.getIdentity().getFirstName(),
                    ((ResidentIDApplication)app).getIdentity().getFirstName());
                    */
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
}
