package ejava.examples.jaxrsscale.bank;

import javax.inject.Inject;



import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.exercises.jaxrsscale.bank.BankConfig;
import ejava.exercises.jaxrsscale.bank.BankTest;

/**
 * This class implements a remote test of the Bank service.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BankConfig.class, BankITConfig.class})
public class BankIT extends BankTest {    
    
	//used to query application configuration
	protected @Inject ApplicationContext ctx;
	
	@Override
	public void setUp() throws Exception {
        log.debug("=== {}.setUp() ===", getClass().getSimpleName());
		super.setUp();
	}

	//the @Tests are defined in the parent class
}
