package ejava.rs.examples.ear6.svc;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.ws.examples.ear6.svc.HelloService;

/**
 * This class provides a local unit test of the service facade. All 
 * configuration is expressed by the ContextConfiguration classes.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={HelloEAR6TestConfig.class})
public class HelloResourceEAR6Test {
	protected static final Logger log = LoggerFactory.getLogger(HelloResourceEAR6Test.class);

	@Inject
	protected HelloService svcImpl;
	
	@Before
	public void setUp() throws Exception {
	    log.debug("svcImpl=" + svcImpl);
	}
	
	@Test
	public void testHelloEAR6() {
		log.info("*** testHelloEAR6 ***");
		assertEquals("unexpected reply", 
				"Hello world, ctx.identity=anonymous\n", 
				svcImpl.sayHello("world"));
	}
}