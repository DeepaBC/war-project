package ejava.ws.examples.hello.ear6.rest;

import java.net.URI;


import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.ws.examples.hello.ear6.svc.HelloEAR6TestConfig;
import ejava.ws.examples.hello.ear6.svc.HelloResourceEAR6Test;

/**
 * This class provides an integration test of the deployed service. It runs
 * during the integration-test phase after the application has been built 
 * and deployed to the server.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={
        HelloEAR6TestConfig.class,
        HelloEAR6ITConfig.class})
public class HelloResourceEAR6IT extends HelloResourceEAR6Test {
    @Inject
	protected URI serviceURI;  
	
	@Override
	public void setUp() throws Exception {
		log.info("serviceURI=" + serviceURI);
	}
}
