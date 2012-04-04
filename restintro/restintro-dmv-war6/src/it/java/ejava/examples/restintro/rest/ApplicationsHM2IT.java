package ejava.examples.restintro.rest;

import java.net.URI;

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.restintro.DmvConfig;
import ejava.examples.restintro.dmv.ApplicationsServiceTest;

/**
 * This class implements a remote test of the Applications service using a 
 * JAX-RS interface wrapper.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class, DmvHM2ITConfig.class})
public class ApplicationsHM2IT extends ApplicationsServiceTest {    
    
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
}
