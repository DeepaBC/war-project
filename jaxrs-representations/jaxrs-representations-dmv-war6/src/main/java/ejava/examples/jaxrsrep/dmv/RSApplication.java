package ejava.examples.jaxrsrep.dmv;

import java.util.HashSet;


import java.util.Set;

import javax.ws.rs.ApplicationPath;

import javax.ws.rs.core.Application;
import javax.xml.bind.JAXBException;

import ejava.examples.jaxrsrep.handlers.ApplicationResolver;
import ejava.examples.jaxrsrep.handlers.ContentHandlerDemoRS;
import ejava.examples.jaxrsrep.handlers.JSONDemarshaller;
import ejava.examples.jaxrsrep.handlers.JSONHandlerDemoRS;
import ejava.examples.jaxrsrep.handlers.JSONMarshaller;
import ejava.examples.jaxrsrep.handlers.XMLHandlerDemoRS;

/**
 * The following is an example for activating JAX-RS functionality using
 * the no-XML approach. By creating a class that extends 
 * javax.ws.rs.core.Application and annotating with 
 * @javax.ws.rs.ApplicationPath -- we are signaling to the container that
 * this is a jax-ws application.
 */
@ApplicationPath("")
public class RSApplication extends Application {
    private Set<Object> singletons = new HashSet<Object>();
    private Set<Class<?>> classes = new HashSet<Class<?>>();
    
    public RSApplication() {
        //register per-request providers
        classes.add(ContentHandlerDemoRS.class);
        classes.add(XMLHandlerDemoRS.class);
        classes.add(JSONHandlerDemoRS.class);
        
        //register singleton providers
        try {
            singletons.add(new ApplicationResolver());
            singletons.add(new JSONMarshaller());
            singletons.add(new JSONDemarshaller());
        } catch (JAXBException ex) {
            throw new RuntimeException("unable to register singleton", ex);
        }
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}

