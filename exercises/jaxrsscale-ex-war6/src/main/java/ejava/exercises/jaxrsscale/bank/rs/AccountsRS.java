package ejava.exercises.jaxrsscale.bank.rs;

import java.net.URI;

import java.util.Date;


import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Request;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.exercises.jaxrsscale.bank.dto.Account;
import ejava.exercises.jaxrsscale.bank.dto.Accounts;
import ejava.exercises.jaxrsscale.bank.svc.AccountsService;

/**
 * This class implements the JAX-RS interface for the Account resources.
 */
@Path("accounts")
public class AccountsRS {
    private static final Logger log = LoggerFactory.getLogger(AccountsRS.class);
    public static final int PAGE_SIZE=3;
    protected @Inject AccountsService service;
    protected @Context UriInfo uriInfo;
    protected @Context Request request;

    EntityTag getTag(Account account) {
        //TODO: 3 -- derive an eTag from the account state
        //return new EntityTag("" + account.getUpdated().getTime() + account.getBalance());
        return new EntityTag("");
    }
    Date getLastModified(Account account) {
        return new Date((account.getUpdated().getTime()/1000) * 1000);
    }
    
    @Path("")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response createAccount(Account account) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        Account created = service.createAccount(account);
        URI self = new AccountRefs(uriInfo).setHRefs(account);
        log.debug("created account:\n{}", account.toXML());
        return Response.created(self)
                .contentLocation(self)
                .lastModified(getLastModified(account))
                .tag(getTag(account))
                .entity(created)
                .type(MediaType.APPLICATION_XML)
                .build();
    }

    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateAccount(
            @PathParam("id") int id,
            Account account) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        if (service.updateAccount(id, account)==0) {
            account = service.getAccount(id);
            log.debug("updated account:{}", account.toXML());
            URI self = new AccountRefs(uriInfo).selfURI(id);

            return Response.noContent()
                    .location(self)
                    .lastModified(getLastModified(account))
                    .tag(getTag(account))
                    .build();
        }
        else  {
            return Response.status(Status.NOT_FOUND)
                    .entity(String.format("cannot find account %d", account.getId()))
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @Path("{id}")
    @DELETE
    public Response deleteAccount(@PathParam("id") int id) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        if (service.deleteAccount(id)==0) {
            return Response.noContent()
                    .build();
        }
        else  {
            return Response.status(Status.NOT_FOUND)
                    .entity(String.format("cannot find account %d", id))
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    @Path("deposits")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Formatted
    public Response deposit(
            @FormParam("id") int id, 
            @FormParam("amount") float amount) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("deposit id={}, amount={}", id, amount);
        if (service.deposit(id, amount)==0) {
            Account account = service.getAccount(id);
            URI self = new AccountRefs(uriInfo).setHRefs(account);
            log.debug("deposited to account:{}", account.toXML());
            return Response.ok(account, MediaType.APPLICATION_XML)
                    .location(self)
                    .contentLocation(self)
                    .lastModified(getLastModified(account))
                    .tag(getTag(account))
                    .build();
        }
        else  {
            return Response.status(Status.NOT_FOUND)
                    .entity(String.format("cannot find account %d", id))
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @Path("withdraws")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Formatted
    public Response withdraw(
            @FormParam("id") int id, 
            @FormParam("amount") float amount) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        
        Account account = service.getAccount(id);
        //TODO: 2 -- add check for entity changes prior to modify
        /*ResponseBuilder response = request.evaluatePreconditions(
                getLastModified(account),
                getTag(account));
        if (response != null) {
            log.debug("withdraw pre-condition failed");
            return response
                    .entity(account)
                    .type(MediaType.APPLICATION_XML)
                    .lastModified(getLastModified(account))
                    .tag(getTag(account))
                    .build();
        }
        else */if (service.withdraw(id, amount)==0) {
            log.debug("withdraw id={}, amount={}", id, amount);
            URI self = new AccountRefs(uriInfo).setHRefs(account);
            log.debug("tag={}, ${}", getTag(account), account.getBalance());
            return Response.ok(account, MediaType.APPLICATION_XML)
                    .location(self)
                    .contentLocation(self)
                    .lastModified(getLastModified(account))
                    .tag(getTag(account))
                    .build();
        }
        else  {
            return Response.status(Status.NOT_FOUND)
                    .entity(String.format("cannot find account %d", id))
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @Path("transfers")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Formatted
    public Response transfer(
            @FormParam("from") int fromId, 
            @FormParam("to") int toId, 
            @FormParam("amount") float amount) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("transfer from={}, to={}, amount=" + amount, fromId, toId);
        if (service.transfer(fromId, toId, amount)==0) {
            Accounts accounts = new Accounts();
            accounts.add(service.getAccount(fromId));
            accounts.add(service.getAccount(toId));
            URI self = new AccountRefs(uriInfo).setHRefs(accounts);
            log.debug("transfer {}", accounts.toXML());
            return Response.ok(accounts, MediaType.APPLICATION_XML)
                    .contentLocation(self)
                    .lastModified(accounts.get(0).getUpdated())
                    .tag(getTag(accounts.get(0)))
                    .build();
        }
        else  {
            return Response.status(Status.NOT_FOUND)
                    .entity(String.format("cannot find account either %d or %d", fromId, toId))
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @Path("")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getAccounts(
            @QueryParam("start") int start, 
            @QueryParam("count") @DefaultValue(""+PAGE_SIZE) int count) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        Accounts accounts = service.getAccounts(start, count);
        URI self = new AccountRefs(uriInfo).setHRefs(accounts);
        log.debug("returning accounts {}", accounts.toXML());
        return Response.ok(accounts, MediaType.APPLICATION_XML)
                .contentLocation(self)
                .build();
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getAccountById(@PathParam("id") int id) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        Account account = service.getAccount(id);
        if (account != null) {
            URI self = new AccountRefs(uriInfo).setHRefs(account);
            log.debug("returning account {}", account.toXML());
            return Response.ok(account, MediaType.APPLICATION_XML)
                    .lastModified(getLastModified(account))
                    .contentLocation(self)
                    .tag(getTag(account))
                    .build();
        }
        else  {
            return Response.status(Status.NOT_FOUND)
                    .entity(String.format("cannot find account %d", id))
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}