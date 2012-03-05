package ejava.examples.restintro.rest.resources;

import java.util.List;

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
import org.jboss.resteasy.spi.NotFoundException;

import ejava.examples.restintro.rest.dto.ContactInfo;
import ejava.examples.restintro.rest.dto.Resident;
import ejava.examples.restintro.svc.DMVService;

@Path("residents")
public class ResidentsResource {
    @Inject
    DMVService service;
    
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Resident createResident(
            @QueryParam("firstName") String firstName,
            @QueryParam("lastName") String lastName,
            @QueryParam("street") String street,
            @QueryParam("city") String city,
            @QueryParam("state") String state,
            @QueryParam("zip") String zip) {
        Resident resident = new Resident();
        resident.setFirstName(firstName);
        resident.setLastName(lastName);
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setName(ContactInfo.HOME);
        contactInfo.setStreet(street);
        contactInfo.setCity(city);
        contactInfo.setState(state);
        contactInfo.setZip(zip);        
        Resident result = service.createResident(resident);
        if (result == null) {
            throw new BadRequestException("unable to create resident");
        }
        return result;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<Resident> getResidents(
            @QueryParam("start") int start, 
            @QueryParam("count") int count) {
        return service.getResidents(start, count);
    }
    
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Resident getResident(
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
    @Produces(MediaType.TEXT_XML)
    public int deleteResident(@PathParam("id")long id) {
        return service.deleteResident(id);
    }
    
    @Path("/names")
    @GET
    @Produces(MediaType.TEXT_XML)
    public String getResidentNames() {
        StringBuilder text = new StringBuilder();
        for (Resident resident : service.getResidents()) {
            text.append(String.format("%s, %s", 
                    resident.getLastName(), 
                    resident.getFirstName()));
        }
        return text.toString();
    }
    
    @Path("/same")
    @GET
    @Produces(MediaType.TEXT_XML)
    public boolean isSamePerson(
            @QueryParam("p1") long p1,
            @QueryParam("p2") long p2) {
        return service.isSamePerson(p1, p2);
    }
}
