package ejava.examples.jaxrscs.httpmethod.rs;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
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
    public String m1() { 
        return String.format("%s => %s", httpRequest.getMethod(), "m1()"); 
    }
    @PUT    
    public String m2() { 
        return String.format("%s => %s", httpRequest.getMethod(), "m2()"); 
    }
    @POST    
    public String m3() { 
        return String.format("%s => %s", httpRequest.getMethod(), "m3()"); 
    }
    @DELETE    
    public String m4() { 
        return String.format("%s => %s", httpRequest.getMethod(), "m4()"); 
    }
    @HEAD    
    public String m5() { 
        return String.format("%s => %s", httpRequest.getMethod(), "m5()"); 
    }
    @OPTIONS    
    public String m6() { 
        return String.format("%s => %s", httpRequest.getMethod(), "m6()"); 
    }
    @FOO    
    public String m7() { 
        return String.format("%s => %s", httpRequest.getMethod(), "m7()"); 
    }
    
    @GET @Path("level2")
    public String m8() {
        return String.format("%s => %s", httpRequest.getRequestURI(), "m8()"); 
    }

    @GET @Path("{val1}-{val2}")
    public String m9(
            @PathParam("val1") String val1,
            @PathParam("val2") int val2) {
        return String.format("%s => m9(%s, %d)", httpRequest.getRequestURI(), 
                val1, val2);
    }

    @GET @Path("re/{val}")
    public String m10(
            @PathParam("val") String val) {
        return String.format("%s => m10(%s)", httpRequest.getRequestURI(), val);
    }

    @GET @Path("re/{val : \\d+}")
    public String m11(
            @PathParam("val") String val) {
        return String.format("%s => m11(%s)", httpRequest.getRequestURI(), val);
    }


    /**
     * This method will match any URI pattern below /httpmethods/anything
     */
    @GET @Path("anything/{val:.*+}")
    public String m12(
            @PathParam("val") String val) {
        return String.format("%s => m12(%s)", httpRequest.getRequestURI(), val);
    }

    @GET @Path("special/<jcs>/{doc:.*}")
    public String m13(
            @PathParam("doc") String doc) {
        return String.format("%s => m13(%s)", httpRequest.getRequestURI(), doc);
    }

    @GET @Path("matrix/{val1}/{val2}")
    public String m14(
            @PathParam("val1") String val1,
            @PathParam("val2") int val2,
            @MatrixParam("pick") int pick,
            @MatrixParam("pos") String pos) {
        return String.format("%s => m14(%s, %d) pick=%d, pos=%s", httpRequest.getRequestURI(), 
                val1, val2, pick, pos);
    }
    
    @Path("subresource/{type}")
    public Object m15(
            @PathParam("type") String type) throws Exception {
        return "resident".equalsIgnoreCase(type) ? new ResidentIDApp() : new DriverLicenseApp();
    }

    public class ResidentIDApp {
        @POST
        public String create(String val) {
            return String.format("%s => ResidentID(%s)", httpRequest.getRequestURI(), val);
        }

        @GET @Path("ssn-{id}")
        public String get(@PathParam("id") int id) {
            return String.format("%s => getResidentID(%s)", httpRequest.getRequestURI(), id);
        }    
    }
    public class DriverLicenseApp {
        @POST
        public String create(String val) {
            return String.format("%s => DriverLicense(%s)", httpRequest.getRequestURI(), val);
        }    
        @GET @Path("lic-{id}")
        public String get(@PathParam("id") int id) {
            return String.format("%s => getDriverLicense(%s)", httpRequest.getRequestURI(), id);
        }    
    }
    
    
    @GET @Path("injection/{pp1}/{pp2}")
    public String m16(
            @PathParam("pp1") String pp1,
            @PathParam("pp2") String pp2,
            @QueryParam("qt1") String qv1,
            @QueryParam("qt2") String qv2,
            @MatrixParam("mt1") String mv1,
            @MatrixParam("mt2") String mv2) {
        return String.format("%s => m16() path1=%s path2=%s, qv1=%s, qv2=%s, mv1=%s, mv2=%s", 
                httpRequest.getRequestURI(), 
                pp1, pp2, qv1, qv2, mv1, mv2);
    }
}
