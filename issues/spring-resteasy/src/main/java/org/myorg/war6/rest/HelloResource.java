package org.myorg.war6.rest;

import javax.annotation.PostConstruct;

import javax.inject.Inject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.myorg.war6.svc.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the REST-specific interface portion of the service.
 * Everything works fine when running a local unit test with Spring and 
 * a integration test with RestEasy and JBoss. However, when we instantiate
 * this project within Jetty with Spring and RestEasy working together --
 * this class gets separately managed. The Spring instances are instantiated,
 * and injected/initialized but are ignored by RestEasy. RestEasy instantiates
 * instances of its own and the failure is first noticed with the NPE for the
 * impl not injected. 
 */
@Path("")  //results in a base URI of /rest depending on ApplicationPath
public class HelloResource {
    protected static final Logger log = LoggerFactory.getLogger(HelloResource.class);
	/**
	 * Have the container inject the service implementation. This requires
	 * a WEB-INF/beans.xml file to turn on CDI/JSR-299 processing. 
	 */
	protected @Inject HelloService impl;
	
	public HelloResource() {
        log.info("*********************************** ");
        log.info("*** HelloResource ctor: " + super.toString());
        log.info("*********************************** ");
	}
	
	@PostConstruct
	public void init() {
        log.info("*********************************** ");
	    log.info("*** HelloResource:PostConstruct *** ");
        log.info("impl1=" + impl);
        log.info("*********************************** ");
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
        log.info("*********************************** ");
        log.info("impl=" + impl);
        log.info("*********************************** ");
		return impl.sayHello(name);
	}
}
