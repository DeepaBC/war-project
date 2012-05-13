package ejava.examples.jaxrsrep.handlers;

import javax.ws.rs.Consumes;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.jboss.resteasy.annotations.providers.jaxb.json.BadgerFish;
import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.jaxrsrep.dmv.lic.dto.Application;
import ejava.examples.jaxrsrep.dmv.lic.dto.ContactInfo;
import ejava.examples.jaxrsrep.dmv.lic.dto.Person;
import ejava.examples.jaxrsrep.dmv.lic.dto.ResidentID;
import ejava.util.rest.Link;
import ejava.util.xml.JAXBHelper;

/**
 * This class is used to demonstrate XML entity handling cases within JAX-RS
 */
@Path("data")
public class JSONHandlerDemoRS {
    private static final Logger log = LoggerFactory.getLogger(JSONHandlerDemoRS.class);
    private @Context UriInfo uriInfo;
    private @Context Request request;
    private @Context HttpHeaders headers;

    
    /**
     * This method provides an example for a resource method to receive,
     * demarshal, respond, and marshal a JAXB Link object that uses attributes.
     */
    @PUT @Path("attributes")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @Mapped(namespaceMap = {
        @XmlNsMap(namespace = "http://ejava.info", jsonName = "ejava"),
    })    
    public Response putLink(Link link) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        link.setHref(uriInfo.getRequestUri());
        link.setType(MediaType.APPLICATION_XML);
        log.debug("returning:{}", JAXBHelper.toString(link));
        return Response.ok(link, MediaType.APPLICATION_JSON).build();
    }

    @PUT @Path("attributes/badgerfish")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @BadgerFish
    public Response putLinkBadgerfish(Link link) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        link.setHref(uriInfo.getRequestUri());
        link.setType(MediaType.APPLICATION_XML);
        log.debug("returning:{}", JAXBHelper.toString(link));
        return Response.ok(link, MediaType.APPLICATION_JSON).build();
    }
    
    /**
     * This method provides an example for a resource method to receive,
     * demarshal, respond, and marshal a JAXB ContactInfo object that uses elements.
     */
    @PUT @Path("elements")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @Mapped(namespaceMap = {
        @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic")
    })    
    public Response putContact(ContactInfo contact) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(contact));
        return Response.ok(contact, MediaType.APPLICATION_JSON).build();
    }

    @PUT @Path("elements/badgerfish")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @BadgerFish
    public Response putContactBadgerfish(ContactInfo contact) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(contact));
        return Response.ok(contact, MediaType.APPLICATION_JSON).build();
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
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @Mapped(namespaceMap = {
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic"),
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic/dap", jsonName = "drvlic-dap"),
    })    
    public Response putPerson(Person person) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(person));
        return Response.ok(person, MediaType.APPLICATION_JSON).build();
    }
    
    @PUT @Path("collection/badgerfish")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @BadgerFish
    public Response putPersonBadgerfish(Person person) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(person));
        return Response.ok(person, MediaType.APPLICATION_JSON).build();
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
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @Mapped(namespaceMap = {
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic")
    })    
    public Response putResidentID(ResidentID residentId) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(residentId));
        return Response.ok(residentId, MediaType.APPLICATION_JSON).build();
    }
    
    @PUT @Path("reference/badgerfish")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @BadgerFish
    public Response putResidentIDBadgerfish(ResidentID residentId) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(residentId));
        return Response.ok(residentId, MediaType.APPLICATION_JSON).build();
    }
    
    
    /**
     * This method provides an example for a resource method to receive,
     * demarshal, respond, and marshal a JAXB object
     * @param app
     * @return
     */
    @PUT @Path("jaxbContext")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @Mapped(namespaceMap = {
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic")
    })    
    public Response putApplication(Application app) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("accept={}", headers.getRequestHeader("Accept"));
        log.debug("returning:{}", JAXBHelper.toString(app));
        return Response.ok(app, MediaType.APPLICATION_JSON).build();
    }

    @PUT @Path("jaxbContext/badgerfish")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @BadgerFish
    public Response putApplicationBadgerfish(Application app) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("accept={}", headers.getRequestHeader("Accept"));
        log.debug("returning:{}", JAXBHelper.toString(app));
        return Response.ok(app, MediaType.APPLICATION_JSON).build();
    }
}
