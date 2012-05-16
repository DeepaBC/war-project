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
        //return Response.ok(link, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalMappedJSON(link), MediaType.APPLICATION_JSON).build();
    }
    

    /**
     * This helper method encapsulates the building of a JSON Mapping configuration
     * to marshal and demarshal all mapped JSON.
     * @return
     */
    protected MappedNamespaceConvention getJSONMapping() {
        Configuration config = new Configuration();
        Map<String, String> xmlToJsonNamespaces = new HashMap<String,String>();
        xmlToJsonNamespaces.put("http://ejava.info", "ejava");
        xmlToJsonNamespaces.put("http://dmv.ejava.info", "dmv");
        xmlToJsonNamespaces.put("http://dmv.ejava.info/dap", "dmv-dap");
        xmlToJsonNamespaces.put("http://dmv.ejava.info/drvlic", "drvlic");
        xmlToJsonNamespaces.put("http://dmv.ejava.info/drvlic/dap", "drvlic-dap");
        config.setXmlToJsonNamespaces(xmlToJsonNamespaces);
        MappedNamespaceConvention con = new MappedNamespaceConvention(config);
        return con;
    }
    
    /**
     * Create a JAXContext suitable to marshal/demarshal the referenced classes
     * @param type
     * @param clazzes
     * @return
     * @throws JAXBException 
     */
    protected <T> JAXBContext getJAXBContext(Class<T> type, Class<?>...clazzes) 
            throws JAXBException {
        Class<?>[] classes = new Class<?>[clazzes.length+1];
        classes[0]=type;
        for (int i=0;i<clazzes.length; i++) {
            classes[i+1]=clazzes[i];
        }
        return JAXBContext.newInstance(classes);
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
            //configure a JAXBContext to handle the object
        JAXBContext ctx = getJAXBContext(type, clazzes);
        
            //configure a stream to read the JSON
        JSONObject obj = new JSONObject(jsonString);
        XMLStreamReader xmlStreamReader = new MappedXMLStreamReader(obj, getJSONMapping());
        
            //demarshall the stream into a JAXB object
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        return (T)unmarshaller.unmarshal(xmlStreamReader);
    }

    public String marshalMappedJSON(Object jaxbObject, Class<?>...clazzes) 
            throws JAXBException {
            //configure a JAXBContext to handle the object
        JAXBContext ctx = getJAXBContext(jaxbObject.getClass(), clazzes);
        
            //configure a stream to write the JSON
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlStreamWriter = new MappedXMLStreamWriter(getJSONMapping(), writer);

            //marshall the JAXB object to a JSON String
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.marshal(jaxbObject, xmlStreamWriter);
        return writer.toString();
    }

    @SuppressWarnings("unchecked")
    public <T> T demarshalBadgerFishJSON(Class<T> type, String jsonString, Class<?>...clazzes) 
            throws JAXBException, JSONException, XMLStreamException {
            //configure a JAXBContext to handle the object
        JAXBContext ctx = getJAXBContext(type, clazzes);

            //configure a stream to read the JSON
        JSONObject obj = new JSONObject(jsonString);
        XMLStreamReader xmlStreamReader = new BadgerFishXMLStreamReader(obj);
        
            //demarshall the stream into a JAXB object
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        return (T)unmarshaller.unmarshal(xmlStreamReader);
    }
    
    public String marshalBadgerFishJSON(Object jaxbObject, Class<?>...clazzes) 
            throws JAXBException {
            //configure a JAXBContext to handle the object
        JAXBContext ctx = getJAXBContext(jaxbObject.getClass(), clazzes);
    
        //configure a stream to write the JSON
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlStreamWriter = new BadgerFishXMLStreamWriter(writer);

        //marshall the JAXB object to a JSON String
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.marshal(jaxbObject, xmlStreamWriter);
        return writer.toString();
    }

    
    
    

    @PUT @Path("attributes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
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
        //return Response.ok(link, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalMappedJSON(link), MediaType.APPLICATION_JSON).build();
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
        //return Response.ok(link, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalBadgerFishJSON(link), MediaType.APPLICATION_JSON).build();
    }
    
    @PUT @Path("attributes/badgerfish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putJSONLinkBadgerfish(String jsonString) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("received:{}", jsonString);
        
        Link link = demarshalBadgerFishJSON(Link.class, jsonString);
        log.debug("unmarshalled to:{}", JAXBHelper.toString(link));
        
        link.setHref(uriInfo.getRequestUri());
        link.setType(MediaType.APPLICATION_JSON);
        log.debug("returning:{}", JAXBHelper.toString(link));
        //return Response.ok(link, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalBadgerFishJSON(link), MediaType.APPLICATION_JSON).build();
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
        //return Response.ok(contact, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalMappedJSON(contact), MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("elements")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Mapped(namespaceMap = {
        @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic")
    })    
    public Response putContact(String jsonString) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("received:{}", jsonString);
        
        ContactInfo contact = demarshalMappedJSON(ContactInfo.class, jsonString);
        log.debug("returning:{}", JAXBHelper.toString(contact));
        //return Response.ok(contact, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalMappedJSON(contact), MediaType.APPLICATION_JSON).build();
    }

    @PUT @Path("elements/badgerfish")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putContactBadgerfish(ContactInfo contact) throws JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(contact));
        //return Response.ok(contact, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalBadgerFishJSON(contact), MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("elements/badgerfish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putJSONContactBadgerfish(String jsonString) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("received:{}", jsonString);
        
        ContactInfo contact = demarshalBadgerFishJSON(ContactInfo.class, jsonString);
        log.debug("returning:{}", JAXBHelper.toString(contact));
        //return Response.ok(contact, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalBadgerFishJSON(contact), MediaType.APPLICATION_JSON).build();
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
        //return Response.ok(person, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalMappedJSON(person), MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("collection")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
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
        //return Response.ok(person, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalMappedJSON(person), MediaType.APPLICATION_JSON).build();
    }
    
    @PUT @Path("collection/badgerfish")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putPersonBadgerfish(Person person) throws JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(person));
        //return Response.ok(person, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalBadgerFishJSON(person), MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("collection/badgerfish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putJSONPersonBadgerfish(String jsonString) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("received:{}", jsonString);
        
        Person person = demarshalBadgerFishJSON(Person.class, jsonString);
        
        log.debug("returning:{}", JAXBHelper.toString(person));
        //return Response.ok(person, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalBadgerFishJSON(person), MediaType.APPLICATION_JSON).build();
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
        //return Response.ok(residentId, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalMappedJSON(residentId), MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("reference")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Mapped(namespaceMap = {
            @XmlNsMap(namespace = "http://dmv.ejava.info/drvlic", jsonName = "drvlic")
    })    
    public Response putJSONResidentID(String jsonString) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("received:{}", jsonString);
        
        ResidentID residentId = demarshalMappedJSON(ResidentID.class, jsonString);
        log.debug("returning:{}", JAXBHelper.toString(residentId));
        //return Response.ok(residentId, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalMappedJSON(residentId), MediaType.APPLICATION_JSON).build();
    }
    
    @PUT @Path("reference/badgerfish")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putJSONResidentIDBadgerfish(ResidentID residentId) throws JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("returning:{}", JAXBHelper.toString(residentId));
        //return Response.ok(residentId, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalBadgerFishJSON(residentId), MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("reference/badgerfish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putJSONResidentIDBadgerfish(String jsonString) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("received:{}", jsonString);
        
        ResidentID residentId=demarshalBadgerFishJSON(ResidentID.class, jsonString);
        log.debug("returning:{}", JAXBHelper.toString(residentId));
        //return Response.ok(residentId, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalBadgerFishJSON(residentId), MediaType.APPLICATION_JSON).build();
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
        //return Response.ok(app, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalMappedJSON(app), MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("jaxbContext")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
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
        //return Response.ok(app, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalMappedJSON(app), MediaType.APPLICATION_JSON).build();
    }

    @PUT @Path("jaxbContext/badgerfish")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putApplicationBadgerfish(Application app) throws JAXBException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("accept={}", headers.getRequestHeader("Accept"));
        log.debug("returning:{}", JAXBHelper.toString(app));
        //return Response.ok(app, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalBadgerFishJSON(app), MediaType.APPLICATION_JSON).build();
    }
    @PUT @Path("jaxbContext/badgerfish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @BadgerFish
    public Response putJSONApplicationBadgerfish(String jsonString) 
            throws JAXBException, JSONException, XMLStreamException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("accept={}", headers.getRequestHeader("Accept"));
        log.debug("received:{}", jsonString);
        
        Application app = demarshalBadgerFishJSON(Application.class, jsonString, 
                ResidentIDApplication.class);
        log.debug("returning:{}", JAXBHelper.toString(app));
        //return Response.ok(app, MediaType.APPLICATION_JSON).build();
        return Response.ok(marshalBadgerFishJSON(app), MediaType.APPLICATION_JSON).build();
    }
}
