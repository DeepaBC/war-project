package ejava.examples.jaxrsrep.handlers;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.jaxrsrep.dmv.lic.dto.Application;
import ejava.examples.jaxrsrep.dmv.lic.dto.ContactInfo;
import ejava.examples.jaxrsrep.dmv.lic.dto.Person;
import ejava.examples.jaxrsrep.dmv.lic.dto.PhysicalDetails;
import ejava.examples.jaxrsrep.dmv.lic.dto.ResidentID;
import ejava.examples.jaxrsrep.dmv.lic.dto.ResidentIDApplication;

/**
 * This class provides a JAXBContext that can marshal/demarshal Application
 * JAXB objects. It is consulted when the JAX-RS provider needs to process 
 * XML to/from a JAXB class and allows the context to be long-lived thru
 * the application.
 */
@Provider
public class ApplicationResolver implements ContextResolver<JAXBContext> {
    private static final Logger log = LoggerFactory.getLogger(ApplicationResolver.class);
    
    private JAXBContext ctx;
    
    public ApplicationResolver() throws JAXBException {
        log.debug("creating resolver for Applications");
        ctx = JAXBContext.newInstance(Application.class,
                ResidentIDApplication.class,
                ResidentID.class,
                Person.class,
                ContactInfo.class,
                PhysicalDetails.class);
    }
    
    @Override
    public JAXBContext getContext(Class<?> type) {
        log.debug("getContext({})", type.getName());

        if (type.equals(ResidentIDApplication.class) ||
                type.equals(Application.class)) {
            return ctx;
        }
        else {
            return null;
        }
    }

}
