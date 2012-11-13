package ejava.examples.jaxrssec.rest;

import java.net.URI;


import javax.inject.Inject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.jaxrssec.dmv.ApplicationsServiceTest;
import ejava.examples.jaxrssec.dmv.DmvConfig;

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
