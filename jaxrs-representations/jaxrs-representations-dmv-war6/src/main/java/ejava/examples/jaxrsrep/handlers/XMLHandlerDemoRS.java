package ejava.examples.jaxrsrep.handlers;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.jaxrsrep.dmv.lic.dto.Application;

/**
 * This class is used to demonstrate XML entity handling cases within JAX-RS
 */
@Path("data")
public class XMLHandlerDemoRS {
    private static final Logger log = LoggerFactory.getLogger(XMLHandlerDemoRS.class);
    private @Context UriInfo uriInfo;
    private @Context Request request;

    /**
     * This method provides an example for a resource method to receive,
     * demarshal, respond, and marshal a JAXB object
     * @param app
     * @return
     */
    @PUT @Path("jaxbContext")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response putApplication(Application app) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        return Response.ok(app, MediaType.APPLICATION_XML).build();
    }
    
}
