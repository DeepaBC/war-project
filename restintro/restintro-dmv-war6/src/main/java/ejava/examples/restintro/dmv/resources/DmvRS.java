package ejava.examples.restintro.dmv.resources;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;

import ejava.examples.restintro.dmv.dto.DMV;
import ejava.examples.restintro.dmv.dto.DMVRepresentation;

/**
 * This class implements the web interface for the main entry point to the
 * DMV
 */
@Path("dmv")
public class DmvRS {
    
    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(DMVRepresentation.DMV_MEDIA_TYPE)
    @Formatted
    public Response getDMV() {
        DMV dmv = new DMV(); //normally would go to backend to determine these services
        URI self = new DmvState(uriInfo).setHRefs(dmv);
        
        return Response.ok(dmv, DMVRepresentation.DMV_MEDIA_TYPE)
                .contentLocation(self)
                .build();
    }
}
