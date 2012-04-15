package ejava.examples.restintro.dmv.resources;

import java.net.URI;


import javax.ws.rs.core.UriInfo;

import ejava.examples.restintro.dmv.lic.dto.DrvLicRepresentation;
import ejava.examples.restintro.dmv.lic.dto.Photo;
import ejava.examples.restintro.dmv.lic.dto.ResidentID;
import ejava.util.rest.Link;

/**
 * This class bridges the internal state information for the representation 
 * with the URI-based links exposed to the outside world.
 */
public class PhotosState {
    protected UriInfo uriInfo;
    public PhotosState(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }
    
    /**
     * This method will set the hrefs based on the state, properties, and the 
     * uri context information from the interface.
     * @param photo
     * @return self URI optionally used for HTTP headers
     */
    public URI setHRefs(Photo photo) {
        URI self = selfURI(photo.getId());
        for (Link link : photo.getLinks()) {
            if (link.getHref() == null) {
                if (DrvLicRepresentation.SELF_REL.equals(link.getRel())) {
                    link.setHref(selfURI(photo.getId()));
                }
                else if (DrvLicRepresentation.CREATE_PHOTO_REL.equals(link.getRel())) {
                    link.setHref(createPhotoURI());
                }
            }
        }
        return self;
    }
    
    protected URI selfURI(long id) {
        return uriInfo.getBaseUriBuilder()
                .path(PhotosRS.class)
                .path(PhotosRS.class, "getPhoto")
                .build(id);
    }
    protected URI createPhotoURI() {
        return uriInfo.getBaseUriBuilder()
                .path(PhotosRS.class)
                .build();
    }
}
