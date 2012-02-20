package ejava.ws.examples.hello.war6.rest;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.ws.examples.hello.war6.svc.HelloServiceImpl;

public class HelloResourceTest {
	protected static final Logger log = LoggerFactory.getLogger(HelloResourceTest.class);

	protected HelloResource restImpl;
	
	@Before
	public void setUp() throws Exception {		
		restImpl=new HelloResource();
		restImpl.impl=new HelloServiceImpl();
	}
	
	@Test
	public void testHello() {
		log.info("*** testHello ***");
		assertEquals("unexpected reply", 
				"Hello world\n", 
				restImpl.sayHelloREST("world"));
	}

}
