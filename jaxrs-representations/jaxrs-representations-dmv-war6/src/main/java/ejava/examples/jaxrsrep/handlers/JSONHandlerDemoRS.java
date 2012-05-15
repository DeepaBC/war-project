package ejava.examples.jaxrsrep.handlers;

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
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamReader;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
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
import ejava.examples.jaxrsrep.dmv.lic.dto.ResidentIDApplication;
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
    
    /**
     * This helper method compensates for the apparent lack of direct support
     * RESTEasy currently has for accepting JSON objects requiring namespace 
     * mapping. The code here is the same type of code required in a JSE
     * environment.
     * @param jsonString
     * @return
     * @throws JAXBException
     * @throws JSONException
     * @throws XMLStreamException
     */
    @SuppressWarnings("unchecked")
    public <T> T demarshalMappedJSON(Class<T> type, String jsonString, Class<?>...clazzes) 
            throws JAXBException, JSONException, XMLStreamException {
        Class<?>[] classes = new Class<?>[clazzes.length+1];
        classes[0]=type;
        for (int i=0;i<clazzes.length; i++) {
            classes[i+1]=clazzes[i];
        }
        JAXBContext ctx = JAXBContext.newInstance(classes);
        Configuration config = new Configuration();
        Map<String, String> xmlToJsonNamespaces = new HashMap<String,String>();
        xmlToJsonNamespaces.put("http://ejava.info", "ejava");
        xmlToJsonNamespaces.put("http://dmv.ejava.info", "dmv");
        xmlToJsonNamespaces.put("http://dmv.ejava.info/dap", "dmv-dap");
        xmlToJsonNamespaces.put("http://dmv.ejava.info/drvlic", "drvlic");
        xmlToJsonNamespaces.put("http://dmv.ejava.info/drvlic/dap", "drvlic-dap");
        config.setXmlToJsonNamespaces(xmlToJsonNamespaces);
        MappedNamespaceConvention con = new MappedNamespaceConvention(config);

        JSONObject obj = new JSONObject(jsonString);
        XMLStreamReader xmlStreamReader = new MappedXMLStreamReader(obj, con);
        
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        return (T)unmarshaller.unmarshal(xmlStreamReader);
    }

    @SuppressWarnings("unchecked")
    public <T> T demarshalBadgerfishJSON(Class<T> type, String jsonString, Class<?>...clazzes) 
            throws JAXBException, JSONException, XMLStreamException {
        Class<?>[] classes = new Class<?>[clazzes.length+1];
        classes[0]=type;
        for (int i=0;i<clazzes.length; i++) {
            classes[i+1]=clazzes[i];
        }
        JAXBContext ctx = JAXBContext.newInstance(classes);
        JSONObject obj = new JSONObject(jsonString);
        XMLStreamReader xmlStreamReader = new BadgerFishXMLStreamReader(obj);
        
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        return (T)unmarshaller.unmarshal(xmlStreamReader);
    }
    
    

    @PUT @Path("attributes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @Mapped(namespaceMap = {
        @XmlNsMap(namespace = "http://ejava.info", jsonName = "ejava"),
    })    
    public Response putLinkJSON(String jsonString) 
            throws JSONException, XMLStreamException, JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("accept={}", headers.getRequestHeader("Accept"));
        log.debug("received:{}", jsonString);

        Link link = demarshalMappedJSON(Link.class, jsonString);
        log.debug("unmarshalled to:{}", JAXBHelper.toString(link));
        
        link.setHref(uriInfo.getRequestUri());
        link.setType(MediaType.APPLICATION_JSON);
        log.debug("returning:{}", JAXBHelper.toString(link));
        return Response.ok(link, MediaType.APPLICATION_JSON).build();
    }

    @PUT @Path("attributes/badgerfish")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @BadgerFish
    public Response putXMLLinkBadgerfish(Link link) {
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
    @Formatted
    @BadgerFish
    public Response putJSONLinkBadgerfish(String jsonString) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("received:{}", jsonString);
        
        Link link = demarshalBadgerfishJSON(Link.class, jsonString);
        log.debug("unmarshalled to:{}", JAXBHelper.toString(link));
        
        link.setHref(uriInfo.getRequestUri());
        link.setType(MediaType.APPLICATION_JSON);
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
    @PUT @Path("elements")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @Mapped(namespaceMap = {
        @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic")
    })    
    public Response putContact(String jsonString) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("received:{}", jsonString);
        
        ContactInfo contact = demarshalMappedJSON(ContactInfo.class, jsonString);
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
    @PUT @Path("elements/badgerfish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @BadgerFish
    public Response putJSONContactBadgerfish(String jsonString) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("received:{}", jsonString);
        
        ContactInfo contact = demarshalBadgerfishJSON(ContactInfo.class, jsonString);
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
    @PUT @Path("collection")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @Mapped(namespaceMap = {
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic"),
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic/dap", jsonName = "drvlic-dap"),
    })    
    public Response putPerson(String jsonString) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("received:{}", jsonString);
        
        Person person = demarshalMappedJSON(Person.class, jsonString);
        
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
    @PUT @Path("collection/badgerfish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @BadgerFish
    public Response putJSONPersonBadgerfish(String jsonString) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("received:{}", jsonString);
        
        Person person = demarshalBadgerfishJSON(Person.class, jsonString);
        
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
    @PUT @Path("reference")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @Mapped(namespaceMap = {
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic")
    })    
    public Response putJSONResidentID(String jsonString) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("received:{}", jsonString);
        
        ResidentID residentId = demarshalMappedJSON(ResidentID.class, jsonString);
        log.debug("returning:{}", JAXBHelper.toString(residentId));
        return Response.ok(residentId, MediaType.APPLICATION_JSON).build();
    }
    
    @PUT @Path("reference/badgerfish")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @BadgerFish
    public Response putJSONResidentIDBadgerfish(ResidentID residentId) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(residentId));
        return Response.ok(residentId, MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("reference/badgerfish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @BadgerFish
    public Response putJSONResidentIDBadgerfish(String jsonString) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("received:{}", jsonString);
        
        ResidentID residentId=demarshalBadgerfishJSON(ResidentID.class, jsonString);
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
    @PUT @Path("jaxbContext")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @Mapped(namespaceMap = {
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic")
    })    
    public Response putJSONApplication(String jsonString) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("accept={}", headers.getRequestHeader("Accept"));
        log.debug("received:{}", jsonString);
        
        Application app = demarshalMappedJSON(Application.class, jsonString,
                ResidentIDApplication.class);
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
    @PUT @Path("jaxbContext/badgerfish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Formatted
    @BadgerFish
    public Response putJSONApplicationBadgerfish(String jsonString) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("accept={}", headers.getRequestHeader("Accept"));
        log.debug("received:{}", jsonString);
        
        Application app = demarshalBadgerfishJSON(Application.class, jsonString, 
                ResidentIDApplication.class);
        log.debug("returning:{}", JAXBHelper.toString(app));
        return Response.ok(app, MediaType.APPLICATION_JSON).build();
    }
}
