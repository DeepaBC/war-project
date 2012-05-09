package ejava.exercises.jaxrscs.bank.rs;

//import javax.inject.Inject;
import java.util.Random;

//import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
//import javax.ws.rs.core.Context;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
//import javax.ws.rs.core.UriInfo;
//import javax.ws.rs.core.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.exercises.jaxrscs.bank.dto.Account;

/**
 * This class implements the JAX-RS interface for the Account resources.
 */
//TODO: 1b -- all resource classes must be annotated with a @Path
//@Path("accounts")
public class AccountsRS {
    private static final Logger log = LoggerFactory.getLogger(AccountsRS.class);

    //TODO: 4b inject helper objects to determine method context
    //protected @Context UriInfo uriInfo;

    
    @PUT //TODO: 2b -- Java methods may only have a single HTTP method assigned
    @Produces(MediaType.TEXT_PLAIN)
    public String getHello() {
        return "hello";
    }
    
    @GET 
    @Path("/greeting/{id}")  //TODO: 3b -- wildcard path parameter must be int
    @Produces(MediaType.TEXT_PLAIN)
    public String getGreeting(@PathParam("id") int id) {
        switch (id) {
        case 0:
            return "good morning!";
        case 1:
            return "good afternoon!";
        case 2:
            return "good evening!";
        default:
            return "huh?";
        }
    }
    
    @GET
    @Path("inject")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMyPath() {
        //TODO: 4b -- use injected helpers to get context information 
        //inject the helper at class scope
        return "/accounts/something-like-this"; //uriInfo.getPath();
    }
    
    @GET
    @Path("query")
    @Produces(MediaType.TEXT_PLAIN)             //TODO: use a query param for q1
    public String whatsMyLine(String question1, 
                              @QueryParam("q2x") String question2,
                              @Context UriInfo uriInfo) {
        log.debug("received: {}",uriInfo.getRequestUri().toString());
        log.debug("question1={}, question2={}", question1, question2);
        if (question1 == null || question2 == null) {
            return "ask next question?";
        }
        else if (question1.equals("do you drive a truck?") && question2.equals("18 wheels?")) {
            return "no";
        }
        else if (question1.equals("do you develop software?") && question2.equals("JavaEE?")) {
            return "yes";
        }
        else {
            return "huh?";
        }
    }
    
    @POST
    @Produces(MediaType.APPLICATION_XML)                  //TODO: use form parameters
    public Object createAccount(@PathParam("ownerName") String ownerName, 
                                @PathParam("amount") float amount) {
        Account account = new Account();
        account.setId(new Random().nextInt(1000));
        account.setOwnerName(ownerName);
        account.setBalance(amount);
        boolean simple=true; //TODO: marshal an entity and provide a 201 response code
        return simple ? account.toXML() :
            Response.status(Response.Status.CREATED).entity(account.toXML()).build();
    }
    
}