package ejava.examples.restintro.dmv.resources;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import ejava.examples.restintro.dmv.dto.Application;
import ejava.examples.restintro.dmv.dto.Link;
import ejava.examples.restintro.dmv.dto.Representation;
import ejava.examples.restintro.dmv.dto.ResidentID;
import ejava.examples.restintro.dmv.dto.ResidentIDApplication;

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
                if (Representation.SELF_REL.equals(link.getRel())) {
                    link.setHref(selfURI(res.getId()));
                }
                else if (Representation.PHOTO_REL.equals(link.getRel())) {
                    link.setHref(photoURI(res.getId()));
                }
                else if (Representation.CREATE_PHOTO_REL.equals(link.getRel())) {
                    link.setHref(createPhotoURI(res.getId()));
                }
            }
        }
        return self;
    }
    
    protected URI selfURI(long id) {
        return uriInfo.getBaseUriBuilder()
                .path(ApplicationsRS.class)
                .path(ApplicationsRS.class, "getApplication")
                .build(id);
    }
    protected URI photoURI(long id) {
        return uriInfo.getBaseUriBuilder()
                //TODO: finish this
                .build();
    }
    protected URI createPhotoURI(long id) {
        return uriInfo.getBaseUriBuilder()
                //TODO: finish this
                .build();
    }
}
