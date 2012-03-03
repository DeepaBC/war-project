package ejava.ws.examples.war6.rest;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ejava.ws.examples.war6.svc.HelloService;

/**
 * This class implements the REST-specific interface portion of the service. 
 */
@Component
@Path("")  //results in a base URI of /rest depending on ApplicationPath
public class HelloResource {
    protected static final Logger log = LoggerFactory.getLogger(HelloResource.class);
	/**
	 * Have the container inject the service implementation. This requires
	 * a WEB-INF/beans.xml file to turn on CDI/JSR-299 processing. 
	 */
	protected @Inject HelloService impl;
	
	static HelloService staticService;
	public HelloResource() {
        log.info("*** HelloResource ctor, loader= " + this.getClass().getClassLoader());
	}
	
	@PostConstruct
	public void init() {
	    log.info("*** HelloResource *** ");
        log.info("impl1=" + impl);
        if (impl != null) {
            staticService = impl;
        }
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
        log.info("impl=" + impl);
        if (impl == null) {
            impl = staticService;
        }
        log.info("impl2=" + impl);
		return impl.sayHello(name);
	}
}