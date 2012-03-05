package ejava.examples.restintro.rest;

import java.net.URI;

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.restintro.DmvConfig;
import ejava.examples.restintro.rest.ResidentsResourceTest;

/**
 * This class implements a remote test of the RESTful HelloResource. It does 
 * so by extending the local unit tests and replacing local stubs with 
 * local proxies that relay commands to the server via REST calls using
 * the Apache HttpClient library.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class, DmvITConfig.class})
public class HelloResourceIT extends ResidentsResourceTest {    
    
	//used to query application configuration
	protected @Inject ApplicationContext ctx;
	
	@Override
	public void setUp() throws Exception {
        log.debug("=== HelloResourceIT.setUp() ===");
        URI serviceURI = ctx.getBean("serviceURI", URI.class);
		log.info("serviceURI=" + serviceURI);
		super.setUp();
	}
	
	//the @Tests are defined in the parent class
}
