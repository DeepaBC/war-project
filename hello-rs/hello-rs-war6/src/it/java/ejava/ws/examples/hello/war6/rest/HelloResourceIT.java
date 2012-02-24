package ejava.ws.examples.hello.war6.rest;

import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ejava.ws.examples.hello.war6.HelloConfig;

/**
 * This class implements a remote test of the RESTful HelloResource. It does 
 * so by extending the local unit tests and replacing local stubs with 
 * local proxies that relay commands to the server via REST calls using
 * the Apache HttpClient library.
 */
public class HelloResourceIT extends HelloResourceTest {
	protected HttpClient httpClient = new DefaultHttpClient();
	
	@Override
	public void setUp() throws Exception {
        log.debug("=== HelloResourceIT.setUp() ===");
        //override unit test JavaConfig beans with integration test beans
	    if (super.context == null) {
	        super.context = new AnnotationConfigApplicationContext(
	                HelloConfig.class, 
	                HelloITConfig.class);
	    }
	    URI serviceURI = context.getBean("serviceURI", URI.class);
		log.info("serviceURI=" + serviceURI);
		super.setUp();
	}
	
	//the @Tests are defined in the parent class
}
