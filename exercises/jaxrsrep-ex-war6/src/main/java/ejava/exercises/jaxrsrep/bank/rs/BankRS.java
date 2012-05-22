package ejava.exercises.jaxrsrep.bank.rs;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.exercises.jaxrsrep.bank.dto.Bank;
import ejava.exercises.jaxrsrep.bank.svc.BankService;

/**
 * This class implements the JAX-RS interface for the Bank resource
 */
@Path("bank")
public class BankRS {
    protected Logger log = LoggerFactory.getLogger(BankRS.class);
    protected @Inject BankService service;
    protected @Context UriInfo uriInfo;
    protected @Context Request request;

    @Path("")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getBank() {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        Bank bank = service.getBank();
        URI self = new BankRefs(uriInfo).setHRefs(bank);
        log.debug("returning bank:\n{}", bank.toXML());
        return Response.ok(bank, MediaType.APPLICATION_XML)
                .contentLocation(self)
                .lastModified(bank.getUpdated())
                .build();
    }

    @Path("")
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateBank(Bank bank) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
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
    
    @Path("")
    @DELETE
    public Response resetBank() {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        service.resetBank();
        return Response.noContent().build();
    }
}