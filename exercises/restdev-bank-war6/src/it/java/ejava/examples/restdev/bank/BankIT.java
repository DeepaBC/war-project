package ejava.examples.restdev.bank;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.exercises.restdev.bank.BankConfig;
import ejava.exercises.restdev.bank.BankTest;

/**
 * This class implements a remote test of the Bank service.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BankConfig.class, BankITConfig.class})
public class BankIT extends BankTest {    
	@Override
	public void setUp() throws Exception {
        log.debug("=== {}.setUp() ===", getClass().getSimpleName());
		super.setUp();
	}

	//the @Tests are defined in the parent class
}
