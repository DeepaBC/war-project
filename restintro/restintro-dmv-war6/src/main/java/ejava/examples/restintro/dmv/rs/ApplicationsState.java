package ejava.examples.restintro.dmv.rs;

import java.net.URI;


import javax.ws.rs.core.UriInfo;

import ejava.examples.restintro.dmv.lic.dto.Application;
import ejava.examples.restintro.dmv.lic.dto.DrvLicRepresentation;
import ejava.examples.restintro.dmv.lic.dto.ResidentIDApplication;
import ejava.util.rest.Link;

/**
 * This class bridges the internal state information for applications with the 
 * URI-based links exposed to the outside world.
 */
public class ApplicationsState {
    protected UriInfo uriInfo;
    public ApplicationsState(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }
    
    /**
     * This method will set the hrefs based on the application state, the
     * application properties, and the uri context information from the 
     * interface.
     * @param app
     * @return self URI optionally used for HTTP headers
     */
    public URI setHRefs(Application app) {
        URI self = selfURI(app.getId());
        for (Link link : app.getLinks()) {
            if (link.getHref() == null) {
                if (DrvLicRepresentation.APPROVE_REL.equals(link.getRel())) {
                    link.setHref(approveURI(app.getId()));
                }
                else if (DrvLicRepresentation.CANCEL_REL.equals(link.getRel())) {
                    link.setHref(cancelURI(app.getId()));
                }
                else if (DrvLicRepresentation.PAYMENT_REL.equals(link.getRel())) {
                    link.setHref(paymentURI(app.getId()));
                }
                else if (DrvLicRepresentation.REFUND_REL.equals(link.getRel())) {
                    link.setHref(refundURI(app.getId()));
                }
                else if (DrvLicRepresentation.REJECT_REL.equals(link.getRel())) {
                    link.setHref(rejectURI(app.getId()));
                }
                else if (DrvLicRepresentation.SELF_REL.equals(link.getRel())) {
                    link.setHref(self);
                }
                else if (app instanceof ResidentIDApplication) {
                    ResidentIDApplication resapp = (ResidentIDApplication)app;
                    
                    if (DrvLicRepresentation.RESID_REL.equals(link.getRel())) {
                        link.setHref(residentIdURI(resapp.getResid().getId()));
                    }
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
    protected URI cancelURI(long id) {
        return uriInfo.getBaseUriBuilder()
                .path(ApplicationsRS.class)
                .path(ApplicationsRS.class, "cancelApplication")
                .build(id);
    }
    protected URI approveURI(long id) {
        return uriInfo.getBaseUriBuilder()
                .path(ApplicationsRS.class)
                .path(ApplicationsRS.class, "approveApplication")
                .build(id);
    }
    protected URI rejectURI(long id) {
        return uriInfo.getBaseUriBuilder()
                .path(ApplicationsRS.class)
                .path(ApplicationsRS.class, "rejectApplication")
                .build(id);
    }
    protected URI paymentURI(long id) {
        return uriInfo.getBaseUriBuilder()
                .path(ApplicationsRS.class)
                .path(ApplicationsRS.class, "payApplication")
                .build(id);
    }
    protected URI refundURI(long id) {
        return uriInfo.getBaseUriBuilder()
                .path(ApplicationsRS.class)
                .path(ApplicationsRS.class, "refundApplicationPayment")
                .build(id);
    }
    protected URI residentIdURI(long id) {
        return uriInfo.getBaseUriBuilder()
                .path(ResidentsRS.class)
                .path(ResidentsRS.class, "getResidentID")
                .build(id);
    }
}
