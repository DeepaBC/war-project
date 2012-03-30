package ejava.examples.restintro.dmv.resources;

import java.io.IOException;

import java.net.URI;

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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.NotFoundException;

import ejava.examples.restintro.dmv.dto.Application;
import ejava.examples.restintro.dmv.dto.Applications;
import ejava.examples.restintro.dmv.dto.ResidentIDApplication;
import ejava.examples.restintro.dmv.svc.ApplicationsService;
import ejava.examples.restintro.dmv.svc.BadArgument;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements CRUD-based resource access to Applications using
 * a JAX-RS API and no additional headers.
 */
@Path("jax-rs/applications")
public class ApplicationsRS {
    @Inject
    private ApplicationsService service;
    
    @Context
    private UriInfo uriInfo;

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response createApplication(ResidentIDApplication app) {
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
            throw new BadRequestException("client error creating application");
        }
        catch (Exception ex) {
            throw new InternalServerErrorException("server error creating application");
        }
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Application getApplicationById(
            @PathParam("id") long id) {
        Application app = service.getApplication(id);
        if (app == null) {
            throw new NotFoundException("unable to locate id:" + id);
        }
        return app;
    }

    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateApplication(String appString) {
        //marshal to a string and demarshal locally so we have more control over transform 
        try {
            Application app = JAXBHelper.unmarshall(appString, Application.class, null, 
                    Application.class,
                    ResidentIDApplication.class);
            if (service.updateApplication(app)!=0) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("unable to update application")
                        .type(MediaType.TEXT_PLAIN)
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
    public void deleteApplication(
            @PathParam("id") long id) {
        if (service.getApplication(id)==null) {
            throw new NotFoundException("application not found:" + id);
        }
        else if (service.deleteApplication(id)!=0) {
            throw new BadRequestException("unable to update application");
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Applications getApplications(
            @QueryParam("active") Boolean active, 
            @QueryParam("start") int start, 
            @QueryParam("count") int count) {
        return service.getApplications(active, start, count);
    }
}
