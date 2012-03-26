package ejava.examples.restintro.dmv.resources;

import java.net.URI;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.restintro.dmv.dto.ContactInfo;
import ejava.examples.restintro.dmv.dto.ContactType;
import ejava.examples.restintro.dmv.dto.Person;
import ejava.examples.restintro.dmv.dto.Persons;
import ejava.examples.restintro.dmv.svc.ResidentsService;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a JAX-RS interface over our service logic
 * and provides a HTTP responses that are more inline with REST constraints.
 * This will make our resources representations better understood by WWW
 * infrastructure.
 */
@Path("http/residents")
public class ResidentsHTTP {
    protected static Logger log = LoggerFactory.getLogger(ResidentsHTTP.class);
    @Inject
    ResidentsService service;
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response createResident(
            @Context HttpServletRequest httpRequest,
            @Context UriInfo uriInfo,
            @FormParam("firstName") String firstName,
            @FormParam("lastName") String lastName,
            @FormParam("street") String street,
            @FormParam("city") String city,
            @FormParam("state") String state,
            @FormParam("zip") String zip) throws URISyntaxException {
        log.debug("POST createResident({})\n{}",firstName, debugRequest(httpRequest));
        
        Person resident = new Person();
        resident.setFirstName(firstName);
        resident.setLastName(lastName);
        if (street!=null || city!=null || state!=null || zip!=null) {
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setType(ContactType.RESIDENCE);
            contactInfo.setStreet(street);
            contactInfo.setCity(city);
            contactInfo.setState(state);
            contactInfo.setZip(zip);        
            resident.getContactInfo().add(contactInfo);
        }
        Person result = service.createResident(resident);
        if (result == null) {
            throw new BadRequestException("unable to create resident");
        }
        log.info("created {}", result);
        
        
        //now form a more proper http-response
        EntityTag eTag=new EntityTag(JAXBHelper.getTag(result), false);       
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(10);
        cacheControl.setSMaxAge(10);

        URI uri=uriInfo.getAbsolutePathBuilder()
                .path(ResidentsHTTP.class, "getResidentById")
                .build(result.getId());
        return Response
                .created(uri)   //201-Created and a Location header of what was created
                .entity(result) //marshals the representation in response
                .contentLocation(uri) //Content-Location header of representation
                .type(MediaType.APPLICATION_XML) //Content-Type header of representation
                .lastModified(new Date()) //Last-Modified header of the representation
                .cacheControl(cacheControl) //cache controls for representation
                .tag(eTag)     //state identifier for respresentation
                .build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getResidents(
            @Context HttpServletRequest httpRequest,
            @Context Request request,
            @QueryParam("start") int start, 
            @QueryParam("count") int count) throws URISyntaxException {
        log.debug(String.format("GET getResidents(%d,%d)=\n%s",start, count, debugRequest(httpRequest)));
        List<Person> residents = service.getResidents(start, count);

        StringBuffer uri = httpRequest.getRequestURL();
        uri.append("?");
        uri.append(httpRequest.getQueryString());
        Persons result = new Persons(residents, start, count);

        EntityTag eTag=new EntityTag(JAXBHelper.getTag(result), false);       
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(10);
        cacheControl.setSMaxAge(30);
        
        ResponseBuilder builder = request.evaluatePreconditions(eTag);
        if (builder != null) {
            log.debug("not modified");
            builder.cacheControl(cacheControl);
            return builder.build();
        }
        else {
            log.debug("modified");
            return Response
                    .ok(result, MediaType.APPLICATION_XML)   //200-OK
                    .contentLocation(new URI(uri.toString()))
                    .cacheControl(cacheControl)
                    .tag(eTag)
                    .build();
        }
    }
    
    @HEAD
    public Response getResidentsHEAD(
            @Context HttpServletRequest httpRequest,
            @Context Request request,
            @QueryParam("start") int start, 
            @QueryParam("count") int count) throws URISyntaxException {
        List<Person> residents = service.getResidents(start, count);
        log.debug(String.format("HEAD getResidents(%d,%d)=%d",start, count, residents.size()));

        StringBuffer uri = httpRequest.getRequestURL();
        uri.append("?");
        uri.append(httpRequest.getQueryString());
        Persons result = new Persons(residents, start, count);

        EntityTag eTag=new EntityTag(JAXBHelper.getTag(result), false);
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(10);
        cacheControl.setSMaxAge(30);
        
        
        ResponseBuilder builder = request.evaluatePreconditions(eTag);
        if (builder != null) {
            log.debug("not modified");
            builder.cacheControl(cacheControl);
            return builder.build();
        }
        else {
            log.debug("modified");
            return Response
                    .ok()   //200-OK
                    .cacheControl(cacheControl)
                    .tag(eTag)
                    .build();
        }
    }    

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getResidentById(
            @Context HttpServletRequest httpRequest,
            @Context Request request,
            @PathParam("id")long id) throws URISyntaxException {
        log.debug("GET getResidentById({})\n{}",id, debugRequest(httpRequest));
        Person resident = service.getResidentById(id);
        if (resident == null) {
            throw new NotFoundException(String.format("resident %d not found", id));
        }

        EntityTag eTag=new EntityTag(JAXBHelper.getTag(resident), false);
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(10);
        cacheControl.setSMaxAge(30);
        

        ResponseBuilder builder = request.evaluatePreconditions(eTag);
        if (builder != null) {
            log.debug("not modified");
            builder.cacheControl(cacheControl);
            return builder.build();
        }
        else {
            log.debug("modified");
            return Response
                    .ok(resident, MediaType.APPLICATION_XML)   //200-OK
                    .contentLocation(new URI(httpRequest.getRequestURI()))
                    .cacheControl(cacheControl)
                    .tag(eTag)
                    .build();
        }    
    }
    
    @SuppressWarnings("unchecked")
    private StringBuilder debugRequest(HttpServletRequest httpRequest) {
        StringBuilder text = new StringBuilder();
        for (Enumeration<String> e=httpRequest.getHeaderNames();e.hasMoreElements();) {
            String name=e.nextElement();
            String value=httpRequest.getHeader(name);
            text.append(String.format("%s: %s\n", name, value));
        }        
        return text;
    }

    @Path("{id}")
    @HEAD
    public Response getResidentByIdHEAD(
            @Context HttpServletRequest httpRequest,
            @Context Request request,
            @PathParam("id")long id) throws URISyntaxException {
        
        log.debug("HEAD getResidentById({})\n{}",id, debugRequest(httpRequest));
        
        Person resident = service.getResidentById(id);
        if (resident == null) {
            throw new NotFoundException(String.format("resident %d not found", id));
        }

        EntityTag eTag=new EntityTag(JAXBHelper.getTag(resident), false);
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(10);
        cacheControl.setSMaxAge(30);        

        ResponseBuilder builder = request.evaluatePreconditions(eTag);
        if (builder != null) {
            log.debug("not modified");
            builder.cacheControl(cacheControl);
            return builder.build();
        }
        else {
            log.debug("modified");
            return Response
                    .ok()   //200-OK
                    .cacheControl(cacheControl)
                    .tag(eTag)
                    .build();
        }
    }
    

    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateResident(
            @Context HttpServletRequest httpRequest,
            @Context Request request,
            Person resident) throws URISyntaxException {
        log.debug("PUT updateResident({})\n{}",resident.getId(), debugRequest(httpRequest));

        Person existing = service.getResidentById(resident.getId());
        if (existing != null) {
            EntityTag eTag = new EntityTag(JAXBHelper.getTag(existing));
            ResponseBuilder builder = request.evaluatePreconditions(eTag);
            if (builder != null) {
                return builder.build(); //pre-conditions not met
            }
            else if (service.updateResident(resident)!=0) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("unable to update resident")
                            .type(MediaType.TEXT_PLAIN)
                            .build();
            }
            else {   //pre-conditions met and resident updated
                return Response
                        .noContent()   //204-No Content
                        .build();
            }
        }
        else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(String.format("resident %d unknown", resident.getId()))
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    @Path("{id}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteResident(
            @Context HttpServletRequest httpRequest,
            @Context Request request,
            @PathParam("id")long id) {
        log.debug("DELETE deleteResident({})\n{}",id, debugRequest(httpRequest));
        
        int result=service.deleteResident(id);
        if (result == 0) {
            return Response.notModified()
                    .entity(0)
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        else {
            return Response.ok(result)                
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /*
    @Path("/names")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getResidentNames(
            @Context HttpServletRequest httpRequest,
            @Context Request request) throws URISyntaxException {
        log.debug("GET getResidentNames()\n{}",debugRequest(httpRequest));

        String result=service.getResidentNames();
        
        EntityTag eTag = new EntityTag(MD5Helper.getTag(result), true);
        CacheControl cacheControl = new CacheControl();

        ResponseBuilder builder = request.evaluatePreconditions(eTag);
        if (builder != null) {
            log.debug("not modified");
            builder.cacheControl(cacheControl);
            return builder.build();
        }
        else {
            log.debug("modified");
            return Response
                    .ok(result, MediaType.TEXT_PLAIN)   //200-OK
                    .contentLocation(new URI(httpRequest.getRequestURI()))
                    .lastModified(new Date())
                    .cacheControl(cacheControl)
                    .tag(eTag)
                    .build();
        }
    }
    
    @Path("/same")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response isSamePerson(
            @Context HttpServletRequest httpRequest,
            @Context Request request,
            @QueryParam("p1") long p1,
            @QueryParam("p2") long p2) throws URISyntaxException {
        log.debug("GET isSamePerson()\n{}",debugRequest(httpRequest));


        StringBuffer uri = httpRequest.getRequestURL();
        uri.append("?");
        uri.append(httpRequest.getQueryString());
        Persons taggedResidents = new Persons();
        taggedResidents.add(service.getResidentById(p1));
        taggedResidents.add(service.getResidentById(p2));
        EntityTag eTag = new EntityTag(JAXBHelper.getTag(taggedResidents), false);
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(10);
        cacheControl.setSMaxAge(30);
        ResponseBuilder builder = request.evaluatePreconditions(eTag);
        if (builder != null) {
            return builder.cacheControl(cacheControl)
                          .build();
        }
        else {
            boolean result = service.isSamePerson(p1, p2);
            return Response
                    .ok()   //200-OK
                    .entity(result) 
                    .contentLocation(new URI(uri.toString()))
                    .type(MediaType.TEXT_PLAIN)
                    .lastModified(new Date())
                    .cacheControl(cacheControl)
                    .tag(eTag)
                    .build();
        }
    }
    */
}
