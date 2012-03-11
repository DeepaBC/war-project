package ejava.examples.restintro.rest;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.StringTokenizer;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.restintro.DmvConfig;
import ejava.examples.restintro.rest.ResidenceServiceTest;
import ejava.examples.restintro.rest.dto.Resident;
import ejava.examples.restintro.rest.dto.Residents;
import ejava.examples.restintro.rest.resources.ResidentsHTTP;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a remote test of the RESTful HelloResource. It does 
 * so by extending the local unit tests and replacing local stubs with 
 * local proxies that relay commands to the server via REST calls using
 * the Apache HttpClient library.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class, DmvRSITConfig.class})
public class ResidentsResourceIT extends ResidenceServiceTest {    
    
	//used to query application configuration
	protected @Inject ApplicationContext ctx;
	
	@Override
	public void setUp() throws Exception {
        log.debug("=== ResidentsResourceIT.setUp() ===");
        URI serviceURI = ctx.getBean("serviceURI", URI.class);
		log.info("serviceURI=" + serviceURI);
		super.setUp();
	}

	//the @Tests are defined in the parent class

	/**
	 * This method extends the base test by adding a verification
	 * of the DTO size, start, and count values.
	 */
    @Override
    public void testGetResidents() {
        super.testGetResidents();
        
        Residents residents = (Residents)svcImpl.getResidents(1, 3);
        log.debug("{}", JAXBHelper.toString(residents));
        assertEquals("unexexpected residents", 3, residents.size());
        assertEquals("unexexpected start", 1, residents.getStart());
        assertEquals("unexexpected count", 3, residents.getCount());
    }    
}
