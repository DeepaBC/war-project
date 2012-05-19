package ejava.examples.jaxrsscale.dmv.rs;

import java.net.URI;


import javax.ws.rs.core.UriInfo;

import ejava.examples.jaxrsscale.dmv.lic.dto.DrvLicRepresentation;
import ejava.examples.jaxrsscale.dmv.lic.dto.ResidentID;
import ejava.util.rest.Link;

/**
 * This class bridges the internal state information for resident IDs with the 
 * URI-based links exposed to the outside world.
 */
public class ResidentIDsState {
    protected UriInfo uriInfo;
    public ResidentIDsState(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }
    
    /**
     * This method will set the hrefs based on the resident ID state, the
     * resident ID properties, and the uri context information from the 
     * interface.
     * @param res
     * @return self URI optionally used for HTTP headers
     */
    public URI setHRefs(ResidentID res) {
        URI self = selfURI(res.getId());
        for (Link link : res.getLinks()) {
            if (link.getHref() == null) {
                if (DrvLicRepresentation.SELF_REL.equals(link.getRel())) {
                    link.setHref(selfURI(res.getId()));
                }
                else if (DrvLicRepresentation.PHOTO_REL.equals(link.getRel())) {
                    link.setHref(photoURI(res.getId()));
                }
                else if (DrvLicRepresentation.SET_PHOTO_REL.equals(link.getRel())) {
                    link.setHref(setPhotoURI(res.getId()));
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
                .path(ResidentsRS.class)
                .path(ResidentsRS.class, "getResidentID")
                .build(id);
    }
    protected URI photoURI(long id) {
        return uriInfo.getBaseUriBuilder()
                .path(PhotosRS.class)
                .path(PhotosRS.class,"getPhoto")
                .build(id);
    }
    protected URI createPhotoURI() {
        return uriInfo.getBaseUriBuilder()
                .path(PhotosRS.class)
                .build();
    }
    protected URI setPhotoURI(long id) {
        return uriInfo.getBaseUriBuilder()
                .path(ResidentsRS.class)
                .path(ResidentsRS.class, "setPhoto")
                .build(id);
    }
}
