package ejava.examples.jaxrscs.httpmethod.rs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;

import ejava.examples.jaxrscs.dmv.lic.dto.Photo;


/**
 * This class implements a test JAX-RS interface used to demonstrate HTTP
 * Methods.
 */
@Path("httpresponse")
public class HttpResponseDemoRS {
    @Context
    private UriInfo uriInfo;
    
    @GET   
    @Produces(MediaType.TEXT_PLAIN)
    public String method(@QueryParam("action") int response) {
        switch (response) {
            case 200:
                return "";
            case 204:
                return null;
            case 500:
                throw new RuntimeException();
        }
                
        return null;
    }
    
    @GET @Path("/custom")
    @Produces(MediaType.TEXT_PLAIN)
    public Response customResponse(@QueryParam("action") int response) {
        switch (response) {
        case 200:
            return Response.ok("").build();
        case 204:
            return Response.noContent().build();
        case 500:
            return Response.serverError().build();
        default:
            return Response.status(Response.Status.BAD_REQUEST).build();
        }            
    }

    @GET @Path("/custom2")
    @Produces(MediaType.TEXT_PLAIN)
    public Response customResponse2(@QueryParam("action") int response) {
        switch (response) {
        case 200:
            CacheControl cacheControl = new CacheControl();
            cacheControl.setMaxAge(60);
            Date lastModified = new Date();
            
            EntityTag eTag = new EntityTag("" + lastModified.hashCode());
            return Response.ok("")
                    .contentLocation(uriInfo.getAbsolutePath())
                    .lastModified(lastModified)
                    .tag(eTag)
                    .cacheControl(cacheControl)
                    .build();
        case 204:
            return Response.noContent().build();
        case 500:
            return Response.serverError().build();
        default:
            return Response.status(Response.Status.BAD_REQUEST).build();
        }            
    }
    
    @GET @Path("/photo/{id}")
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getPhoto(@PathParam("id") Integer id) {
        Photo photo = new Photo();
        photo.setId(id==null ? new Random().nextInt() : id);
        photo.setTimestamp(new Date());
        return Response.ok(photo).build();
    }

    @GET @Path("/photos")
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getPhotos() {
        List<Photo> photos = new ArrayList<Photo>();
        for (int i=0; i<3; i++) {
            photos.add((Photo)getPhoto(i).getEntity());
        }
        //return Response.ok(photos).build();
        return Response.ok(new GenericEntity<List<Photo>>(photos){}).build();
    }

    @GET @Path("/exceptions")
    @Produces(MediaType.TEXT_PLAIN)
    public String exceptions(@QueryParam("action") int response) {
        switch (response) {
            case 200:
                return "";
            case 204:
                return null;
            case 500:
                throw new WebApplicationException(
                        Response.serverError().build());
            default:
                throw new WebApplicationException(
                        Response.status(Response.Status.BAD_REQUEST).build());
        }
    }

    @GET @Path("/exception-mapper")
    @Produces(MediaType.TEXT_PLAIN)
    public String exceptionMapper(@QueryParam("action") int response) 
            throws SQLException {
        switch (response) {
            case 200:
                return "";
            case 204:
                return null;
            case 500:
                throw new SQLException("map this");
        }
        return null;
    }

    
}
