package ejava.ws.examples.war6.rest;

import javax.inject.Inject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ejava.ws.examples.war6.svc.HelloService;

/**
 * This class implements the REST-specific interface portion of the service. 
 */
@Path("")  //results in a base URI of /rest depending on ApplicationPath
public class HelloResource {
	/**
	 * Have the container inject the service implementation. This requires
	 * a WEB-INF/beans.xml file to turn on CDI/JSR-299 processing.
	 */
	protected @Inject HelloService impl;

	/**
	 * This method provides a REST-specific wrapper around the Service method. 
	 * @param name
	 * @return
	 */
	@Path("/hello")  //results in a URI of /rest/hello depending on ApplicationPath
	@GET 
	@Produces(MediaType.TEXT_PLAIN) //returns text/plain MIME type
	public String sayHelloREST(
			@QueryParam("name") String name) {
		return impl.sayHello(name);
	}
}