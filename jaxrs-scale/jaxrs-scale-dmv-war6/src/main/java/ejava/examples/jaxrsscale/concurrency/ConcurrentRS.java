package ejava.examples.jaxrsscale.concurrency;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.jaxrsscale.concurrency.dto.ConcurrencyCheck;

/**
 * This class will provide a demonstration of concurrent client access to 
 * a shared resource.
 */
@Path("concurrency")
public class ConcurrentRS {
    private Logger log = LoggerFactory.getLogger(ConcurrentRS.class);
    private @Inject ConcurrentService service;
    private @Context UriInfo uriInfo;
    private @Context Request request;
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getResource() {
        log.info("called: {} {}", request.getMethod(), uriInfo.getRequestUri());
        ConcurrencyCheck check = service.get();
        EntityTag eTag = new EntityTag("" + check.hashCode());
        ResponseBuilder response = request.evaluatePreconditions(check.getModifiedDate(), eTag);
        if (response != null) {
            return response
                    .lastModified(check.getModifiedDate())
                    .tag(eTag)
                    .build();
        }
        
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        
        return Response.ok(check, MediaType.APPLICATION_XML)
                       .lastModified(check.getModifiedDate())
                       .tag(eTag)
                       .build();
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response putResource(ConcurrencyCheck update) {
        log.info("called: {} {}", request.getMethod(), uriInfo.getRequestUri());
        ConcurrencyCheck check = service.get();
        EntityTag eTag = new EntityTag("" + check.hashCode());
        ResponseBuilder response = request.evaluatePreconditions(check.getModifiedDate(), eTag);
        if (response != null) {
            return response
                    .lastModified(check.getModifiedDate())
                    .tag(eTag)
                    .entity(check)
                    .build();
        }
        
        service.set(update);
        eTag = new EntityTag("" + update.hashCode());
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        
        return Response.ok(update, MediaType.APPLICATION_XML)
                       .lastModified(update.getModifiedDate())
                       .tag(eTag)
                       .build();
    }
    
}
