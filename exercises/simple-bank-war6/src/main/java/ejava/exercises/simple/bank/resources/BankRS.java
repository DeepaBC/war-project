package ejava.exercises.simple.bank.resources;

import java.net.URI;


import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;

import ejava.exercises.simple.bank.dto.Bank;
import ejava.exercises.simple.bank.svc.BankService;

/**
 * This class implements the JAX-RS interface for the Bank resource
 */
@Path("bank")
public class BankRS {
    protected @Inject BankService service;
    protected @Inject UriInfo uriInfo;


    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateBank(Bank bank) {
        if (service.updateBank(bank)==0) {
            return Response.noContent()
                    .build();
        }
        else  {
            return Response.status(Status.BAD_REQUEST)
                    .entity(String.format("cannot update bank"))
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getAccount(int id) {
        Bank bank = service.getBank();
        if (bank != null) {
            URI self = new BankState(uriInfo).setHRefs(bank);
            return Response.ok(bank, MediaType.APPLICATION_XML)
                    .lastModified(bank.getUpdated())
                    .contentLocation(self)
                    .build();
        }
        else  {
            return Response.serverError()
                    .entity(String.format("error getting bank"))
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}