package ejava.ws.other.jersey.rs;

import info.ejava.organization.Org;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;

import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.ws.other.jersey.model.Organization;
import ejava.ws.other.jersey.model.Person;
import ejava.ws.other.jersey.xml.MyJAXBContext;

/**
 * This class provides a JAXBContext that can marshal/demarshal Application
 * JAXB objects. It is consulted when the JAX-RS provider needs to process 
 * XML to/from a JAXB class and allows the context to be long-lived thru
 * the application.
 */
@Provider
@Consumes(MediaType.APPLICATION_XML)
@Produces(MediaType.APPLICATION_XML)
public class MyContextResolver implements ContextResolver<JAXBContext> {
    private static final Logger log = LoggerFactory.getLogger(MyContextResolver.class);
    private JAXBContext ctx;
    
    public MyContextResolver() throws JAXBException {
        log.debug("creating resolver for Applications");
        ctx = new MyJAXBContext();
    }

    //@Override
    public JAXBContext getContext(Class<?> type) {
        log.debug("getContext({})", type.getName());

        if (type.equals(Person.class) ||
                type.equals(Org.class) ||
                type.equals(Organization.class)) {
            return ctx;
        }
        else {
            return null;
        }
    }
}
