package ejava.examples.jaxrscs.httpmethod.rs;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * This class implements a test JAX-RS interface used to demonstrate HTTP
 * Methods.
 */
@Path("httpresponse")
public class HttpResponseDemoRS {
    @Context
    private HttpServletRequest httpRequest;
    
    @GET   
    @Produces(MediaType.TEXT_PLAIN)
    public String method(@QueryParam("action") int response) {
        switch (response) {
            case 200:
                return "";
            case 204:
                return null;
            case 500:
                throw new RuntimeException();
        }
                
        return null;
    }
    
    @GET @Path("/custom")
    @Produces(MediaType.TEXT_PLAIN)
    public Response customResponse(@QueryParam("action") int response) {
        switch (response) {
        case 200:
            return Response.ok("").build();
        case 204:
            return Response.noContent().build();
        case 500:
            return Response.serverError().build();
        default:
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
            
    }
}
