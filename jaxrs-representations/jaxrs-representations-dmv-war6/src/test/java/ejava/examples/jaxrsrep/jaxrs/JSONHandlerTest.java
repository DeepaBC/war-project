package ejava.examples.jaxrsrep.jaxrs;

import static org.junit.Assert.*;


import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
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
import ejava.examples.jaxrsrep.handlers.JSONHandlerDemoRS;
import ejava.util.rest.Link;

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
     * This helper method will marshal the provided JAXB object into an HttpEntity
     * suitable for issuing in an HttpPUT or POST.
     * @param jaxbObject
     * @return
     * @throws JAXBException
     */
    protected HttpEntity getXMLEntity(Object jaxbObject) throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(jaxbObject.getClass());
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(jaxbObject, bos);
        return new ByteArrayEntity(bos.toByteArray());
    }
    
    
    /**
     * This helper method will marshal the provided JAXB object into an HttpEntity
     * suitable for issuing in an HttpPUT or POST.
     * @param jaxbObject
     * @return
     * @throws JAXBException 
     * @throws UnsupportedEncodingException 
     */
    protected HttpEntity getJSONEntity(Object jaxbObject) 
            throws JAXBException, UnsupportedEncodingException {
        String jsonString = new JSONHandlerDemoRS().marshalMappedJSON(jaxbObject);
        return new StringEntity(jsonString);
    }
    
    protected HttpEntity getJSONEntityBadgerfish(Object jaxbObject) 
            throws JAXBException, UnsupportedEncodingException {
        String jsonString = new JSONHandlerDemoRS().marshalBadgerFishJSON(jaxbObject);
        return new StringEntity(jsonString);
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
        doTestAttributesJSON(new URI(xmlHandlerURI + "/attributes"),
                MediaType.APPLICATION_XML_TYPE, false);
    }
    @Test 
    public void testAttributesXMLBadgerfish() throws Exception {
        log.info("*** testAttributesXMLBadgerfish ***");
        doTestAttributesJSON(new URI(xmlHandlerURI + "/attributes/badgerfish"),
                MediaType.APPLICATION_XML_TYPE, true);
    }
    @Test 
    public void testAttributesJSON() throws Exception {
        log.info("*** testAttributesJSON ***");
        doTestAttributesJSON(new URI(xmlHandlerURI + "/attributes"),
                MediaType.APPLICATION_JSON_TYPE, false);
    }
    @Test 
    public void testAttributesJSONCustom() throws Exception {
        log.info("*** testAttributesJSONCustom ***");
        doTestAttributesJSON(new URI(xmlHandlerURI + "/attributes/custom"),
                MediaType.APPLICATION_JSON_TYPE, false);
    }
    @Test 
    public void testAttributesJSONBadgerfish() throws Exception {
        log.info("*** testAttributesJSONBadgerfish ***");
        doTestAttributesJSON(new URI(xmlHandlerURI + "/attributes/badgerfish"),
                MediaType.APPLICATION_JSON_TYPE, true);
    }
    @Ignore @Test //annotations not being passed to marshaller 
    public void testAttributesJSONBadgerfishCustom() throws Exception {
        log.info("*** testAttributesJSONBadgerfishCustom ***");
        doTestAttributesJSON(new URI(xmlHandlerURI + "/attributes/badgerfish/custom"),
                MediaType.APPLICATION_JSON_TYPE, true);
    }
    public void doTestAttributesJSON(URI uri, MediaType mt, boolean badgerfish) 
            throws Exception {
        //marshal a JAXB object that uses attributes 
        Link link = new Link("self");
        HttpEntity entity=null; 
        if (mt.equals(MediaType.APPLICATION_XML_TYPE)) {
            entity = getXMLEntity(link);
        }
        else if (mt.equals(MediaType.APPLICATION_JSON_TYPE) && !badgerfish) {
            entity = getJSONEntity(link);
        }
        else if (mt.equals(MediaType.APPLICATION_JSON_TYPE) && badgerfish) {
            entity = getJSONEntityBadgerfish(link);
        }
        else {
            fail("unknown mediaType:" + mt);
        }

        //build the HTTP PUT
        HttpPut put = new HttpPut(uri);
        put.setHeader("Content-Type", mt.toString());
        put.setHeader("Accept", MediaType.APPLICATION_JSON);
        
        //put the XML into the entity of the PUT
        put.setEntity(entity);
        HttpResponse response = httpClient.execute(put);
        log.debug("sent:{}", IOUtils.toString(entity.getContent()));
        try {
            assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
            assertEquals("unexpected Content-Type", 
                    MediaType.APPLICATION_JSON,
                    response.getFirstHeader("Content-Type").getValue());
            String jsonString = EntityUtils.toString(response.getEntity(), "UTF-8");
            log.debug("received json:{}", jsonString);
            Link link2 = !badgerfish ?
                new JSONHandlerDemoRS().demarshalMappedJSON(Link.class, jsonString) :
                new JSONHandlerDemoRS().demarshalBadgerFishJSON(Link.class, jsonString);
            assertEquals("unexpected link.rel", link.getRel(), link2.getRel());
            assertNotNull("unexpected link.href", link2.getHref());
            assertNotNull("unexpected link.type", link2.getType());
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
        doTestElementsJSON(new URI(xmlHandlerURI + "/elements"),
                MediaType.APPLICATION_XML_TYPE, false);
    }
    @Test
    public void testElementsXMLBadgerfish() throws Exception {
        log.info("*** testElementsXMLBadgerfish ***");
        doTestElementsJSON(new URI(xmlHandlerURI + "/elements/badgerfish"),
                MediaType.APPLICATION_XML_TYPE, true);
    }
    @Test
    public void testElementsJSON() throws Exception {
        log.info("*** testElementsJSON ***");
        doTestElementsJSON(new URI(xmlHandlerURI + "/elements"),
                MediaType.APPLICATION_JSON_TYPE, false);
    }
    @Test
    public void testElementsJSONBadgerfish() throws Exception {
        log.info("*** testElementsJSONBadgerfish ***");
        doTestElementsJSON(new URI(xmlHandlerURI + "/elements/badgerfish"),
                MediaType.APPLICATION_JSON_TYPE, true);
    }
    public void doTestElementsJSON(URI uri, MediaType mt, boolean badgerfish) throws Exception {
        //marshal a JAXB object that uses elements 
        ContactInfo contact = new ContactInfo()
            .setStreet("328 Chauncey Street")
            .setCity("Brooklyn")
            .setState("NY");
        HttpEntity entity=null; 
        if (mt.equals(MediaType.APPLICATION_XML_TYPE)) {
            entity = getXMLEntity(contact);
        }
        else if (mt.equals(MediaType.APPLICATION_JSON_TYPE) && !badgerfish) {
            entity = getJSONEntity(contact);
        }
        else if (mt.equals(MediaType.APPLICATION_JSON_TYPE) && badgerfish) {
            entity = getJSONEntityBadgerfish(contact);
        }
        else {
            fail("unknown mediaType:" + mt);
        }

        //build the HTTP PUT
        HttpPut put = new HttpPut(uri);
        put.setHeader("Content-Type", mt.toString());
        put.setHeader("Accept", MediaType.APPLICATION_JSON);
        
        //put the XML into the entity of the PUT
        put.setEntity(entity);
        HttpResponse response = httpClient.execute(put);
        log.debug("sent:{}", IOUtils.toString(entity.getContent()));
        try {
            assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
            assertEquals("unexpected Content-Type", 
                    MediaType.APPLICATION_JSON,
                    response.getFirstHeader("Content-Type").getValue());
            String jsonString = EntityUtils.toString(response.getEntity(), "UTF-8");
            log.debug("received json:{}", jsonString);
            ContactInfo contact2 = !badgerfish ?
                new JSONHandlerDemoRS().demarshalMappedJSON(ContactInfo.class, jsonString) :
                new JSONHandlerDemoRS().demarshalBadgerFishJSON(ContactInfo.class, jsonString);
            assertEquals("unexpected contact.street", contact.getStreet(), contact2.getStreet());
            assertEquals("unexpected contact.city", contact.getCity(), contact2.getCity());
            assertEquals("unexpected contact.state", contact.getState(), contact2.getState());
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
        doTestCollectionJSONWrapped(new URI(xmlHandlerURI + "/collection"),
                MediaType.APPLICATION_XML_TYPE, false);
    }
    @Test
    public void testCollectionXMLWrappedBadgerfish() throws Exception {
        log.info("*** testCollectionXMLWrappedBadgerfish ***");
        doTestCollectionJSONWrapped(new URI(xmlHandlerURI + "/collection/badgerfish"),
                MediaType.APPLICATION_XML_TYPE, true);
    }
    @Test
    public void testCollectionJSONWrapped() throws Exception {
        log.info("*** testCollectionJSONWrapped ***");
        doTestCollectionJSONWrapped(new URI(xmlHandlerURI + "/collection"),
                MediaType.APPLICATION_JSON_TYPE, false);
    }
    @Test
    public void testCollectionJSONWrappedBadgerfish() throws Exception {
        log.info("*** testCollectionJSONWrappedBadgerfish ***");
        doTestCollectionJSONWrapped(new URI(xmlHandlerURI + "/collection/badgerfish"),
                MediaType.APPLICATION_JSON_TYPE, true);
    }
    public void doTestCollectionJSONWrapped(URI uri, MediaType mt, boolean badgerfish) throws Exception {
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
        HttpEntity entity=null; 
        if (mt.equals(MediaType.APPLICATION_XML_TYPE)) {
            entity = getXMLEntity(person);
        }
        else if (mt.equals(MediaType.APPLICATION_JSON_TYPE) && !badgerfish) {
            entity = getJSONEntity(person);
        }
        else if (mt.equals(MediaType.APPLICATION_JSON_TYPE) && badgerfish) {
            entity = getJSONEntityBadgerfish(person);
        }
        else {
            fail("unknown mediaType:" + mt);
        }

        //build the HTTP PUT
        HttpPut put = new HttpPut(uri);
        put.setHeader("Content-Type", mt.toString());
        put.setHeader("Accept", MediaType.APPLICATION_JSON);
        
        //put the XML into the entity of the PUT
        put.setEntity(entity);
        HttpResponse response = httpClient.execute(put);
        log.debug("sent:{}", IOUtils.toString(entity.getContent()));
        try {
            assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
            assertEquals("unexpected Content-Type", 
                    MediaType.APPLICATION_JSON,
                    response.getFirstHeader("Content-Type").getValue());
            String jsonString = EntityUtils.toString(response.getEntity(), "UTF-8");
            log.debug("received json:{}", jsonString);
            Person person2 = !badgerfish ?
                new JSONHandlerDemoRS().demarshalMappedJSON(Person.class, jsonString) :
                new JSONHandlerDemoRS().demarshalBadgerFishJSON(Person.class, jsonString);
            assertEquals("unexpected person.firstName", person.getFirstName(), person2.getFirstName());
            assertEquals("unexpected person.lastName", person.getLastName(), person2.getLastName());
            assertEquals("unexpected person.contactInfo", 2, person2.getContactInfo().size());
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
        doTestCollectionJSONUnwrapped(new URI(xmlHandlerURI + "/collection"),
                MediaType.APPLICATION_XML_TYPE, false);
    }
    @Test
    public void testCollectionXMLUnwrappedBadgerfish() throws Exception {
        log.info("*** testCollectionXMLUnwrappedBadgerfish ***");
        doTestCollectionJSONUnwrapped(new URI(xmlHandlerURI + "/collection/badgerfish"),
                MediaType.APPLICATION_XML_TYPE, true);
    }
    @Test
    public void testCollectionJSONUnwrapped() throws Exception {
        log.info("*** testCollectionJSONUnwrapped ***");
        doTestCollectionJSONUnwrapped(new URI(xmlHandlerURI + "/collection"),
                MediaType.APPLICATION_JSON_TYPE, false);
    }
    @Test
    public void testCollectionJSONUnwrappedBadgerfish() throws Exception {
        log.info("*** testCollectionJSONUnwrappedBadgerfish ***");
        doTestCollectionJSONUnwrapped(new URI(xmlHandlerURI + "/collection/badgerfish"),
                MediaType.APPLICATION_JSON_TYPE, true);
    }
    public void doTestCollectionJSONUnwrapped(URI uri, MediaType mt, boolean badgerfish) throws Exception {
        //marshal a JAXB object that uses collection
        Person person = new Person("Peyton", "Manning");
        person.addLink(new Link("self"));
        person.addLink(new Link("center"));
        person.addLink(new Link("runningback"));
        person.addLink(new Link("receiver"));
        HttpEntity entity=null; 
        if (mt.equals(MediaType.APPLICATION_XML_TYPE)) {
            entity = getXMLEntity(person);
        }
        else if (mt.equals(MediaType.APPLICATION_JSON_TYPE) && !badgerfish) {
            entity = getJSONEntity(person);
        }
        else if (mt.equals(MediaType.APPLICATION_JSON_TYPE) && badgerfish) {
            entity = getJSONEntityBadgerfish(person);
        }
        else {
            fail("unknown mediaType:" + mt);
        }

        //build the HTTP PUT
        HttpPut put = new HttpPut(uri);
        put.setHeader("Content-Type", mt.toString());
        put.setHeader("Accept", MediaType.APPLICATION_JSON);
        
        //put the XML into the entity of the PUT
        put.setEntity(entity);
        HttpResponse response = httpClient.execute(put);
        log.debug("sent:{}", IOUtils.toString(entity.getContent()));
        try {
            assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
            assertEquals("unexpected Content-Type", 
                    MediaType.APPLICATION_JSON,
                    response.getFirstHeader("Content-Type").getValue());
            String jsonString = EntityUtils.toString(response.getEntity(), "UTF-8");
            log.debug("received json:{}", jsonString);
            Person person2 = !badgerfish ?
                new JSONHandlerDemoRS().demarshalMappedJSON(Person.class, jsonString) :
                new JSONHandlerDemoRS().demarshalBadgerFishJSON(Person.class, jsonString);
            assertEquals("unexpected person.firstName", person.getFirstName(), person2.getFirstName());
            assertEquals("unexpected person.lastName", person.getLastName(), person2.getLastName());
            assertEquals("unexpected person.links", 4, person2.getLinks().size());
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
        doTestXMLReference(new URI(xmlHandlerURI + "/reference"),
                MediaType.APPLICATION_XML_TYPE, false);
    }
    @Test
    public void testXMLReferenceBadgerfish() throws Exception {
        log.info("*** testXMLReferenceBadgerfish ***");
        doTestXMLReference(new URI(xmlHandlerURI + "/reference/badgerfish"),
                MediaType.APPLICATION_XML_TYPE, true);
    }
    @Test
    public void testJSONReference() throws Exception {
        log.info("*** testJSONReference ***");
        doTestXMLReference(new URI(xmlHandlerURI + "/reference"),
                MediaType.APPLICATION_JSON_TYPE, false);
    }
    @Test
    public void testJSONReferenceBadgerfish() throws Exception {
        log.info("*** testJSONReferenceBadgerfish ***");
        doTestXMLReference(new URI(xmlHandlerURI + "/reference/badgerfish"),
                MediaType.APPLICATION_JSON_TYPE, true);
    }
    public void doTestXMLReference(URI uri, MediaType mt, boolean badgerfish) throws Exception {
        //marshal a JAXB object
        ResidentID residentId = new ResidentID();
        Person person = new Person("Greg", "Williams");
        residentId.setIdentity(person);
        HttpEntity entity=null; 
        if (mt.equals(MediaType.APPLICATION_XML_TYPE)) {
            entity = getXMLEntity(residentId);
        }
        else if (mt.equals(MediaType.APPLICATION_JSON_TYPE) && !badgerfish) {
            entity = getJSONEntity(residentId);
        }
        else if (mt.equals(MediaType.APPLICATION_JSON_TYPE) && badgerfish) {
            entity = getJSONEntityBadgerfish(residentId);
        }
        else {
            fail("unknown mediaType:" + mt);
        }

        //build the HTTP PUT
        HttpPut put = new HttpPut(uri);
        put.setHeader("Content-Type", mt.toString());
        put.setHeader("Accept", MediaType.APPLICATION_JSON);
        
        //put the XML into the entity of the PUT
        put.setEntity(entity);
        HttpResponse response = httpClient.execute(put);
        log.debug("sent:{}", IOUtils.toString(entity.getContent()));
        try {
            assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
            assertEquals("unexpected Content-Type", 
                    MediaType.APPLICATION_JSON,
                    response.getFirstHeader("Content-Type").getValue());
            String jsonString = EntityUtils.toString(response.getEntity(), "UTF-8");
            log.debug("received json:{}", jsonString);
            ResidentID residentId2 = !badgerfish ?
                new JSONHandlerDemoRS().demarshalMappedJSON(ResidentID.class, jsonString) :
                new JSONHandlerDemoRS().demarshalBadgerFishJSON(ResidentID.class, jsonString);
            assertEquals("unexpected residentId.identity.firstName", 
                    residentId.getIdentity().getFirstName(), 
                    residentId2.getIdentity().getFirstName());
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
        doJaxbContextTest(new URI(xmlHandlerURI + "/jaxbContext"),
                MediaType.APPLICATION_XML_TYPE, false);
    }    
    @Test
    public void jaxbContextTestBadgerfish() throws Exception {
        log.info("*** jaxbContextTestBadgerfish ***");
        doJaxbContextTest(new URI(xmlHandlerURI + "/jaxbContext/badgerfish"),
                MediaType.APPLICATION_XML_TYPE, true);
    }
    @Test
    public void jaxbContextTestJSON() throws Exception {
        log.info("*** jaxbContextTestJSON ***");
        doJaxbContextTest(new URI(xmlHandlerURI + "/jaxbContext"),
                MediaType.APPLICATION_JSON_TYPE, false);
    }    
    @Test
    public void jaxbContextTestJSONBadgerfish() throws Exception {
        log.info("*** jaxbContextTestBadgerfish ***");
        doJaxbContextTest(new URI(xmlHandlerURI + "/jaxbContext/badgerfish"),
                MediaType.APPLICATION_JSON_TYPE, true);
    }
    public void doJaxbContextTest(URI uri, MediaType mt, boolean badgerfish) throws Exception {
        ResidentIDApplication resId = new ResidentIDApplication();
        Person person = new Person("cat", "inhat");
        resId.setIdentity(person);
        HttpEntity entity=null; 
        if (mt.equals(MediaType.APPLICATION_XML_TYPE)) {
            entity = getXMLEntity(resId);
        }
        else if (mt.equals(MediaType.APPLICATION_JSON_TYPE) && !badgerfish) {
            entity = getJSONEntity(resId);
        }
        else if (mt.equals(MediaType.APPLICATION_JSON_TYPE) && badgerfish) {
            entity = getJSONEntityBadgerfish(resId);
        }
        else {
            fail("unknown mediaType:" + mt);
        }

        //build the HTTP PUT
        HttpPut put = new HttpPut(uri);
        put.setHeader("Content-Type", mt.toString());
        put.setHeader("Accept", MediaType.APPLICATION_JSON);
        
        //put the XML into the entity of the PUT
        put.setEntity(entity);
        HttpResponse response = httpClient.execute(put);
        log.debug("sent:{}", IOUtils.toString(entity.getContent()));
        try {
            assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
            assertEquals("unexpected Content-Type", 
                    MediaType.APPLICATION_JSON,
                    response.getFirstHeader("Content-Type").getValue());
            String jsonString = EntityUtils.toString(response.getEntity(), "UTF-8");
            log.debug("received json:{}", jsonString);
            
            //TODO: register a BUG report. We are getting XML back for JSON payload
            /*
            Application app = !badgerfish ?
                new JSONHandlerDemoRS().demarshalMappedJSON(Application.class, jsonString) :
                new JSONHandlerDemoRS().demarshalBadgerfishJSON(Application.class, jsonString);
            assertEquals("unexpected firstName", 
                    resId.getIdentity().getFirstName(),
                    ((ResidentIDApplication)app).getIdentity().getFirstName());
                    */
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
}
