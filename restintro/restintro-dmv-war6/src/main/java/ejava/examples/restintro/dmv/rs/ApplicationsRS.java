package ejava.examples.restintro.dmv.rs;

import java.io.IOException;



import java.net.URI;
import java.util.Date;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.restintro.dmv.lic.dto.Application;
import ejava.examples.restintro.dmv.lic.dto.Applications;
import ejava.examples.restintro.dmv.lic.dto.DrvLicRepresentation;
import ejava.examples.restintro.dmv.lic.dto.ResidentIDApplication;
import ejava.examples.restintro.dmv.svc.ApplicationsService;
import ejava.examples.restintro.dmv.svc.BadArgument;
import ejava.util.rest.Link;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements CRUD-based resource access to Applications using
 * a JAX-RS API and no additional headers.
 */
@Path("applications")
public class ApplicationsRS {
    private static final Logger log = LoggerFactory.getLogger(ApplicationsRS.class);
    @Inject
    private ApplicationsService service;
    
    @Context
    private UriInfo uriInfo;
    
    @Context 
    private Request request;

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response createApplication(ResidentIDApplication app) {
        log.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
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
    
    /**
     * This method implements an initial version of the hypermedia protocol
     * using flat XML reference links. Note the version# in the produces.
     */
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces("application/dmvlic.ejava.0+xml")
    @Formatted
    public Response createApplicationHM(ResidentIDApplication app) {
        log.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
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
    
    /**
     * This method represents another step closer to the targeted hypermedia
     * protocol. It generates complex link structures that can express more 
     * metadata about the transition.
     * @param app
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces("application/dmvlic.ejava.2+xml")
    @Formatted
    public Response createApplicationHM2(ResidentIDApplication app) {
        log.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
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



    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getApplicationById(
            @PathParam("id") long id) {
        log.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
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

    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Formatted
    public Response updateApplication(String appString) {
        log.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
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

    @Path("{id}")
    @DELETE
    public Response deleteApplication(
            @PathParam("id") long id) {
        log.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
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

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getApplications(
            @QueryParam("active") Boolean active, 
            @QueryParam("start") int start, 
            @QueryParam("count") int count) {
        log.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
        
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
    
    @DELETE
    public void purgeApplications() {
        log.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
        log.info("purging applications");
        service.purgeApplications();
    }
    
    
    @POST
    @Consumes(DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
    @Produces(DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
    @Formatted
    public Response createApplicationRep(ResidentIDApplication app) {
        log.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
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

    @Path("{id}")
    @GET
    @Produces(DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
    @Formatted
    public Response getApplication(
            @PathParam("id") long id) {
        log.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
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

    @Path("{id}/cancel")
    @DELETE
    public Response cancelApplication(
            @PathParam("id")long id) {
        log.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
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

    @Path("{id}/approve")
    @PUT
    @Produces(DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
    @Formatted
    public Response approveApplication(
            @PathParam("id")long id) {
        log.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
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
    
    @Path("{id}/reject")
    @PUT
    @Produces(DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
    @Formatted
    public Response rejectApplication(
            @PathParam("id")long id) {
        log.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
        return null;
    }
    
    @Path("{id}/payment")
    @PUT
    @Produces(DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
    @Formatted
    public Response payApplication(
            @PathParam("id")long id) {
        log.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
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
    
    @Path("{id}/refund")
    @PUT
    @Produces(DrvLicRepresentation.DRVLIC_MEDIA_TYPE)
    @Formatted
    public Response refundApplicationPayment(
            @PathParam("id")long id) {
        log.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
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
