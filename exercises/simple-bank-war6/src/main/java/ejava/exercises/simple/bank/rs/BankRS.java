package ejava.exercises.simple.bank.rs;

import java.net.URI;


import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.exercises.simple.bank.dto.Bank;
import ejava.exercises.simple.bank.svc.BankService;

/**
 * This class implements the JAX-RS interface for the Bank resource
 */
@Path("bank")
public class BankRS {
    protected Logger log = LoggerFactory.getLogger(BankRS.class);
    protected @Inject BankService service;
    protected @Context UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getBank() {
        Bank bank = service.getBank();
        URI self = new BankRefs(uriInfo).setHRefs(bank);
        log.debug("returning bank:\n{}", bank.toXML());
        return Response.ok(bank, MediaType.APPLICATION_XML)
                .contentLocation(self)
                .lastModified(bank.getUpdated())
                .build();
    }

    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateBank(Bank bank) {
        if (service.updateBank(bank)==0) {
            log.debug("updated bank:\n{}", bank.toXML());
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
            URI self = new BankRefs(uriInfo).setHRefs(bank);
            return Response.ok(bank, MediaType.APPLICATION_XML)
                    .contentLocation(self)
                    .lastModified(bank.getUpdated())
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