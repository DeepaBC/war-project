package ejava.exercises.jaxrsrep.bank;

import javax.ws.rs.ApplicationPath;

import javax.ws.rs.core.Application;

/**
 * The following is an example for activating JAX-RS functionality using
 * the no-XML approach. By creating a class that extends 
 * javax.ws.rs.core.Application and annotating with 
 * @javax.ws.rs.ApplicationPath -- we are signaling to the container that
 * this is a jax-ws application.
 */
@ApplicationPath("rest")
public class RSApplication extends Application {
}
