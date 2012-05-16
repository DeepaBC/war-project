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

public class JettisonJSONBase {
    @Context protected Providers providers;

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

    protected <T> JAXBContext getJAXBContext(Class<T> type, MediaType mediaType, Class<?>...clazzes)
            throws JAXBException {
        JAXBContext ctx = null;
        if (providers != null) {
            ContextResolver<JAXBContext> resolver = 
                    providers.getContextResolver(JAXBContext.class, mediaType);
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

    protected boolean isBadgerFish(Annotation[] annotations) {
        boolean badgerfish=false;
        for (Annotation annotation : annotations) {
            if (annotation.equals(BadgerFish.class)) {
                badgerfish=true; break;
            }
        }
        return badgerfish;
    }
}