package ejava.examples.jaxrsrep.dmv.rs;

import java.net.URI;


import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;

import ejava.examples.jaxrsrep.dmv.lic.dto.DrvLicRepresentation;
import ejava.examples.jaxrsrep.dmv.lic.dto.ResidentID;
import ejava.examples.jaxrsrep.dmv.svc.ResidentsService;
import ejava.util.rest.Link;

/**
 * This class implements a jax-rs interface for managing residentIDs
 */
@Path("/residents")
public class ResidentsRS {
    
    private @Context UriInfo uriInfo;
    
    private @Inject ResidentsService service;

    @Path("{id}")
    @GET
    @Produces(DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
    @Formatted
    public Response getResidentID(@PathParam("id") long id) {
        ResidentID resid = service.getResident(id);
        if (resid != null) {
            URI self = new ResidentIDsState(uriInfo).setHRefs(resid);
            return Response.ok(resid, DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
                    .lastModified(resid.getUpdated())
                    .contentLocation(self)
                    .build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(String.format("residentID %d not found", id))
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    @Path("{id}")
    @PUT
    @Consumes(DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
    @Produces(DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
    @Formatted
    public Response updateResidentID(ResidentID update) {
        ResidentID updated=null;
        if ((updated=service.updateResident(update)) != null) {
            URI self = new ResidentIDsState(uriInfo).setHRefs(updated);
            return Response.ok(updated, DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
                    .lastModified(updated.getUpdated())
                    .contentLocation(self)
                    .build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(String.format("unable to update residentID %d", update.getId()))
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }    
    
    @Path("{id}/photo")
    @PUT
    //@Consumes(DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
    @Produces(DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
    @Formatted
    public Response setPhoto(@PathParam("id") long id, Link photoLink) {
        ResidentID updated=null;
        if ((updated=service.setPhoto(id, photoLink)) != null) {
            URI self = new ResidentIDsState(uriInfo).setHRefs(updated);
            return Response.ok(updated, DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
                    .lastModified(updated.getUpdated())
                    .contentLocation(self)
                    .build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(String.format("unable to add photo to residentID %d", id))
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    
}
