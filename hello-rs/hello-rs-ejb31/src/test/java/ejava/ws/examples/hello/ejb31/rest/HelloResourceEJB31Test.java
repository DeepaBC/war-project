package ejava.ws.examples.hello.ejb31.rest;

import static org.junit.Assert.*;

import javax.ejb.SessionContext;


import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.ws.examples.hello.ejb31.svc.HelloServiceEJB;

/**
 * This class provides a local unit test of the service facade.
 */
public class HelloResourceEJB31Test {
	protected static final Logger log = LoggerFactory.getLogger(HelloResourceEJB31Test.class);

	protected HelloResource restImpl;
	
	@Before
	public void setUp() throws Exception {		
		restImpl=new HelloResource();
		restImpl.impl=new HelloServiceEJB();
		SessionContext ctx=new SessionContextStub();
		((SessionContextStub)ctx).setCallerPrincipal("anonymous");
        restImpl.impl.setSessionContext(ctx);
	}
	
	@Test
	public void testHelloEJB31() {
		log.info("*** testHelloEJB31 ***");
		assertEquals("unexpected reply", 
				"Hello world, ctx.identity=anonymous\n", 
				restImpl.sayHelloREST("world"));
	}

}
