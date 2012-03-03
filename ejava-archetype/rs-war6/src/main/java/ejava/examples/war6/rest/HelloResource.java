package ejava.examples.war6.rest;

import javax.annotation.PostConstruct;

import javax.inject.Inject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.war6.svc.HelloService;

/**
 * This class implements the REST-specific interface portion of the service. 
 */
@Path("")  //results in a base URI of /rest depending on ApplicationPath
public class HelloResource {
    protected static final Logger log = LoggerFactory.getLogger(HelloResource.class);
	/**
	 * Have the container inject the service implementation. This requires
	 * a WEB-INF/beans.xml file to turn on CDI/JSR-299 processing. 
	 */
	protected @Inject HelloService impl;
	
	@PostConstruct
	public void init() {
	    log.debug("*** HelloResource: impl={}", impl);
	}

	/**
	 * This method provides a REST-specific wrapper around the Service method. 
	 * @param name
	 * @return
	 */
	@Path("hello")  //results in a URI of /rest/hello depending on ApplicationPath
	@GET 
	@Produces(MediaType.TEXT_PLAIN) //returns text/plain MIME type
	public String sayHelloREST(	        
			@QueryParam("name") String name) {
        log.debug("sayHello: impl={}", impl);
		return impl.sayHello(name);
	}
}
