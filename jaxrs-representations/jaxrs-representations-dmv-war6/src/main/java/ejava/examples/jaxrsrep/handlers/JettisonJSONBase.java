package ejava.examples.jaxrsrep.handlers;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.jboss.resteasy.annotations.providers.jaxb.json.BadgerFish;
import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a base class used by our custom Jettison JSON marshaller and 
 * demarshaller. It contains helper methods generic to Mapped and BadgerFish
 * format techniques.
 */
public class JettisonJSONBase {
    static final Logger log = LoggerFactory.getLogger(JettisonJSONBase.class);
    @Context protected Providers providers;
    
    protected MappedNamespaceConvention getJSONMapping(Annotation[] methodAnnotations) {
        Configuration config = new Configuration();
        Map<String, String> xmlToJsonNamespaces = new HashMap<String,String>();
        for (Annotation annotation: methodAnnotations) {
            log.debug("annotation={}", annotation);
            if (annotation.annotationType().equals(Mapped.class)) {
                for (XmlNsMap map: ((Mapped)annotation).namespaceMap()) {
                    xmlToJsonNamespaces.put(map.namespace(), map.jsonName());
                    log.debug("mapped {} to {}",map.namespace(), map.jsonName());
                }
            }
        }
        config.setXmlToJsonNamespaces(xmlToJsonNamespaces);
        MappedNamespaceConvention con = new MappedNamespaceConvention(config);
        return con;
    }

    protected <T> JAXBContext getJAXBContext(Class<T> type, Class<?>...clazzes)
            throws JAXBException {
        JAXBContext ctx = null;
        if (providers != null) {
            ContextResolver<JAXBContext> resolver = 
                providers.getContextResolver(JAXBContext.class, MediaType.WILDCARD_TYPE);
            if (resolver != null) {
                //try to locate a cached JAXB Context
                ctx = resolver.getContext(type);
            }
        }
        if (ctx == null) {
                //none found -- create what we need here
            Class<?>[] classes = new Class<?>[clazzes.length+1];
            classes[0]=type;
            for (int i=0;i<clazzes.length; i++) {
                classes[i+1]=clazzes[i];
            }
            ctx = JAXBContext.newInstance(classes);
        }
        return ctx;        
    }

    /**
     * This method will return true if the NoEjavaJettison annotation
     * exists in the array. The annotation is used to by-pass out custom
     * Jettison marshaller and use the one built into the JAX-RS provider
     * or some other source.
     * @param annotations
     * @return
     */
    protected boolean turnOff(Annotation[] annotations) {
        boolean turnOff=false;
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(NoEJavaJettison.class)) {
                turnOff=true; break; 
            }
        }
        return turnOff;
    }

    protected boolean isBadgerFish(Annotation[] annotations) {
        boolean badgerfish=false;
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(BadgerFish.class)) {
                badgerfish=true; break; 
            }
        }
        return badgerfish;
    }
}