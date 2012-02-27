package ejava.ws.examples.ear6.svc;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides the business logic implementation for the Hello Service
 * using an EJB, with an interface deployed within the EAR.
 */
@Stateless
public class HelloServiceEJB implements HelloService {
    protected static final Logger log = LoggerFactory.getLogger(HelloServiceEJB.class);
    
	@Resource
    protected SessionContext ctx;
	
	@PostConstruct
	public void init() {
	    log.info("*** HelloServiceEJB ***");
	    log.debug("ctx=" + ctx);
	}
	
    public String sayHello(String name) {
	    String result=String.format("Hello %s, ctx.identity=%s\n", 
	            name, ctx.getCallerPrincipal());
	    log.info(String.format("returning %s", result));
		return result;
	}
}
