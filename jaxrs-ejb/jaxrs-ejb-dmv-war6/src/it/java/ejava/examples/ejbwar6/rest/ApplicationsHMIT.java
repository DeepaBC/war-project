package ejava.examples.ejbwar6.rest;


import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.ejbwar6.dmv.ApplicationsServiceTest;
import ejava.examples.ejbwar6.dmv.DmvConfig;

/**
 * This class implements a remote test of the Applications service using a 
 * JAX-RS interface wrapper.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class, DmvRSITConfig.class, DmvHMITConfig.class})
public class ApplicationsHMIT extends ApplicationsServiceTest {    

    @Override
    public void setUp() throws Exception {
        log.debug("=== {}.setUp() ===", getClass().getSimpleName());
        super.setUp();
    }

	
	//the @Tests are defined in the parent class
}
