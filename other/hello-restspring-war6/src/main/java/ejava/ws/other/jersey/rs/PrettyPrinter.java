package ejava.ws.other.jersey.rs;

import java.io.IOException;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Produces(MediaType.APPLICATION_XML)
public class PrettyPrinter implements MessageBodyWriter<Object> {
    Logger log = LoggerFactory.getLogger(PrettyPrinter.class);

    @Context protected Providers providers;

    public long getSize(Object object, Class<?> type, Type genericType,
            Annotation[] methodAnnotations, MediaType mediaType) {
        return -1; //says we don't know
    }

    public boolean isWriteable(Class<?> type, Type genericType, 
            Annotation[] methodAnnotations, MediaType mediaType) {
        boolean prettyPrint=false;
        for (Annotation annotation : methodAnnotations) {
            log.debug("methodAnnotation={}", annotation.toString());
            if (annotation.annotationType().equals(PrettyPrint.class)) {
                prettyPrint=true; break; 
            }
        }
        if (prettyPrint) {            
            for(Class<?> clazz = type;clazz != null; clazz=clazz.getSuperclass()) {
                if (clazz.isAnnotationPresent(XmlRootElement.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void writeTo(Object object, Class<?> type, Type genericType,
            Annotation[] methodAnnotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream os)
            throws IOException, WebApplicationException {
        log.debug("writeTo({})", object.getClass().getName());

        JAXBContext ctx = null;
        ContextResolver<JAXBContext> resolver = 
            providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE);
        if (resolver != null) {
            //try to locate a cached JAXB Context
            ctx = resolver.getContext(type);
        }
        log.debug("jaxbContext={}", ctx);
        if (ctx == null) {
            try {
                ctx = JAXBContext.newInstance(type);
            } catch (JAXBException ex) {
                throw new RuntimeException("error creating ad-hoc jaxbContext", ex);
            }
        }

        try {
            Marshaller marshaller = ctx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(object, os);
            os.flush();
        } catch (JAXBException ex) {
            throw new RuntimeException("error marshalling XML", ex);
        } finally {}
    }

}
