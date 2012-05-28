package ejava.examples.ejbwar6.dmv.rs;

import java.io.IOException;




import java.net.URI;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbwar6.dmv.lic.dto.Application;
import ejava.examples.ejbwar6.dmv.lic.dto.Applications;
import ejava.examples.ejbwar6.dmv.lic.dto.DrvLicRepresentation;
import ejava.examples.ejbwar6.dmv.lic.dto.ResidentIDApplication;
import ejava.examples.ejbwar6.dmv.svc.ApplicationsService;
import ejava.examples.ejbwar6.dmv.svc.BadArgument;
import ejava.util.rest.Link;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements CRUD-based resource access to Applications using
 * a JAX-RS API and no additional headers.
 */
@Local(ApplicationsRS.class)
@Stateless
public class ApplicationsRSEJB implements ApplicationsRS {
    private static final Logger log = LoggerFactory.getLogger(ApplicationsRSEJB.class);
    private @Resource SessionContext ctx;
    @Inject
    private ApplicationsService service;

    @PostConstruct
    public void init() {
        log.info("*** ApplicationsRSEJB ***");
        log.info("ctx={}", ctx);
    }

    @Override
    public Response createApplication(
            ResidentIDApplication app,
            UriInfo uriInfo) {
        try {
            Application createdApp = service.createApplication(app);
            URI uri=uriInfo.getAbsolutePathBuilder()
                    .path(ApplicationsRS.class, "getApplicationById")
                    .build(createdApp.getId());
            return Response
                    .created(uri)   //201-Created and a Location header of what was created
                    .entity(createdApp) //marshals the representation in response
                    .contentLocation(uri) //Content-Location header of representation
                    .type(MediaType.APPLICATION_XML) //Content-Type header of representation
                    .lastModified(createdApp.getUpdated()) //Last-Modified header of the representation
                    .build();
        } 
        catch (BadArgument ex) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("client error:" + ex.getLocalizedMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        catch (Exception ex) {
            return Response.serverError()
                    .entity("server error:" + ex.getLocalizedMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    @Override
    public Response createApplicationHM(ResidentIDApplication app, UriInfo uriInfo) {
        try {
            Application createdApp = service.createApplication(app);
            URI cancel = uriInfo.getAbsolutePathBuilder()
                    .path(ApplicationsRS.class, "cancelApplication")
                    .build(createdApp.getId());
            URI approve = uriInfo.getAbsolutePathBuilder()
                    .path(ApplicationsRS.class, "approveApplication")
                    .build(createdApp.getId());
            URI reject = uriInfo.getAbsolutePathBuilder()
                    .path(ApplicationsRS.class, "rejectApplication")
                    .build(createdApp.getId());
            URI uri=uriInfo.getAbsolutePathBuilder()
                    .path(ApplicationsRS.class, "getApplicationById")
                    .build(createdApp.getId());
                //set the resource's next state transitions
            createdApp.clearLinks();
            createdApp.setApprove(approve);
            createdApp.setCancel(cancel);
            createdApp.setReject(reject);
            return Response
                    .created(uri)   //201-Created and a Location header of what was created
                    .entity(createdApp) //marshals the representation in response
                    .contentLocation(uri) //Content-Location header of representation
                    .type(MediaType.APPLICATION_XML) //Content-Type header of representation
                    .lastModified(createdApp.getUpdated()) //Last-Modified header of the representation
                    .build();
        } 
        catch (BadArgument ex) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("client error:" + ex.getLocalizedMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        catch (Exception ex) {
            return Response.serverError()
                    .entity("server error:" + ex.getLocalizedMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    @Override
    public Response createApplicationHM2(ResidentIDApplication app, UriInfo uriInfo) {
        try {
            Application createdApp = service.createApplication(app);
            URI cancel = uriInfo.getAbsolutePathBuilder()
                    .path(ApplicationsRS.class, "cancelApplication")
                    .build(createdApp.getId());
            URI approve = uriInfo.getAbsolutePathBuilder()
                    .path(ApplicationsRS.class, "approveApplication")
                    .build(createdApp.getId());
            URI reject = uriInfo.getAbsolutePathBuilder()
                    .path(ApplicationsRS.class, "rejectApplication")
                    .build(createdApp.getId());
            URI uri=uriInfo.getAbsolutePathBuilder()
                    .path(ApplicationsRS.class, "getApplicationById")
                    .build(createdApp.getId());
                //set the resource's next state transitions
            createdApp.clearLinks();
            createdApp.addLink(new Link(DrvLicRepresentation.SELF_REL, uri));
            createdApp.addLink(new Link(DrvLicRepresentation.CANCEL_REL, cancel));
            createdApp.addLink(new Link(DrvLicRepresentation.APPROVE_REL, approve));
            createdApp.addLink(new Link(DrvLicRepresentation.REJECT_REL, reject));
            return Response
                    .created(uri)   //201-Created and a Location header of what was created
                    .entity(createdApp) //marshals the representation in response
                    .contentLocation(uri) //Content-Location header of representation
                    .type(MediaType.APPLICATION_XML) //Content-Type header of representation
                    .lastModified(createdApp.getUpdated()) //Last-Modified header of the representation
                    .build();
        } 
        catch (BadArgument ex) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("client error:" + ex.getLocalizedMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        catch (Exception ex) {
            return Response.serverError()
                    .entity("server error:" + ex.getLocalizedMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }


    @Override
    public Response getApplicationById(long id, UriInfo uriInfo) {
        Application app = service.getApplication(id);
        if (app == null) {
            return Response.status(Status.NOT_FOUND)
                    .entity("unable to locate id:" + id)
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        
        URI uri=uriInfo.getAbsolutePath();
        return Response
            .ok(app, MediaType.APPLICATION_XML)
            .contentLocation(uri) //Content-Location header of representation
            .lastModified(app.getUpdated()) //Last-Modified header of the representation
            .build();
    }

    @Override
    public Response updateApplication(String appString, UriInfo uriInfo) {
        //marshal to string; demarshal locally to have more control over transform 
        try {
            Application app = JAXBHelper.unmarshall(appString, Application.class, null, 
                    Application.class,
                    ResidentIDApplication.class);
            int status=0;
            if ((status=service.updateApplication(app))<0) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("unable to update application - client error")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
            else if (status > 0) {
                Application existingApp = service.getApplication(app.getId());
                return Response.status(Status.CONFLICT)
                        .entity(existingApp)
                        .type(MediaType.APPLICATION_XML)
                        .contentLocation(uriInfo.getAbsolutePath())
                        .lastModified(existingApp.getUpdated())
                        .build();
            }
            return Response.noContent().build();
        } catch (JAXBException ex) {
            ex.printStackTrace();
            return Response.serverError()
                           .entity("JAXBException handling updateApplication:" + ex)
                           .type(MediaType.TEXT_PLAIN)
                           .build();
        } catch (IOException ex) {
            ex.printStackTrace();
            return Response.serverError()
                    .entity("IOException handling updateApplication:" + ex)
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } finally {}
    }

    @Override
    public Response deleteApplication(long id, UriInfo uriInfo) {
        int status=0;
        if ((status=service.deleteApplication(id)) < 0) {
            return Response.status(Status.NOT_FOUND)
                    .entity("unable to locate application:" + id)
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } 
        else if (status > 0) {
            return Response.status(405)
                    .header("Allow", "GET, HEAD")
                    .entity("completed application cannot be deleted")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
            //application deleted
        return Response.noContent()
                    .build();
    }

    @Override
    public Response getApplications(Boolean active, int start, int count, UriInfo uriInfo) {
        
        //get the requested resource
        Applications apps=service.getApplications(active, start, count);
        
        //determine lastModified application or current time if 
        Date lastModified = null;
        if (apps.size() > 0) {
            for (Application app : apps) {
                if (lastModified == null || app.getUpdated().getTime() > lastModified.getTime()) {
                    lastModified=app.getUpdated();
                }
            }
        }
        else {
            lastModified=new Date();
        }
        
        URI uri=uriInfo.getAbsolutePath();
        return Response
            .ok(apps, MediaType.APPLICATION_XML)
            .contentLocation(uri) //Content-Location header of representation
            .lastModified(lastModified) //Last-Modified header of the representation
            .build();
    }
    
    @Override
    public void purgeApplications() {
        log.info("purging applications");
        service.purgeApplications();
    }
    
    
    @Override
    public Response createApplicationRep(ResidentIDApplication app, UriInfo uriInfo) {
        try {
                //create the application
            Application createdApp = service.createApplication(app);
            //generate links for valid follow-on actions
            URI self = new ApplicationsState(uriInfo).setHRefs(app);
            
                //return the response
            return Response
                    .created(self)   //201-Created and a Location header of what was created
                    .entity(createdApp) //marshals the representation in response
                    .contentLocation(self) //Content-Location header of representation
                    .type(DrvLicRepresentation.DRVLIC_MEDIA_TYPE) //Content-Type header of representation
                    .lastModified(createdApp.getUpdated()) //Last-Modified header of the representation
                    .build();
        } 
        catch (BadArgument ex) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("client error:" + ex.getLocalizedMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        catch (Exception ex) {
            return Response.serverError()
                    .entity("server error:" + ex.getLocalizedMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    /* (non-Javadoc)
     * @see ejava.examples.ejbwar6.dmv.rsc.ApplicationsRS#getApplication(long)
     */
    @Override
    public Response getApplication(long id, UriInfo uriInfo) {
        Application app = service.getApplication(id);
        if (app == null) {
            return Response.status(Status.NOT_FOUND)
                    .entity("unable to locate id:" + id)
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        
        //generate links for valid follow-on actions
        URI self = new ApplicationsState(uriInfo).setHRefs(app);
        return Response
                .ok(app, DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
                .contentLocation(self) //Content-Location header of representation
                .lastModified(app.getUpdated()) //Last-Modified header of the representation
                .build();
    }

    @Override
    public Response cancelApplication(long id, UriInfo uriInfo) {
        int status=0;
        if ((status=service.deleteApplication(id)) < 0) {
            return Response.status(Status.NOT_FOUND)
                    .entity("unable to locate application:" + id)
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } 
        else if (status > 0) {
            return Response.status(405)
                    .header("Allow", "GET, HEAD")
                    .entity("completed application cannot be deleted")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
            //application deleted
        return Response.noContent()
                    .build();
    }

    @Override
    public Response approveApplication(long id, UriInfo uriInfo) {
        int status=0;
        if ((status=service.approve(id)) == 0) {
            Application approvedApp = service.getApplication(id);
            //generate links for valid follow-on actions
            URI self = new ApplicationsState(uriInfo).setHRefs(approvedApp);
            
                //return the response
            return Response
                    .ok(approvedApp, DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
                    .contentLocation(self) //Content-Location header of representation
                    .lastModified(approvedApp.getUpdated()) //Last-Modified header of the representation
                    .build();
        }
        else if (status < 0) {
            return Response.status(Status.NOT_FOUND)
                    .entity("unable to locate application:" + id)
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        else {
            return Response.status(405)
                    .header("Allow", "GET, HEAD")
                    .entity("completed application cannot be approved")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    @Override
    public Response rejectApplication(long id, UriInfo uriInfo) {
        return null;
    }
    
    @Override
    public Response payApplication(long id, UriInfo uriInfo) {
        int status=0;
        if ((status=service.payment(id)) == 0) {
            Application paidApp = service.getApplication(id);
            //generate links for valid follow-on actions
            URI self = new ApplicationsState(uriInfo).setHRefs(paidApp);
                
                //return the response
            return Response
                    .ok(paidApp, DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
                    .contentLocation(self) //Content-Location header of representation
                    .lastModified(paidApp.getUpdated()) //Last-Modified header of the representation
                    .build();
        }
        else if (status < 0) {
            return Response.status(Status.NOT_FOUND)
                    .entity("unable to locate application:" + id)
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        else {
            return Response.status(405)
                    .header("Allow", "GET, HEAD")
                    .entity("completed application cannot be paid")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    @Override
    public Response refundApplicationPayment(long id, UriInfo uriInfo) {
        int status=0;
        if ((status=service.refund(id)) == 0) {
            Application approvedApp = service.getApplication(id);
            //generate links for valid follow-on actions
            URI self = new ApplicationsState(uriInfo).setHRefs(approvedApp);
            
                //return the response
            return Response
                    .ok(approvedApp, DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
                    .contentLocation(self) //Content-Location header of representation
                    .lastModified(approvedApp.getUpdated()) //Last-Modified header of the representation
                    .build();
        }
        else if (status < 0) {
            return Response.status(Status.NOT_FOUND)
                    .entity("unable to locate application:" + id)
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        else {
            return Response.status(405)
                    .header("Allow", "GET, HEAD")
                    .entity("completed application cannot be refunded")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }    
}
