package ejava.exercises.simple.bank.resources;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;

import ejava.exercises.simple.bank.dto.Account;
import ejava.exercises.simple.bank.dto.Accounts;
import ejava.exercises.simple.bank.svc.AccountsService;

/**
 * This class implements the JAX-RS interface for the Account resources.
 */
@Path("accounts")
public class AccountsRS {
    protected @Inject AccountsService service;
    protected @Inject UriInfo uriInfo;

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response createAccount(Account account) {
        Account created = service.createAccount(account);
        URI self = new AccountsState(uriInfo).setHRefs(account);
        return Response.created(self)
                .contentLocation(self)
                .lastModified(account.getUpdated())
                .entity(created)
                .type(MediaType.APPLICATION_XML)
                .build();
    }

    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateAccount(Account account) {
        if (service.updateAccount(account)==0) {
            return Response.noContent()
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
    public Response deleteAccount(int id) {
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

    @Path("{id}/deposits")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response deposit(int id, float amount) {
        if (service.deposit(id, amount)==0) {
            Account account = service.getAccount(id);
            URI self = new AccountsState(uriInfo).setHRefs(account);
            return Response.ok(account, MediaType.APPLICATION_XML)
                    .lastModified(account.getUpdated())
                    .contentLocation(self)
                    .build();
        }
        else  {
            return Response.status(Status.NOT_FOUND)
                    .entity(String.format("cannot find account %d", id))
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @Path("{id}/withdraws")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response withdraw(int id, float amount) {
        if (service.withdraw(id, amount)==0) {
            Account account = service.getAccount(id);
            URI self = new AccountsState(uriInfo).setHRefs(account);
            return Response.ok(account, MediaType.APPLICATION_XML)
                    .lastModified(account.getUpdated())
                    .contentLocation(self)
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
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response transfer(int fromId, int toId, float amount) {
        if (service.transfer(fromId, toId, amount)==0) {
            Account fromAccount = service.getAccount(fromId);
            Account toAccount = service.getAccount(toId);
            Accounts accounts = new Accounts();
            accounts.add(service.getAccount(fromId));
            accounts.add(service.getAccount(toId));
            new AccountsState(uriInfo).setHRefs(fromAccount);
            new AccountsState(uriInfo).setHRefs(toAccount);
            return Response.ok(accounts, MediaType.APPLICATION_XML)
                    .lastModified(fromAccount.getUpdated())
                    .build();
        }
        else  {
            return Response.status(Status.NOT_FOUND)
                    .entity(String.format("cannot find account either %d or %d", fromId, toId))
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getAccounts(int start, int count) {
        Accounts accounts = service.getAccounts(start, count);
        return Response.ok(accounts, MediaType.APPLICATION_XML)
                .build();
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Formatted
    public Response getAccount(int id) {
        Account account = service.getAccount(id);
        if (account != null) {
            URI self = new AccountsState(uriInfo).setHRefs(account);
            return Response.ok(account, MediaType.APPLICATION_XML)
                    .lastModified(account.getUpdated())
                    .contentLocation(self)
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