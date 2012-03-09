package ejava.examples.restintro.rest;

import static org.junit.Assert.*;


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
	 * This test verifies the proper response is returned from the server.
	 * It will be executed once during unit testing with a local implementation
	 * and then again during integration testing with the aid of a proxy class
	 * to relay commands to the server via REST calls.
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
		Resident resident = restImpl.createResident(
		        expected.getFirstName(), 
		        expected.getLastName(), 
		        expected.getContactInfo().get(0).getStreet(),
		        expected.getContactInfo().get(0).getCity(),
                expected.getContactInfo().get(0).getState(),
                expected.getContactInfo().get(0).getZip());
		log.debug("{}", JAXBHelper.toString(resident, Resident.class));
        assertTrue("id unassigned", resident.getId() > 0);
		assertEquals("firstName", expected.getFirstName(), resident.getFirstName());
        assertEquals("lastName", expected.getLastName(), resident.getLastName());
	}
}
