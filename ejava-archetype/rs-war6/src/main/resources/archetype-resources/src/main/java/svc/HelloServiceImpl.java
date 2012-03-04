#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.svc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides the business logic implementation for the Hello Service.
 */
public class HelloServiceImpl implements HelloService {
	protected static Logger log = LoggerFactory.getLogger(HelloServiceImpl.class);

	public String sayHello(String name) {
        String result = String.format("Hello %s${symbol_escape}n", name);
        log.debug(String.format("returning %s", result));
        return result;
    }
}
