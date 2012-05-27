package ejava.examples.jaxrsrep.handlers;

import java.io.StringWriter;

import java.util.HashMap;
import java.util.Map;

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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamReader;
import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamWriter;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;
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
     * @throws XMLStreamException 
     * @throws JSONException 
     * @throws JAXBException 
     */
    @PUT @Path("attributes")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Mapped(namespaceMap = {
        @XmlNsMap(namespace = "http://ejava.info", jsonName = "ejava"),
    })    
    public Response putLink(Link link) throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        link.setHref(uriInfo.getRequestUri());
        link.setType(MediaType.APPLICATION_XML);
        log.debug("returning:{}", JAXBHelper.toString(link));
        return Response.ok(link, MediaType.APPLICATION_JSON).build();
    }
    
    @PUT @Path("attributes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Mapped(namespaceMap = {
        @XmlNsMap(namespace = "http://ejava.info", jsonName = "ejava")
    })    
    public Response putLinkJSON(
            @Mapped(namespaceMap = {@XmlNsMap(namespace = "http://ejava.info", jsonName = "ejava")}) Link link) 
            throws JSONException, XMLStreamException, JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("accept={}", headers.getRequestHeader("Accept"));

        log.debug("unmarshalled to:{}", JAXBHelper.toString(link));
        
        link.setHref(uriInfo.getRequestUri());
        link.setType(MediaType.APPLICATION_JSON);
        log.debug("returning:{}", JAXBHelper.toString(link));
        return Response.ok(link, MediaType.APPLICATION_JSON).build();
    }

    @PUT @Path("attributes/badgerfish")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putXMLLinkBadgerfish(Link link) throws JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("accept={}", headers.getRequestHeader("Accept"));
        link.setHref(uriInfo.getRequestUri());
        link.setType(MediaType.APPLICATION_XML);
        log.debug("returning:{}", JAXBHelper.toString(link));
        return Response.ok(link, MediaType.APPLICATION_JSON).build();
    }
    
    @PUT @Path("attributes/badgerfish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putJSONLinkBadgerfish(
            @BadgerFish Link link) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("unmarshalled to:{}", JAXBHelper.toString(link));
        
        link.setHref(uriInfo.getRequestUri());
        link.setType(MediaType.APPLICATION_JSON);
        log.debug("returning:{}", JAXBHelper.toString(link));
        return Response.ok(link, MediaType.APPLICATION_JSON).build();
    }
    
    /**
     * This method is using a custom demarshaller and marshaller that are
     * custom written to take care of the types expected. 
     */
    @PUT @Path("attributes/custom")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Mapped(namespaceMap = {@XmlNsMap(namespace = "http://ejava.info", jsonName = "ejava")}) 
    public Response putLinkJSONCustom(
            @Mapped(namespaceMap = {@XmlNsMap(namespace = "http://ejava.info", jsonName = "ejava")}) Link link) 
            throws JSONException, XMLStreamException, JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("accept={}", headers.getRequestHeader("Accept"));
        log.debug("unmarshalled to:{}", JAXBHelper.toString(link));
        link.setHref(uriInfo.getRequestUri());
        link.setType(MediaType.APPLICATION_JSON);
        log.debug("returning:{}", JAXBHelper.toString(link));
        return Response.ok(link, MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("attributes/badgerfish/custom")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putLinkJSONBadgerfishCustom(
            @BadgerFish Link link) 
            throws JSONException, XMLStreamException, JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("accept={}", headers.getRequestHeader("Accept"));
        log.debug("unmarshalled to:{}", JAXBHelper.toString(link));
        link.setHref(uriInfo.getRequestUri());
        link.setType(MediaType.APPLICATION_JSON);
        log.debug("returning:{}", JAXBHelper.toString(link));
        return Response.ok(link, MediaType.APPLICATION_JSON).build();
    }
    

    /**
     * This method provides an example for a resource method to receive,
     * demarshal, respond, and marshal a JAXB ContactInfo object that uses elements.
     * @throws JAXBException 
     */
    @PUT @Path("elements")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Mapped(namespaceMap = {
        @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic")
    })    
    public Response putContact(ContactInfo contact) throws JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(contact));
        return Response.ok(contact, MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("elements")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Mapped(namespaceMap = {
        @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic")
    })    
    public Response putContactJSON(
            @Mapped(namespaceMap = {@XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName ="drvlic")}) ContactInfo contact) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        
        log.debug("returning:{}", JAXBHelper.toString(contact));
        return Response.ok(contact, MediaType.APPLICATION_JSON).build();
    }

    @PUT @Path("elements/badgerfish")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putContactBadgerfish(ContactInfo contact) throws JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(contact));
        return Response.ok(contact, MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("elements/badgerfish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putJSONContactBadgerfish(
            @BadgerFish ContactInfo contact) 
            throws JAXBException, JSONException, XMLStreamException {
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
     * @throws JAXBException 
     */
    @PUT @Path("collection")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Mapped(namespaceMap = {
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic"),
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic/dap", jsonName = "drvlic-dap"),
    })    
    public Response putPerson(Person person) throws JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(person));
        return Response.ok(person, MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("collection")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Mapped(namespaceMap = {
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic"),
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic/dap", jsonName = "drvlic-dap"),
    })    
    public Response putPersonJSON(
            @Mapped(namespaceMap = {
                @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic"),
                @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic/dap", jsonName = "drvlic-dap"),
            }) Person person) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        
        log.debug("returning:{}", JAXBHelper.toString(person));
        return Response.ok(person, MediaType.APPLICATION_JSON).build();
    }
    
    @PUT @Path("collection/badgerfish")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putPersonBadgerfish(Person person) throws JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(person));
        return Response.ok(person, MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("collection/badgerfish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putJSONPersonBadgerfish(
            @BadgerFish Person person) 
            throws JAXBException, JSONException, XMLStreamException {
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
     * @throws JAXBException 
     */
    @PUT @Path("reference")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Mapped(namespaceMap = {
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic")
    })    
    public Response putResidentID(ResidentID residentId) throws JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(residentId));
        return Response.ok(residentId, MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("reference")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Mapped(namespaceMap = {
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic")
    })    
    public Response putJSONResidentID(
            @Mapped(namespaceMap = {
                @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic")
            }) ResidentID residentId) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(residentId));
        return Response.ok(residentId, MediaType.APPLICATION_JSON).build();
    }
    
    @PUT @Path("reference/badgerfish")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putJSONResidentIDBadgerfish(ResidentID residentId) throws JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(residentId));
        return Response.ok(residentId, MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("reference/badgerfish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putJSONResidentIDBadgerfishJSON(
            @BadgerFish ResidentID residentId) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        
        log.debug("returning:{}", JAXBHelper.toString(residentId));
        return Response.ok(residentId, MediaType.APPLICATION_JSON).build();
    }
    
    
    /**
     * This method provides an example for a resource method to receive,
     * demarshal, respond, and marshal a JAXB object
     * @param app
     * @return
     * @throws JAXBException 
     */
    @PUT @Path("jaxbContext")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Mapped(namespaceMap = {
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic")
    })    
    public Response putApplication(Application app) throws JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("accept={}", headers.getRequestHeader("Accept"));
        log.debug("returning:{}", JAXBHelper.toString(app));
        return Response.ok(app, MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("jaxbContext")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Mapped(namespaceMap = {
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic")
    })    
    public Response putJSONApplication(
            @Mapped(namespaceMap = {
                @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic")
            }) Application app) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("accept={}", headers.getRequestHeader("Accept"));
        
        log.debug("returning:{}", JAXBHelper.toString(app));
        return Response.ok(app, MediaType.APPLICATION_JSON).build();
    }

    @PUT @Path("jaxbContext/badgerfish")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putApplicationBadgerfish(Application app) throws JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("accept={}", headers.getRequestHeader("Accept"));
        log.debug("returning:{}", JAXBHelper.toString(app));
        return Response.ok(app, MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("jaxbContext/badgerfish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putJSONApplicationBadgerfish(
            @BadgerFish Application app) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("accept={}", headers.getRequestHeader("Accept"));
        
        log.debug("returning:{}", JAXBHelper.toString(app));
        return Response.ok(app, MediaType.APPLICATION_JSON).build();
    }
}
