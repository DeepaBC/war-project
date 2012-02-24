package ejava.ws.examples.hello.war6.rest;

import static org.junit.Assert.*;

import javax.inject.Inject;


import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ejava.ws.examples.hello.war6.HelloConfig;

/**
 * This class implements a local unit test of the HelloResource class prior 
 * to deploying to the server.
 */
public class HelloResourceTest {
	protected static final Logger log = LoggerFactory.getLogger(HelloResourceTest.class);

	@Autowired //TODO: get this to work
	protected HelloResource restImpl;
	//leverage Spring for object assembly outside of JavaEE container
	protected ApplicationContext context;
	
	@Before
	public void setUp() throws Exception {	
	    log.debug("=== HelloResourceTest.setUp() ===");
	    if (context == null) {
	        context = new AnnotationConfigApplicationContext(HelloConfig.class);
	    }
        restImpl = context.getBean(HelloResource.class);
        log.debug("restImpl=" + restImpl);
	}
	
	/**
	 * This test verifies the proper response is returned from the server.
	 * It will be executed once during unit testing with a local implementation
	 * and then again during integration testing with the aid of a proxy class
	 * to relay commands to the server via REST calls.
	 */
	@Test
	public void testHello() {
		log.info("*** testHello ***");
		assertEquals("unexpected reply", 
				"Hello world\n", 
				restImpl.sayHelloREST("world"));
	}

}
