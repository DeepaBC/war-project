package ejava.ws.examples.ejb31.rest;

import java.net.URI;

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.ws.examples.ejb31.rest.HelloEJB31TestConfig;
import ejava.ws.examples.ejb31.rest.HelloResourceEJB31Test;

/**
 * This class provides an integration test of the deployed service. It runs
 * during the integration-test phase after the application has been built 
 * and deployed to the server.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={
        HelloEJB31TestConfig.class, 
        HelloEJB31ITConfig.class})
public class HelloResourceEJB31IT extends HelloResourceEJB31Test {
    @Inject
	protected URI serviceURI;  
	
	@Override
	public void setUp() throws Exception {
		log.info("serviceURI=" + serviceURI);
	}
}
