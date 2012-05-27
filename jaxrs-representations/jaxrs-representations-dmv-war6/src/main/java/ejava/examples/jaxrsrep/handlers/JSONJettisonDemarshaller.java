package ejava.examples.jaxrsrep.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamReader;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;

/**
 * This class implements a custom JAX-RS demarshaller to convert from 
 * JSON objects/strings into JAXB object instances using Jettison libraries. 
 * Depending on annotations provided -- it will either use Mapped or Badgerfish 
 * formatting.
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class JSONJettisonDemarshaller extends JettisonJSONBase 
    implements MessageBodyReader<Object> {
    
    /**
     * If type not a JAXB object -- we cannot unmarshal it
     */
    @Override
    public boolean isReadable(Class<?> type, Type genericType, 
            Annotation[] annotations, MediaType mediaType) {
        return type.isAnnotationPresent(XmlRootElement.class);
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, 
            Annotation[] paramAnnotations,
            MediaType mediaType, MultivaluedMap<String, String> headers,
            InputStream is) throws IOException, WebApplicationException {
        try {
            boolean badgerfish=isBadgerFish(paramAnnotations);
            String jsonString = IOUtils.toString(is);
            return !badgerfish ? 
                    demarshalMappedJSON(type, jsonString, getJSONMapping(paramAnnotations)) :
                    demarshalBadgerFishJSON(type, jsonString);
        } catch (JAXBException ex) {
            throw new WebApplicationException(Response.serverError()
                    .entity("error marshalling JSON:" + ex.getLocalizedMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        } catch (JSONException ex) {
            throw new WebApplicationException(Response.serverError()
                    .entity("error marshalling JSON:" + ex.getLocalizedMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        } catch (XMLStreamException ex) {
            throw new WebApplicationException(Response.serverError()
                    .entity("error marshalling JSON:" + ex.getLocalizedMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T demarshalMappedJSON(Class<T> type, String jsonString, 
            MappedNamespaceConvention mapping, Class<?>...clazzes) 
            throws JAXBException, JSONException, XMLStreamException {
            //configure a JAXBContext to handle the object
        JAXBContext ctx = getJAXBContext(type, clazzes);
        
            //configure a stream to read the JSON
        JSONObject obj = new JSONObject(jsonString);
        XMLStreamReader xmlStreamReader = new MappedXMLStreamReader(obj, mapping);
        
            //demarshall the stream into a JAXB object
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        return (T)unmarshaller.unmarshal(xmlStreamReader);
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

}
