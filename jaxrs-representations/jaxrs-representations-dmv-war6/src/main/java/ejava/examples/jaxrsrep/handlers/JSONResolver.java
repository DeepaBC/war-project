package ejava.examples.jaxrsrep.handlers;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBException;

import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.util.rest.Link;

@Provider
public class JSONResolver implements ContextResolver<MappedNamespaceConvention> {
    private static final Logger log = LoggerFactory.getLogger(JSONResolver.class);
    
    private MappedNamespaceConvention con;
    
    public JSONResolver() throws JAXBException {
        log.debug("creating resolver for Applications");

        Configuration config = new Configuration();
        Map<String, String> xmlToJsonNamespaces = new HashMap<String,String>();
        xmlToJsonNamespaces.put("http://ejava.info", "ejava");
        config.setXmlToJsonNamespaces(xmlToJsonNamespaces);
        con = new MappedNamespaceConvention(config);
    }
    
    @Override
    public MappedNamespaceConvention getContext(Class<?> type) {
        log.debug("getContext({})", type.getName());

        if (type.equals(Link.class)) {
            return con;
        }
        else {
            return null;
        }
    }

}
