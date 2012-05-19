package ejava.examples.jaxrsscale.handlers;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.jaxrsscale.dmv.lic.dto.Application;
import ejava.examples.jaxrsscale.dmv.lic.dto.ContactInfo;
import ejava.examples.jaxrsscale.dmv.lic.dto.Person;
import ejava.examples.jaxrsscale.dmv.lic.dto.ResidentID;
import ejava.util.rest.Link;

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
     * demarshal, respond, and marshal a JAXB Link object that uses attributes.
     */
    @PUT @Path("attributes")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response putLink(Link link) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        link.setHref(uriInfo.getRequestUri());
        link.setType(MediaType.APPLICATION_XML);
        return Response.ok(link, MediaType.APPLICATION_XML).build();
    }
    
    /**
     * This method provides an example for a resource method to receive,
     * demarshal, respond, and marshal a JAXB ContactInfo object that uses elements.
     */
    @PUT @Path("elements")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response putContact(ContactInfo contact) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        return Response.ok(contact, MediaType.APPLICATION_XML).build();
    }
    
    /**
     * This method provides an example for a resource method to receive, 
     * demarshal, respond, and marshal a JAXB Person object that contains a 
     * collection of ContactInfo.
     * @param person
     * @return
     */
    @PUT @Path("collection")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response putPerson(Person person) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        return Response.ok(person, MediaType.APPLICATION_XML).build();
    }
    
    /**
     * This method provides an example for a resource method to receive,
     * demarshal, respond, and marshal a JAXB ResidentID that contains a 
     * direct reference to a JAXB Person.
     * @param person
     * @return
     */
    @PUT @Path("reference")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response putResidentID(ResidentID residentId) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        return Response.ok(residentId, MediaType.APPLICATION_XML).build();
    }
    
    
    /**
     * This method provides an example for a resource method to receive,
     * demarshal, respond, and marshal a JAXB object
     * @param app
     * @return
     */
    @PUT @Path("jaxbContext")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response putApplication(Application app) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        return Response.ok(app, MediaType.APPLICATION_XML).build();
    }
    
}
