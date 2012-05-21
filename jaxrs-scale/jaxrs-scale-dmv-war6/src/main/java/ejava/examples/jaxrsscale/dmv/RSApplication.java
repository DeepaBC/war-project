package ejava.examples.jaxrsscale.dmv;

import java.util.HashSet;



import java.util.Set;

import javax.ws.rs.ApplicationPath;

import javax.ws.rs.core.Application;

import ejava.examples.jaxrsscale.caching.CachingRS;

/**
 * The following is an example for activating JAX-RS functionality using
 * the no-XML approach. By creating a class that extends 
 * javax.ws.rs.core.Application and annotating with 
 * @javax.ws.rs.ApplicationPath -- we are signaling to the container that
 * this is a jax-ws application.
 */
@ApplicationPath("")
public class RSApplication extends Application {
    /*
    private Set<Object> singletons = new HashSet<Object>();
    private Set<Class<?>> classes = new HashSet<Class<?>>();
    
    public RSApplication() {
        //register per-request providers
        //classes.add(XXX.class);
        //classes.add(CachingRS.class);
        
        //register singleton providers
        //singletons.add(new XXX());
    }
    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
    */
}

