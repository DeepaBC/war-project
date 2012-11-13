package ejava.examples.ejbear6.rest;

import java.net.URI;


import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.ejbear6.dmv.ApplicationsServiceTest;
import ejava.examples.ejbear6.dmv.DmvConfig;
import ejava.examples.ejbear6.rest.DmvHMITConfig;
import ejava.examples.ejbear6.rest.DmvRSITConfig;

/**
 * This class implements a remote test of the Applications service using a 
 * JAX-RS interface wrapper.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class, DmvRSITConfig.class, DmvHMITConfig.class})
public class ApplicationsHMIT extends ApplicationsServiceTest {    
    protected @Inject URI appURI;
	
    @Override
    public void setUp() throws Exception {
    log.debug("=== {}.setUp() ===", getClass().getSimpleName());
            log.info("appURI={}",appURI);
            super.setUp();
    }
	
	//the @Tests are defined in the parent class
}
