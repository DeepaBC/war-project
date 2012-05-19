package ejava.examples.jaxrsscale.rest;

import org.junit.runner.RunWith;



import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.jaxrsscale.jaxrs.JSONHandlerTest;
import ejava.examples.jaxrsscale.jaxrs.RepresentationsTestConfig;

/**
 * This class implements a remote test of the service deployed to server. 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={RepresentationsTestConfig.class, RepresentationsITConfig.class})
public class JSONHandlerIT extends JSONHandlerTest {    
	//the @Tests are defined in the parent class
}
