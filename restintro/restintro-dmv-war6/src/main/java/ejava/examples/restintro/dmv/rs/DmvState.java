package ejava.examples.restintro.dmv.rs;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import ejava.examples.restintro.dmv.dto.DMV;
import ejava.examples.restintro.dmv.dto.DmvRepresentation;
import ejava.util.rest.Link;

/**
 * This class bridges the internal state information for the DMV with the 
 * URI-based links exposed to the outside world.
 */
public class DmvState {
    protected UriInfo uriInfo;
    public DmvState(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }
    
    /**
     * This method will set the hrefs based on the application state, the
     * application properties, and the uri context information from the 
     * interface.
     * @param dmv
     * @return self URI optionally used for HTTP headers
     */
    public URI setHRefs(DMV dmv) {
        URI self = selfURI();        
        for (Link link : dmv.getLinks()) {
            if (link.getHref() == null) {
                if (DmvRepresentation.SELF_REL.equals(link.getRel())) {
                    link.setHref(self);
                }
                else if (DmvRepresentation.RESID_APP_REL.equals(link.getRel())) {
                    link.setHref(residURI());
                }
            }
        }
        return self;
    }
    
    public URI selfURI() {
        return uriInfo.getBaseUriBuilder()
                .path(DmvRS.class)
                .build();
    }
    public URI residURI() {
        return uriInfo.getBaseUriBuilder()
                .path(ApplicationsRS.class)
                .build();
    }
}
