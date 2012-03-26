package ejava.examples.restintro.dmv;

import static org.junit.Assert.*;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.resteasy.spi.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.restintro.DmvConfig;
import ejava.examples.restintro.dmv.dto.Application;
import ejava.examples.restintro.dmv.dto.Applications;
import ejava.examples.restintro.dmv.dto.ContactInfo;
import ejava.examples.restintro.dmv.dto.ContactType;
import ejava.examples.restintro.dmv.dto.Person;
import ejava.examples.restintro.dmv.dto.ResidentIDApplication;
import ejava.examples.restintro.dmv.svc.ApplicationsService;
import ejava.examples.restintro.dmv.svc.BadArgument;
import ejava.examples.restintro.dmv.svc.ResidentsService;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a local unit test of the ApplicationsService prior 
 * to deploying to the server.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class})
public class ApplicationsServiceTest {
	protected static final Logger log = LoggerFactory.getLogger(ApplicationsServiceTest.class);

	@Inject
	protected ApplicationsService svcImpl;
	
	@Before
	public void setUp() throws Exception {	
	    log.debug("=== ApplicationsServiceTest.setUp() ===");
        log.debug("svcImpl=" + svcImpl);
        cleanup();
	}
	
	protected void cleanup() {
	    while (svcImpl.getApplications(null, 0, 1).size() > 0) {
    	    for (Application app: svcImpl.getApplications(null, 0, 10)) {
    	        svcImpl.deleteApplication(app.getId());
    	    }
	    }
	}
	
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
	 * Tests ability to update the values for a specific application.
	 * @throws BadArgument 
     */
	@Test
	public void testUpdateApplication() throws BadArgument {
	    log.info("*** testUpdateApplication ***");
	    
	    Person person = new Person()
	        .setFirstName("payton")
	        .setLastName("manning");
	    ContactInfo residence = new ContactInfo()
	        .setType(ContactType.RESIDENCE)
	        .setCity("Indianapolis")
	        .setState("IN");
	    person.getContactInfo().add(residence);
	    ResidentIDApplication resapp = new ResidentIDApplication().setIdentity(person);
	    
	    Application app = svcImpl.createApplication(resapp);
	    assertNotNull("null application", app);
	    
	    ((ResidentIDApplication)app).getIdentity().getContactInfo().get(0).setCity("unknown");
	    ((ResidentIDApplication)app).getIdentity().getContactInfo().get(0).setState("unknown");
        svcImpl.updateApplication(app);
        
        Application a2 = svcImpl.getApplication(app.getId());
        assertNotNull("null application", a2);
        assertEquals("unexpected firstName", person.getFirstName(), 
                ((ResidentIDApplication)a2).getIdentity().getFirstName());
        assertEquals("unexpected city", 
                person.getContactInfo().get(0).getCity(),
                ((ResidentIDApplication)a2).getIdentity().getContactInfo().get(0).getCity());
        assertEquals("unexpected state", 
                person.getContactInfo().get(0).getState(),
                ((ResidentIDApplication)a2).getIdentity().getContactInfo().get(0).getState());
	}
	
	/**
	 * Tests ability to delete a specific application
	 * @throws BadArgument 
     */
	@Test
	public void testDeleteApplication() throws BadArgument {
	    log.info("*** testDeleteResident ***");
	    
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
