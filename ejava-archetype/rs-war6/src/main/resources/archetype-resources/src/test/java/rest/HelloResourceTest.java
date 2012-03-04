#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.rest;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ${package}.HelloTestConfig;
import ${package}.rest.HelloResource;

/**
 * This class implements a local unit test of the HelloResource class prior 
 * to deploying to the server.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={HelloTestConfig.class})
public class HelloResourceTest {
	protected static final Logger log = LoggerFactory.getLogger(HelloResourceTest.class);

	@Inject
	protected HelloResource restImpl;
	
	@Before
	public void setUp() throws Exception {	
	    log.debug("=== HelloResourceTest.setUp() ===");
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
				"Hello world${symbol_escape}n", 
				restImpl.sayHelloREST("world"));
	}
}
