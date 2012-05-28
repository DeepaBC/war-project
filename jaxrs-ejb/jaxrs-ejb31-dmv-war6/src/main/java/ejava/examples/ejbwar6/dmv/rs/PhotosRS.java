package ejava.examples.ejbwar6.dmv.rs;

import java.net.URI;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbwar6.dmv.lic.dto.DrvLicRepresentation;
import ejava.examples.ejbwar6.dmv.lic.dto.Photo;
import ejava.examples.ejbwar6.dmv.svc.PhotosService;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a jax-rs interface for managing photos.
 */
@Path("photos")
public class PhotosRS {
    private static final Logger log = LoggerFactory.getLogger(PhotosRS.class);
    
    private @Context UriInfo uriInfo;
    private @EJB PhotosService service;

    @POST
    @Consumes(DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
    @Produces(DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
    @Formatted
    public Response createPhoto(Photo photo) {
        try {
                //create the photo
            Photo createdPhoto = service.createPhoto(photo);
                //generate links for valid follow-on actions
            URI self = new PhotosState(uriInfo).setHRefs(photo);
            
                //generate a checksum of the XML response for the ETag
            EntityTag eTag = new EntityTag(JAXBHelper.getTag(createdPhoto));
            
                //have clients cache the contents for up to 1yr
                //individual photos do not change
            CacheControl cacheControl = new CacheControl();
            cacheControl.setMaxAge(365*24*60*60);
            cacheControl.setSMaxAge(365*24*60*60);
            log.debug("created photo {}", self);
            
                //return the response
            return Response
                    .created(self)   //201-Created and a Location header of what was created
                    .entity(createdPhoto) //marshals the representation in response
                    .contentLocation(self) //Content-Location header of representation
                    .type(DrvLicRepresentation.DRVLIC_MEDIA_TYPE) //Content-Type header of representation
                    .lastModified(createdPhoto.getTimestamp()) //Last-Modified header of the representation
                    .tag(eTag)
                    .build();
        } 
        catch (Exception ex) {
            ex.printStackTrace();
            return Response.serverError()
                    .entity("server error:" + ex.getLocalizedMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }    

    @Path("{id}")
    @GET
    @Produces(DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
    @Formatted
    public Response getPhoto(@PathParam("id") long id) {
        log.debug("getting photo {}", id);
        Photo photo = service.getPhoto(id);
        if (photo != null) {
            URI self = new PhotosState(uriInfo).setHRefs(photo);
            return Response.ok(photo, DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
                    .lastModified(photo.getTimestamp())
                    .contentLocation(self)
                    .build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(String.format("photo %d not found", id))
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
