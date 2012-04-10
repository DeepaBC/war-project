package ejava.examples.restintro.rest;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.restintro.dmv.DmvConfig;
import ejava.examples.restintro.dmv.dto.Persons;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a remote test of the basic JAX-RS interface
 * on the Residents Service. It does so be extending the unit test
 * and injecting a proxy that is configured to contact a specific urlBase.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class, DmvRSITConfig.class, DmvHttpITConfig.class})
public class ResidentsHttpIT extends ResidentsRSIT {    
    
	//used to query application configuration
	protected @Inject ApplicationContext ctx;
	
	@Override
	public void setUp() throws Exception {
        log.debug("=== {}.setUp() ===", getClass().getSimpleName());
        URI serviceURI = ctx.getBean("serviceURI", URI.class);
        String implContext = ctx.getBean("implContext", String.class);
		log.info("serviceURI={}/{}",serviceURI,implContext);
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
        
        Persons residents = (Persons)svcImpl.getResidents(1, 3);
        log.debug("{}", JAXBHelper.toString(residents));
        assertEquals("unexexpected residents", 3, residents.size());
        assertEquals("unexexpected start", 1, residents.getStart());
        assertEquals("unexexpected count", 3, residents.getCount());
    }    
}
