package ejava.examples.jaxrssec.dmv;

import static org.junit.Assert.*;

import java.util.Date;


import javax.inject.Inject;

import org.apache.http.client.HttpClient;
import org.jboss.resteasy.spi.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.jaxrssec.dmv.lic.dto.Application;
import ejava.examples.jaxrssec.dmv.lic.dto.Applications;
import ejava.examples.jaxrssec.dmv.lic.dto.ContactInfo;
import ejava.examples.jaxrssec.dmv.lic.dto.ContactType;
import ejava.examples.jaxrssec.dmv.lic.dto.Person;
import ejava.examples.jaxrssec.dmv.lic.dto.ResidentIDApplication;
import ejava.examples.jaxrssec.dmv.svc.ApplicationsService;
import ejava.examples.jaxrssec.dmv.svc.BadArgument;
import ejava.examples.jaxrssec.rest.ApplicationsServiceProxy;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a local unit test of the ApplicationsService prior 
 * to deploying to the server.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class})
public class ApplicationsServiceTest {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected @Inject ApplicationsService svcImpl;
	protected @Inject ApplicationContext ctx;
	
	@Before
	public void setUp() throws Exception {	
	    log.debug("=== ApplicationsServiceTest.setUp() ===");
        log.debug("svcImpl=" + svcImpl);
        cleanup();
	}
	
	protected void cleanup() {
	    asAdmin();
	    svcImpl.purgeApplications();
	}
	
	protected HttpClient asUser(String beanName) {
        HttpClient client = ctx.getBean(beanName, HttpClient.class);
        if (svcImpl instanceof ApplicationsServiceProxy) {
            ((ApplicationsServiceProxy)svcImpl).setHttpClient(client);
        }
        return client;
	}
    protected HttpClient asAnonymous() { return asUser("httpClient"); }
    protected HttpClient asAdmin() { return asUser("adminClient"); }
    protected HttpClient asUser() { return asUser("userClient"); }
    
	
	/**
	 * This test verifies that an application can be created.
	 * @throws Exception 
	 */
	@Test
	public void testCreateApplication() throws Exception {
		log.info("*** testCreateApplication ***");
		Person person = new Person();
		person.setFirstName("cat");
		person.setLastName("inhat");
		ContactInfo info = new ContactInfo();
		info.setType(ContactType.RESIDENCE);
		info.setStreet("1600 Penn Ave, NW");
		info.setCity("Washington");
		info.setState("DC");
		info.setZip("20500");
		person.getContactInfo().add(info);
		
		ResidentIDApplication expected = new ResidentIDApplication()
		    .setIdentity(person);
		
		    //verify how many residents exist
		int appsCountStart = svcImpl.getApplications(null, 0, 0).size();
		
		    //create an application
		Application actual = svcImpl.createApplication(expected);
		    //verify a sample amount of properties from the return value
		log.debug("{}", JAXBHelper.toString(actual));
		assertNotNull("null resident", actual);
        assertTrue("id unassigned", actual.getId() > 0);
        assertNotNull("null created date", actual.getCreated());
        assertNotNull("null updated date", actual.getUpdated());
        assertEquals("unexpected update date", actual.getCreated().getTime(), actual.getUpdated().getTime());
		assertEquals("firstName", person.getFirstName(), ((ResidentIDApplication)actual).getIdentity().getFirstName());
        assertEquals("lastName", person.getLastName(), ((ResidentIDApplication)actual).getIdentity().getLastName());
        assertEquals("contacts", 
                person.getContactInfo().size(), 
                ((ResidentIDApplication)actual).getIdentity().getContactInfo().size());
        assertEquals("contacts.street", 
                person.getContactInfo().get(0).getStreet(), 
                ((ResidentIDApplication)actual).getIdentity().getContactInfo().get(0).getStreet());
        
            //verify we have 1 additional resident
        Applications apps = svcImpl.getApplications(null, 0, 0);
        assertEquals("unexpected number of applications", 
                appsCountStart+1, 
                apps.size());        
	}
	
	/**
	 * This test verifies that the create is rejected when the client
	 * does not supply all the required information.
	 */
	@Test
	public void testCreateClientError() {
	    log.info("*** testCreateClientError ***");
        ResidentIDApplication expected = new ResidentIDApplication();
        
            //verify how many residents exist
        int appsCountStart = svcImpl.getApplications(null, 0, 0).size();
        
            //create an application
        try {
            @SuppressWarnings("unused")
            Application actual = svcImpl.createApplication(expected);
            fail("did not detect bad argument");
        } catch (BadArgument ex) {
            log.debug("caught expected exception:" +ex);
        }
        
        assertEquals("unexpected number of applications", 
                appsCountStart, 
                svcImpl.getApplications(null, 0, 0).size());        
	}

	/**
	 * This test verifies that the create is rejected when the server
	 * suffers an error completing our request. We will have to exploit an 
	 * purposely inserted bug to be able to reliably cause this error.
	 * @throws BadArgument 
	 */
    @Test
    public void testCreateServerErrror() throws BadArgument {
        log.info("*** testCreateServerError ***");
        ResidentIDApplication expected = new ResidentIDApplication()
            .setIdentity(new Person("throw", "500"));
        
            //verify how many residents exist
        int appsCountStart = svcImpl.getApplications(null, 0, 0).size();
        
            //create an application
        try {
            @SuppressWarnings("unused")
            Application actual = svcImpl.createApplication(expected);
            fail("did not detect server error");
        } catch (RuntimeException ex) {
            log.debug("caught expected exception:" +ex);
        }
        
        assertEquals("unexpected number of applications", 
                appsCountStart, 
                svcImpl.getApplications(null, 0, 0).size());        
    }

    /**
	 * Tests the ability to get applications
	 * @throws BadArgument 
     */
	@Test
	public void testGetApplications() throws BadArgument {
	    log.info("*** testGetApplications ***");
	    
        Applications appsStart = svcImpl.getApplications(null, 0, 0);
	    String names[] = new String[] { "larry", "moe", "curly", "shemp", "manny", "mo", "jack"};
	    for (String name: names) {
	        ResidentIDApplication app = new ResidentIDApplication()
	            .setIdentity(new Person(name, "doe"));
	        svcImpl.createApplication(app);
	    }
        Applications apps = svcImpl.getApplications(null, 0, 0);
        assertEquals("unexexpected residents", 
                appsStart.size() + names.length,
                apps.size());
        
        apps = svcImpl.getApplications(null, 1, 3);
        assertEquals("unexexpected residents", 3, apps.size());
	}
	
	/**
	 * Tests the ability to get a specific application.
	 * @throws BadArgument 
     */
	@Test
	public void testGetApplication() throws BadArgument {
	    log.info("*** testGetApplication ***");

        String names[] = new String[] { "larry", "moe", "curly", "shemp", "manny", "mo", "jack"};
        Applications apps = new Applications();
        for (String name: names) {
            ResidentIDApplication app = new ResidentIDApplication().setIdentity(new Person(name, "doe"));
            apps.add(svcImpl.createApplication(app));
        }
        
        for (Application a: apps) {
            Application a2 = svcImpl.getApplication(a.getId());
            assertEquals("unexpected application", 
                    ((ResidentIDApplication)a).getIdentity().getFirstName(), 
                    ((ResidentIDApplication)a2).getIdentity().getFirstName());
        }
	}
	
    /**
     * Tests the ability to detect a specific application does not exist.
     * @throws BadArgument 
     */
    @Test
    public void testGetApplicationNotFound() throws BadArgument {
        log.info("*** testGetApplicationNotFound ***");

            //create an application to get an ID
        ResidentIDApplication resapp = new ResidentIDApplication()
            .setIdentity(new Person("Greg", "Williams"));
        Application app = svcImpl.createApplication(resapp);
        
            //delete the application ID
        svcImpl.deleteApplication(app.getId());
        
            //now try to get the ID
        assertNull("unexpected application returned", svcImpl.getApplication(app.getId()));
    }
    
	/**
	 * Tests ability to update the values for a specific application.
	 * @throws BadArgument 
     */
	@Test
	public void testUpdateApplication() throws BadArgument {
	    log.info("*** testUpdateApplication ***");
	    
	    Person person = new Person()
	        .setFirstName("peyton")
	        .setLastName("manning");
	    ContactInfo residence = new ContactInfo()
	        .setType(ContactType.RESIDENCE)
	        .setCity("Indianapolis")
	        .setState("IN");
	    person.getContactInfo().add(residence);
	    ResidentIDApplication resapp = new ResidentIDApplication().setIdentity(person);
	    
	    Application app = svcImpl.createApplication(resapp);
	    assertNotNull("null application", app);

	    app.setUpdated(new Date());
	    ((ResidentIDApplication)app).getIdentity().getContactInfo().get(0).setCity("Denver");
	    ((ResidentIDApplication)app).getIdentity().getContactInfo().get(0).setState("CO");
        svcImpl.updateApplication(app);
        
        Application a2 = svcImpl.getApplication(app.getId());
        assertNotNull("null application", a2);
        assertEquals("unexpected firstName", person.getFirstName(), 
                ((ResidentIDApplication)a2).getIdentity().getFirstName());
        assertEquals("unexpected city", 
                ((ResidentIDApplication)app).getIdentity().getContactInfo().get(0).getCity(),
                ((ResidentIDApplication)a2).getIdentity().getContactInfo().get(0).getCity());
        assertEquals("unexpected state", 
                ((ResidentIDApplication)app).getIdentity().getContactInfo().get(0).getState(),
                ((ResidentIDApplication)a2).getIdentity().getContactInfo().get(0).getState());
	}
	
	/**
	 * This test will verify the state cannot be updated for a completed application.
	 * @throws BadArgument
	 */
    @Test
    public void testUpdateApplication409() throws BadArgument {
        log.info("*** testUpdateApplication409 ***");
        
        Person person = new Person()
            .setFirstName("peyton")
            .setLastName("manning");
        ContactInfo residence = new ContactInfo()
            .setType(ContactType.RESIDENCE)
            .setCity("Indianapolis")
            .setState("IN");
        person.getContactInfo().add(residence);
        ResidentIDApplication resapp = new ResidentIDApplication().setIdentity(person);
        
        Application app = svcImpl.createApplication(resapp);
        assertNotNull("null application", app);

            //put the application in the completed state
        ResidentIDApplication resapp2 = new ResidentIDApplication();
        resapp2.setId(app.getId());
        resapp2.setCompleted(new Date());
        resapp2.setUpdated(resapp2.getCompleted());
        resapp2.setIdentity(person);
        svcImpl.updateApplication(resapp2);
        

            //attempt to make a change with application cmpleted
        app.setUpdated(new Date());
        app.setCompleted(null);
        ((ResidentIDApplication)app).getIdentity().getContactInfo().get(0).setCity("Denver");
        ((ResidentIDApplication)app).getIdentity().getContactInfo().get(0).setState("CO");
        assertEquals("unexpected status", 1, svcImpl.updateApplication(app));
    }
    
	/**
	 * Tests ability to delete a specific application
	 * @throws BadArgument 
     */
	@Test
	public void testDeleteApplication() throws BadArgument {
	    log.info("*** testDeleteApplication ***");
	    
	    Person person = new Person()
	        .setFirstName("greg")
	        .setLastName("williams");
	    ContactInfo contact = new ContactInfo()
	        .setCity("St. Louis")
	        .setState("MO");
	    person.getContactInfo().add(contact);
	    ResidentIDApplication resapp = new ResidentIDApplication()
	        .setIdentity(person);
	    
	    Application app = svcImpl.createApplication(resapp);
        assertNotNull("null application", app);
        assertEquals("unexpected result from delete", 
                0, svcImpl.deleteApplication(app.getId()));
        
        Application a2 = null;
        try {
            a2=svcImpl.getApplication(app.getId());
        } catch (NotFoundException expected) {}
        assertNull("unexpected resident", a2);
	}

	/**
	 * This test will verify that we cannot delete a completed application.
	 * @throws BadArgument
	 */
    @Test
    public void testDeleteApplication405() throws BadArgument {
        log.info("*** testDeleteApplication405 ***");
        
        Person person = new Person()
            .setFirstName("greg")
            .setLastName("williams");
        ContactInfo contact = new ContactInfo()
            .setCity("St. Louis")
            .setState("MO");
        person.getContactInfo().add(contact);
        ResidentIDApplication resapp = new ResidentIDApplication()
            .setIdentity(person);
        
        Application app = svcImpl.createApplication(resapp);
        app.setCompleted(new Date());
        app.setUpdated(app.getCompleted());
        svcImpl.updateApplication(app);
        
        assertEquals("unexpected result from delete", 
                1, svcImpl.deleteApplication(app.getId()));
        assertNotNull("completed app is missing", svcImpl.getApplication(app.getId()));
    }

    /*
    @Test
    public void testGetNames() {
        log.info("*** testGetNames ***");
        String names[] = new String[] { "larry", "moe", "curly", "shemp", "many", "mo", "jack"};
        for (String name: names) {
            svcImpl.createResident(new Person(name, "doe"));
        }
        String namesText = svcImpl.getResidentNames();
        log.debug(namesText);
        assertEquals("unexpected names", names.length, 
                new StringTokenizer(namesText, "\n").countTokens());
    }
	
	@Test
	public void testIsSame() {
	    log.info("*** testIsSame ***");
	    Person r1 = createResident(
	            "clark", "kent", "", "manhatten", "ny", "");
        Person r2 = createResident(
                "super", "man", "", "manhatten", "ny", "");
        assertFalse("unexpected same", svcImpl.isSamePerson(r1.getId(), r2.getId()));
        r1.setFirstName("super");
        r1.setLastName("man");
        svcImpl.updateResident(r1);
        assertTrue("unexpected diff", svcImpl.isSamePerson(r1.getId(), r2.getId()));
	}
	*/
}
