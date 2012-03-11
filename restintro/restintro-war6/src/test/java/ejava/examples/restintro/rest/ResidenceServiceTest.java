package ejava.examples.restintro.rest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


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
import ejava.examples.restintro.rest.dto.ContactInfo;
import ejava.examples.restintro.rest.dto.Resident;
import ejava.examples.restintro.svc.DMVService;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a local unit test of the HelloResource class prior 
 * to deploying to the server.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class})
public class ResidenceServiceTest {
	protected static final Logger log = LoggerFactory.getLogger(ResidenceServiceTest.class);

	@Inject
	protected DMVService svcImpl;
	
	@Before
	public void setUp() throws Exception {	
	    log.debug("=== ResidentsServiceTest.setUp() ===");
        log.debug("svcImpl=" + svcImpl);
        cleanup();
	}
	
	protected void cleanup() {
	    while (svcImpl.getResidents(0, 1).size() > 0) {
    	    for (Resident resident: svcImpl.getResidents(0, 10)) {
    	        svcImpl.deleteResident(resident.getId());
    	    }
	    }
	}
	
	protected Resident createResident(String firstName, String lastName,
	        String street, String city, String state, String zip) {
	    Resident resident = new Resident(firstName, lastName);
	    resident.addContactInfo()
	            .setStreet(street)
	            .setCity(city)
	            .setState(state)
	            .setZip(zip);
	    return svcImpl.createResident(resident);
	}
	
	/**
	 * This test verifies that a resident and contact can be created.
	 * @throws Exception 
	 */
	@Test
	public void testCreateResident() throws Exception {
		log.info("*** testCreateResident ***");
		Resident expected = new Resident();
		expected.setFirstName("cat");
		expected.setLastName("inhat");
		ContactInfo info = new ContactInfo();
		info.setStreet("1600 Penn Ave, NW");
		info.setCity("Washington");
		info.setState("DC");
		info.setZip("20500");
		expected.getContactInfo().add(info);
		
		    //verify how many residents exist
		int residentsCountStart = svcImpl.getResidents(0, 0).size();
		
		    //create a resident
		Resident resident = svcImpl.createResident(expected);
		    //verify a sample amount of properties from the return value
		log.debug("{}", JAXBHelper.toString(resident));
		assertNotNull("null resident", resident);
        assertTrue("id unassigned", resident.getId() > 0);
		assertEquals("firstName", expected.getFirstName(), resident.getFirstName());
        assertEquals("lastName", expected.getLastName(), resident.getLastName());
        assertEquals("contacts", 
                expected.getContactInfo().size(), 
                resident.getContactInfo().size());
        assertEquals("contacts.street", 
                expected.getContactInfo().get(0).getStreet(), 
                resident.getContactInfo().get(0).getStreet());
        
            //verify we have 1 additional resident
        List<Resident> residents = svcImpl.getResidents(0, 0);
        assertEquals("unexpected number of residents", 
                residentsCountStart+1, 
                residents.size());        
	}

	/**
	 * Tests the ability to get residents.
	 */
	@Test
	public void testGetResidents() {
	    log.info("*** testGetResidents ***");
	    
        List<Resident> residentsStart = svcImpl.getResidents(0, 0);
	    String names[] = new String[] { "larry", "moe", "curly", "shemp", "manny", "mo", "jack"};
	    for (String name: names) {
	        svcImpl.createResident(new Resident(name, "doe"));
	    }
        List<Resident> residents = svcImpl.getResidents(0, 0);
        assertEquals("unexexpected residents", 
                residentsStart.size() + names.length,
                residents.size());
        
        residents = svcImpl.getResidents(1, 3);
        assertEquals("unexexpected residents", 3, residents.size());
	}
	
	/**
	 * Tests the ability to get a specific resident.
	 */
	@Test
	public void testGetResident() {
	    log.info("*** testGetResident ***");

        String names[] = new String[] { "larry", "moe", "curly", "shemp", "manny", "mo", "jack"};
        List<Resident> residents = new ArrayList<Resident>();
        for (String name: names) {
            residents.add(svcImpl.createResident(new Resident(name, "doe")));
        }
        
        for (Resident r: residents) {
            Resident r2 = svcImpl.getResidentById(r.getId());
            assertEquals("unexpected resident", r.getFirstName(), r2.getFirstName());
        }
	}
	
	/**
	 * Tests ability to update the values for a specific resident.
	 */
	@Test
	public void testUpdateResident() {
	    log.info("*** testUpdateResident ***");
	    
	    Resident resident = createResident("payton", "manning", null, "Indianapolis", "IN", null);
	    assertNotNull("null resident", resident);
	    
	    resident.getContactInfo().get(0).setCity("unknown");
        resident.getContactInfo().get(0).setState("unknown");
        svcImpl.updateResident(resident);
        
        Resident r2 = svcImpl.getResidentById(resident.getId());
        assertNotNull("null resident", r2);
        assertEquals("unexpected firstName", resident.getFirstName(), r2.getFirstName());
        assertEquals("unexpected city", 
                resident.getContactInfo().get(0).getCity(),
                r2.getContactInfo().get(0).getCity());
        assertEquals("unexpected state", 
                resident.getContactInfo().get(0).getState(),
                r2.getContactInfo().get(0).getState());
	}
	
	/**
	 * Tests ability to delete a specific resident.
	 */
	@Test
	public void testDeleteResident() {
	    log.info("*** testDeleteResident ***");
	    
        Resident resident = createResident("greg","williams",null,"St. Louis","MO", null);
        assertNotNull("null resident", resident);
        assertEquals("unexpected result from delete", 
                1, svcImpl.deleteResident(resident.getId()));
        
        Resident r2 = null;
        try {
            r2=svcImpl.getResidentById(resident.getId());
        } catch (NotFoundException expected) {}
        assertNull("unexpected resident", r2);
	}
	
    @Test
    public void testGetNames() {
        log.info("*** testGetNames ***");
        String names[] = new String[] { "larry", "moe", "curly", "shemp", "many", "mo", "jack"};
        for (String name: names) {
            svcImpl.createResident(new Resident(name, "doe"));
        }
        String namesText = svcImpl.getResidentNames();
        log.debug(namesText);
        assertEquals("unexpected names", names.length, 
                new StringTokenizer(namesText, "\n").countTokens());
    }
	
	@Test
	public void testIsSame() {
	    log.info("*** testIsSame ***");
	    Resident r1 = createResident(
	            "clark", "kent", "", "manhatten", "ny", "");
        Resident r2 = createResident(
                "super", "man", "", "manhatten", "ny", "");
        assertFalse("unexpected same", svcImpl.isSamePerson(r1.getId(), r2.getId()));
        r1.setFirstName("super");
        r1.setLastName("man");
        svcImpl.updateResident(r1);
        assertTrue("unexpected diff", svcImpl.isSamePerson(r1.getId(), r2.getId()));
	}
}
