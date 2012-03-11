package ejava.examples.restintro.rest.resources;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
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
import org.jboss.resteasy.spi.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.restintro.rest.dto.ContactInfo;
import ejava.examples.restintro.rest.dto.Resident;
import ejava.examples.restintro.rest.dto.Residents;
import ejava.examples.restintro.svc.DMVService;

/**
 * This class implements a simple JAX-RS interface over our service logic
 * so that it can be communicated with using REST-based protocols.
 */
@Path("jax-rs/residents")
public class ResidentsRS {
    protected static Logger log = LoggerFactory.getLogger(ResidentsRS.class);
    @Inject
    DMVService service;
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Resident createResident(
            @FormParam("firstName") String firstName,
            @FormParam("lastName") String lastName,
            @FormParam("street") String street,
            @FormParam("city") String city,
            @FormParam("state") String state,
            @FormParam("zip") String zip) {
        Resident resident = new Resident();
        resident.setFirstName(firstName);
        resident.setLastName(lastName);
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setName(ContactInfo.HOME);
        contactInfo.setStreet(street);
        contactInfo.setCity(city);
        contactInfo.setState(state);
        contactInfo.setZip(zip);        
        resident.getContactInfo().add(contactInfo);
        Resident result = service.createResident(resident);
        if (result == null) {
            throw new BadRequestException("unable to create resident");
        }
        log.info("created {}", result);
        return result;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<Resident> getResidents(
            @QueryParam("start") int start, 
            @QueryParam("count") int count) {
        List<Resident> residents = service.getResidents(start, count);
        log.debug(String.format("getResidents(%d,%d)=%d",start, count, residents.size()));
        return new Residents(residents, start, count);
    }
    
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Resident getResidentById(
            @PathParam("id")long id) {
        Resident resident = service.getResidentById(id);
        if (resident == null) {
            throw new NotFoundException(String.format("resident %d not found", id));
        }
        return resident;
    }
    
    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void updateResident(Resident resident) {
        if (!service.updateResident(resident)) {
            throw new BadRequestException("unable to update resident");
        }
    }
    
    @Path("{id}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public int deleteResident(@PathParam("id")long id) {
        return service.deleteResident(id);
    }
    
    @Path("/names")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getResidentNames() {
        return service.getResidentNames();
    }
    
    @Path("/same")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public boolean isSamePerson(
            @QueryParam("p1") long p1,
            @QueryParam("p2") long p2) {
        return service.isSamePerson(p1, p2);
    }
}
