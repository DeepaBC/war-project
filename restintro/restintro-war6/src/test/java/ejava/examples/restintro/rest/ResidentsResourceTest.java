package ejava.examples.restintro.rest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;


import javax.inject.Inject;

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
import ejava.examples.restintro.rest.dto.Residents;
import ejava.examples.restintro.rest.resources.ResidentsResource;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a local unit test of the HelloResource class prior 
 * to deploying to the server.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class})
public class ResidentsResourceTest {
	protected static final Logger log = LoggerFactory.getLogger(ResidentsResourceTest.class);

	@Inject
	protected ResidentsResource restImpl;
	
	@Before
	public void setUp() throws Exception {	
	    log.debug("=== ResidentsResourceTest.setUp() ===");
        log.debug("restImpl=" + restImpl);
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
		int residentsCountStart = restImpl.getResidents(0, 0).size();
		
		    //create a resident
		Resident resident = restImpl.createResident(
		        expected.getFirstName(), 
		        expected.getLastName(), 
		        expected.getContactInfo().get(0).getStreet(),
		        expected.getContactInfo().get(0).getCity(),
                expected.getContactInfo().get(0).getState(),
                expected.getContactInfo().get(0).getZip());
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
        List<Resident> residents = restImpl.getResidents(0, 0);
        log.debug("{}", JAXBHelper.toString(residents, 300));
        assertEquals("unexpected number of residents", 
                residentsCountStart+1, 
                residents.size());        
	}

	/**
	 * Tests the ability to get residents along with count information.
	 */
	@Test
	public void testGetResidents() {
	    log.info("*** testGetResidents ***");
	    
        List<Resident> residentsStart = restImpl.getResidents(0, 0);
	    String names[] = new String[] { "larry", "moe", "curly", "shemp", "many", "mo", "jack"};
	    for (String name: names) {
	        restImpl.createResident(name, "doe", "", "", "", "");
	    }
        Residents residents = (Residents)restImpl.getResidents(0, 0);
        assertEquals("unexexpected residents", 
                residentsStart.size() + names.length,
                residents.size());
        
        residents = (Residents)restImpl.getResidents(1, 3);
        log.debug("{}", JAXBHelper.toString(residents));
        assertEquals("unexexpected residents", 3, residents.size());
        assertEquals("unexexpected start", 1, residents.getStart());
        assertEquals("unexexpected count", 3, residents.getCount());
	}
	
	/**
	 * Tests the ability to get a specific resident.
	 */
	@Test
	public void testGetResident() {
	    log.info("*** testGetResident ***");

        String names[] = new String[] { "larry", "moe", "curly", "shemp", "many", "mo", "jack"};
        List<Resident> residents = new ArrayList<Resident>();
        for (String name: names) {
            residents.add(restImpl.createResident(name, "doe", "", "", "", ""));
        }
        
        for (Resident r: residents) {
            Resident r2 = restImpl.getResident(r.getId());
            assertEquals("unexpected resident", r.getFirstName(), r2.getFirstName());
        }
	}
	
	@Test
	public void testUpdateResident() {
	    log.info("*** testUpdateResident ***");
	    
	    Resident resident = restImpl.createResident("payton", "manning", "", "Indianapolis", "IN", "");
	    assertNotNull("null resident", resident);
	    resident.getContactInfo().get(0).setCity("unknown");
        resident.getContactInfo().get(0).setState("unknown");
        restImpl.updateResident(resident);
        
        Resident r2 = restImpl.getResident(resident.getId());
        assertNotNull("null resident", r2);
        assertEquals("unexpected firstName", resident.getFirstName(), r2.getFirstName());
        assertEquals("unexpected city", 
                resident.getContactInfo().get(0).getCity(),
                r2.getContactInfo().get(0).getCity());
        assertEquals("unexpected state", 
                resident.getContactInfo().get(0).getState(),
                r2.getContactInfo().get(0).getState());
	}
}
