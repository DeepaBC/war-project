package ejava.examples.jaxrsscale.caching;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.jaxrsscale.caching.dto.CacheCheck;

/**
 * This resource class is used to demonstrate specific caching constructs
 */
@Path("caching")
@Singleton
public class CachingRS {
    private static final Logger log = LoggerFactory.getLogger(CachingRS.class);
    private @Context UriInfo uriInfo;
    private @Context Request request;
    private @Context HttpHeaders headers;
    private @Context HttpServletRequest httpRequest;
    private static AtomicInteger value = newValue();
    private static Date valueDate;
    
    protected static AtomicInteger newValue() {
        log.info("server generating new token");
            //lastModified is only accurate to 1 second -- remove the millisecs
        valueDate = new Date(1000*(System.currentTimeMillis()/1000));
        return value==null ?
                new AtomicInteger(new Random().nextInt()) :
                new AtomicInteger(value.getAndIncrement());
    }

    @GET
    @Path("expires")
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getExpires(
            @QueryParam("delaySecs") int delaySecs) {
        log.info("***********************************************************");
        log.info("called: {} {}", request.getMethod(), uriInfo.getRequestUri());
        log.info("***********************************************************");
        newValue();
        CacheCheck check = new CacheCheck();
        check.setToken(value.get());
        check.setCalledDate(valueDate);
        
        Calendar expires = new GregorianCalendar();
        expires.add(Calendar.SECOND, delaySecs);
        Date expiresDate = expires.getTime();
        check.setExpiresDate(expiresDate);
        
        return Response.ok(check, MediaType.APPLICATION_XML)
                .expires(expiresDate)
                .build();
    }

    @GET
    @Path("max-age")
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getMaxAge(
            @QueryParam("delaySecs") int delaySecs) {
        log.info("***********************************************************");
        log.info("called: {} {}", request.getMethod(), uriInfo.getRequestUri());
        log.info("***********************************************************");
        newValue();
        CacheCheck check = new CacheCheck();
        check.setToken(value.get());
        check.setCalledDate(valueDate);
        Calendar expires = new GregorianCalendar();
        expires.add(Calendar.SECOND, delaySecs);
        check.setExpiresDate(expires.getTime());

            //fill in caching controls
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(delaySecs);
        
            //supply headers and response
        return Response.ok(check, MediaType.APPLICATION_XML)
                .cacheControl(cacheControl)
                .build();
    }

    @GET
    @Path("conditional")
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getConditional()  {
        log.info("called: {} {}", request.getMethod(), uriInfo.getRequestUri());
        
        for (String key : headers.getRequestHeaders().keySet()) {
            List<String> value = headers.getRequestHeader(key);
            log.info("jaxrs.header {} = {}", key, value);
        }
        
        for (Enumeration<String> e=httpRequest.getHeaderNames(); e.hasMoreElements();) {
            String key = e.nextElement();
            String value = httpRequest.getHeader(key);
            log.info("httpRequest.header {} = {}", key, value);
        }
            //determine if the time last changed later than header condition
        ResponseBuilder response = request.evaluatePreconditions(valueDate);
            //response will return with a non-null builder if not modified
        if (response != null) {
            log.info("value not changed on server");
            return response.build();
        }
            //otherwise -- the value has changed so do the work...
        log.info("***********************************************************");
        log.info("client needs current value");
        log.info("***********************************************************");
        
        CacheCheck check = new CacheCheck();
        check.setToken(value.get());
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);

        return Response.ok(check, MediaType.APPLICATION_XML)
                .lastModified(valueDate)
                .cacheControl(cacheControl)
                .build();
    }

    @GET
    @Path("conditional2")
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getConditional2()  {
        log.info("called: {} {}", request.getMethod(), uriInfo.getRequestUri());
        
            //determine if the time last changed later than header condition
        //EntityTag eTag = new EntityTag(""+valueDate.getTime());
        EntityTag eTag = new EntityTag(""+valueDate.getTime(), true);
        ResponseBuilder response = request.evaluatePreconditions(eTag);
            //response will return with a non-null builder if not modified
        if (response != null) {
            log.info("value not changed on server");
            return response.build();
        }
            //otherwise -- the value has changed so do the work...
        log.info("***********************************************************");
        log.info("client needs current value");
        log.info("***********************************************************");
        
        CacheCheck check = new CacheCheck();
        check.setToken(value.get());
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        
        return Response.ok(check, MediaType.APPLICATION_XML)
                .tag(eTag)
                .cacheControl(cacheControl)
                .build();
    }

    /**
     * This resource method provides Cache Controls telling clients how
     * long the values are good for and both a Last-Modified and ETag headers 
     * to aid in cache revalidation.
     * @return
     */
    @GET
    @Path("revalidation")
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getRevalidation(
            @QueryParam("delaySecs") int delaySecs)  {
        log.info("called: {} {}", request.getMethod(), uriInfo.getRequestUri());
        
            //determine if the time last changed later than header condition
        EntityTag eTag = new EntityTag(""+valueDate.getTime());
        ResponseBuilder response = request.evaluatePreconditions(valueDate, eTag);
            //response will return with a non-null builder if not modified
        if (response != null) {
            log.info("value not changed on server");
            return response
                    .lastModified(valueDate)
                    .build();
        }
            //otherwise -- the value has changed so do the work...
        log.info("***********************************************************");
        log.info("client needs current value");
        log.info("***********************************************************");
        
        CacheCheck check = new CacheCheck();
        check.setToken(value.get());
        check.setCalledDate(new Date());
        Calendar expires = new GregorianCalendar();
        expires.add(Calendar.SECOND, delaySecs);
        check.setExpiresDate(expires.getTime());
        
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(delaySecs);
        cacheControl.setSMaxAge(delaySecs);

        return Response.ok(check, MediaType.APPLICATION_XML)
                .tag(eTag)
                .lastModified(valueDate)
                .cacheControl(cacheControl)
                .build();
    }
}
