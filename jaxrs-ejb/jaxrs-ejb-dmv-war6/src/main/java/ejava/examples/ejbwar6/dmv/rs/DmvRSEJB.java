package ejava.examples.ejbwar6.dmv.rs;

import java.net.URI;


import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbwar6.dmv.dto.DMV;
import ejava.examples.ejbwar6.dmv.dto.DmvRepresentation;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements the web interface for the main entry point to the
 * DMV
 */
@Stateless
@Path("dmv")
public class DmvRSEJB {
    private static final Logger log = LoggerFactory.getLogger(DmvRSEJB.class);
    private @Resource SessionContext ctx;
    
    @PostConstruct
    public void init() {
        log.info("*** DmvRSEJB ***");
        log.info("ctx={}", ctx);
    }

    @GET
    @Produces(DmvRepresentation.DMV_MEDIA_TYPE)
    @Formatted
    public Response getDMV(
            @Context UriInfo uriInfo,
            @Context HttpServletRequest httpRequest) {
        log.info("*************************");
        log.info("getDMV from {}:{}", httpRequest.getRemoteAddr(), httpRequest.getRemotePort());
        log.info("*************************");
        DMV dmv = new DMV(); //normally would go to backend to determine these services
        URI self = new DmvState(uriInfo).setHRefs(dmv);
        
            //generate a checksum of the XML response for the ETag
        EntityTag eTag = new EntityTag(JAXBHelper.getTag(dmv)); 
            //have clients cache the contents for up to 24 hours
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(24*60*60);
        cacheControl.setSMaxAge(24*60*60);

        return Response.ok(dmv, DmvRepresentation.DMV_MEDIA_TYPE)
                .contentLocation(self)
                .tag(eTag)
                .cacheControl(cacheControl)
                .build();
    }
}
