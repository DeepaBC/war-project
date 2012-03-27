package ejava.examples.restintro.dmv.resources;

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
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.NotFoundException;

import ejava.examples.restintro.dmv.dto.Application;
import ejava.examples.restintro.dmv.dto.Applications;
import ejava.examples.restintro.dmv.dto.ResidentIDApplication;
import ejava.examples.restintro.dmv.svc.ApplicationsService;
import ejava.examples.restintro.dmv.svc.BadArgument;

/**
 * This class implements CRUD-based resource access to Applications using
 * a JAX-RS API and no additional headers.
 */
@Path("jax-rs/applications")
public class ApplicationsRS {
    @Inject
    ApplicationsService service;

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Application createApplication(ResidentIDApplication app) {
        try {
            return service.createApplication(app);
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
    public Application getApplication(
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
    public void updateApplication(Application app) {
        if (service.updateApplication(app)!=0) {
            throw new BadRequestException("unable to update application");
        }
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
