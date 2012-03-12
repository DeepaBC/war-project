package ejava.examples.restintro.rest.resources;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.restintro.rest.dto.ContactInfo;
import ejava.examples.restintro.rest.dto.Resident;
import ejava.examples.restintro.rest.dto.Residents;
import ejava.examples.restintro.svc.DMVService;
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
    DMVService service;
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response createResident(
            @Context HttpServletRequest httpRequest,
            @FormParam("firstName") String firstName,
            @FormParam("lastName") String lastName,
            @FormParam("street") String street,
            @FormParam("city") String city,
            @FormParam("state") String state,
            @FormParam("zip") String zip) throws URISyntaxException {
        Resident resident = new Resident();
        resident.setFirstName(firstName);
        resident.setLastName(lastName);
        if (street!=null || city!=null || state!=null || zip!=null) {
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setName(ContactInfo.HOME);
            contactInfo.setStreet(street);
            contactInfo.setCity(city);
            contactInfo.setState(state);
            contactInfo.setZip(zip);        
            resident.getContactInfo().add(contactInfo);
        }
        Resident result = service.createResident(resident);
        if (result == null) {
            throw new BadRequestException("unable to create resident");
        }
        log.info("created {}", result);
        
        
        //now form a more proper http-response
        URI uri = new URI(String.format("/%s/%d",
                httpRequest.getRequestURI(),
                result.getId()));
        return Response
                .created(uri)   //201-Created and a Location header
                .entity(result) //marshals the representation in response
                .contentLocation(uri) //Content-Location header of representation
                .type(MediaType.APPLICATION_XML) //Content-Type header of representation
                .lastModified(new Date()) //Last-Modified header
                .expires(new Date(System.currentTimeMillis()+10000))
                .tag(JAXBHelper.getTag(result)) //ETag
                .build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getResidents(
            @Context HttpServletRequest httpRequest,
            @QueryParam("start") int start, 
            @QueryParam("count") int count) throws URISyntaxException {
        List<Resident> residents = service.getResidents(start, count);
        log.debug(String.format("getResidents(%d,%d)=%d",start, count, residents.size()));

        StringBuffer uri = httpRequest.getRequestURL();
        uri.append("?");
        uri.append(httpRequest.getQueryString());
        Residents result = new Residents(residents, start, count);
        return Response
                .ok()   //200-OK
                .entity(result) 
                .contentLocation(new URI(uri.toString()))
                .type(MediaType.APPLICATION_XML)
                .expires(new Date(System.currentTimeMillis()+10000))
                .tag(JAXBHelper.getTag(result))
                .build();
    }
    
    @HEAD
    public Response getResidentsHEAD(
            @Context HttpServletRequest httpRequest,
            @QueryParam("start") int start, 
            @QueryParam("count") int count) throws URISyntaxException {
        List<Resident> residents = service.getResidents(start, count);
        log.debug(String.format("HEAD getResidents(%d,%d)=%d",start, count, residents.size()));

        StringBuffer uri = httpRequest.getRequestURL();
        uri.append("?");
        uri.append(httpRequest.getQueryString());
        Residents result = new Residents(residents, start, count);
        return Response
                .ok()   //200-OK
                .expires(new Date(System.currentTimeMillis()+10000))
                .tag(JAXBHelper.getTag(result))
                .build();
    }    

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getResidentById(
            @Context HttpServletRequest httpRequest,
            @PathParam("id")long id) throws URISyntaxException {
        Resident resident = service.getResidentById(id);
        if (resident == null) {
            throw new NotFoundException(String.format("resident %d not found", id));
        }

        return Response
                .ok()   //200-OK
                .entity(resident) 
                .contentLocation(new URI(httpRequest.getRequestURI()))
                .type(MediaType.APPLICATION_XML) 
                .expires(new Date(System.currentTimeMillis()+10000))
                .tag(JAXBHelper.getTag(resident))
                .build();
    }
    
    @Path("{id}")
    @HEAD
    public Response getResidentByIdHEAD(
            @Context HttpServletRequest httpRequest,
            @PathParam("id")long id) throws URISyntaxException {
        Resident resident = service.getResidentById(id);
        if (resident == null) {
            throw new NotFoundException(String.format("resident %d not found", id));
        }

        return Response
                .ok()   //200-OK
                .expires(new Date(System.currentTimeMillis()+10000))
                .tag(JAXBHelper.getTag(resident))
                .build();
    }
    

    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateResident(
            @Context HttpServletRequest httpRequest,
            Resident resident) throws URISyntaxException {
        if (!service.updateResident(resident)) {
            throw new BadRequestException("unable to update resident");
        }
        return Response
                .noContent()   //204-No Content
                .build();
    }
    
    @Path("{id}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public int deleteResident(@PathParam("id")long id) {
        int result=service.deleteResident(id);
        return result;
    }
    
    @Path("/names")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getResidentNames(
            @Context HttpServletRequest httpRequest) throws URISyntaxException {
        String result=service.getResidentNames();
        return Response
                .ok()   //200-OK
                .entity(result) 
                .contentLocation(new URI(httpRequest.getRequestURI()))
                .type(MediaType.TEXT_PLAIN) 
                .expires(new Date(System.currentTimeMillis()+10000))
                .tag(JAXBHelper.getTag(result))
                .build();
    }
    
    @Path("/same")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response isSamePerson(
            @Context HttpServletRequest httpRequest,
            @QueryParam("p1") long p1,
            @QueryParam("p2") long p2) throws URISyntaxException {
        boolean result = service.isSamePerson(p1, p2);
        
        Residents taggedResidents = new Residents();
        taggedResidents.add(service.getResidentById(p1));
        taggedResidents.add(service.getResidentById(p2));
        StringBuffer uri = httpRequest.getRequestURL();
        uri.append("?");
        uri.append(httpRequest.getQueryString());
        return Response
                .ok()   //200-OK
                .entity(result) 
                .contentLocation(new URI(uri.toString()))
                .type(MediaType.TEXT_PLAIN) 
                .expires(new Date(System.currentTimeMillis()+10000))
                .tag(JAXBHelper.getTag(taggedResidents))
                .build();
    }
}
