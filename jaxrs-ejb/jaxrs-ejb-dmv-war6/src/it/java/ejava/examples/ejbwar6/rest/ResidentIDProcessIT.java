package ejava.examples.ejbwar6.rest;

import java.net.URI;

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.ejbwar6.dmv.DmvConfig;
import ejava.examples.ejbwar6.dmv.HttpCacheConfig;
import ejava.examples.ejbwar6.dmv.ResidentIDProcessTest;

/**
 * This class implements a remote test of the Applications service using a 
 * JAX-RS interface wrapper.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class, HttpCacheConfig.class, DmvRSITConfig.class})
public class ResidentIDProcessIT extends ResidentIDProcessTest {    
    
	//used to query application configuration
	protected @Inject ApplicationContext ctx;
	
	@Override
	public void setUp() throws Exception {
        log.debug("=== {}.setUp() ===", getClass().getSimpleName());
        URI dmvURI = ctx.getBean("dmvURI", URI.class);
		log.info("dmvURI={}",dmvURI);
		super.setUp();
	}


	//the @Tests are defined in the parent class
}
