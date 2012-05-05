package ejava.examples.jaxrscs.httpmethod.rs;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

/**
 * This class implements a test JAX-RS interface used to demonstrate HTTP
 * Methods.
 */
@Path("httpmethod")
public class HttpMethodDemoRS {
    @Context
    private HttpServletRequest httpRequest;

    @GET    
    public String m1() { return String.format("%s => %s", httpRequest.getMethod(), "m1()"); }
    @PUT    
    public String m2() { return String.format("%s => %s", httpRequest.getMethod(), "m2()"); }
    @POST    
    public String m3() { return String.format("%s => %s", httpRequest.getMethod(), "m3()"); }
    @DELETE    
    public String m4() { return String.format("%s => %s", httpRequest.getMethod(), "m4()"); }
    @HEAD    
    public String m5() { return String.format("%s => %s", httpRequest.getMethod(), "m5()"); }
    @OPTIONS    
    public String m6() { return String.format("%s => %s", httpRequest.getMethod(), "m6()"); }
    @FOO    
    public String m7() { return String.format("%s => %s", httpRequest.getMethod(), "m7()"); }
}
