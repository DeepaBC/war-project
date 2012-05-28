package ejava.examples.ejbear6.dmv.rs;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;

import ejava.examples.ejbear6.dmv.dto.DmvRepresentation;

/**
 * This class implements the @Local interface for an EJB directly exposed as a
 * JAX-RS resource.
 */
@Path("dmv")
public interface DmvRS {
    @GET
    @Produces(DmvRepresentation.DMV_MEDIA_TYPE)
    @Formatted
    public abstract Response getDMV(            
        @Context UriInfo uriInfo,
        @Context HttpServletRequest httpRequest);
}