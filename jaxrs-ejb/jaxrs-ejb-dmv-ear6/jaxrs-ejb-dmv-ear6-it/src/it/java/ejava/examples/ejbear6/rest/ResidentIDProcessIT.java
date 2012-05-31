package ejava.examples.ejbear6.rest;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.ejbear6.dmv.DmvConfig;
import ejava.examples.ejbear6.dmv.HttpCacheConfig;
import ejava.examples.ejbear6.dmv.ResidentIDProcessTest;
import ejava.examples.ejbear6.rest.DmvRSITConfig;

/**
 * This class implements a remote test of the Applications service using a 
 * JAX-RS interface wrapper.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class, HttpCacheConfig.class, DmvRSITConfig.class})
public class ResidentIDProcessIT extends ResidentIDProcessTest {    
	//the @Tests are defined in the parent class
}
