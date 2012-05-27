package ejava.examples.jaxrsrep.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamWriter;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;

/**
 * This class implements a custom JAX-RS marshaller to convert from JAXB 
 * objects to JSON using the Jettison library. Depending on annotations 
 * provided -- it will either us Mapped or Badgerfish formatting.
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JSONJettisonMarshaller extends JettisonJSONBase implements MessageBodyWriter<Object>{

    /**
     * If not a JAXB object -- we cannot marshal it
     */
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, 
            Annotation[] methodAnnotations, MediaType mediaType) {
        return !turnOff(methodAnnotations) &&
                type.isAnnotationPresent(XmlRootElement.class);
    }

    @Override
    public long getSize(Object object, Class<?> type, Type genericType,
            Annotation[] methodAnnotations, MediaType mediaType) {
        return -1; //says we don't know
    }

    @Override
    public void writeTo(Object object, Class<?> type, Type genericType,
            Annotation[] methodAnnotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream os)
            throws IOException, WebApplicationException {
        try {
            boolean badgerfish=isBadgerFish(methodAnnotations);
            String jsonString = !badgerfish ? 
                    marshalMappedJSON(object, getJSONMapping(methodAnnotations)) :
                    marshalBadgerFishJSON(object);
            os.write(jsonString.getBytes("UTF-8"));
            os.close();
        } catch (JAXBException ex) {
            throw new WebApplicationException(Response.serverError()
                    .entity("error marshalling JSON:" + ex.getLocalizedMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }
    }

    public String marshalMappedJSON(Object jaxbObject, 
            MappedNamespaceConvention mapping, Class<?>...clazzes) 
            throws JAXBException {
            //configure a JAXBContext to handle the object
        JAXBContext ctx = getJAXBContext(jaxbObject.getClass(), clazzes);
        
            //configure a stream to write the JSON
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlStreamWriter = new MappedXMLStreamWriter(mapping, writer);

            //marshall the JAXB object to a JSON String
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.marshal(jaxbObject, xmlStreamWriter);
        return writer.toString();
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
}
