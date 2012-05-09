package ejava.examples.jaxrscs.httpmethod.rs;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
        return String.format("%s => m16() ppp1=%s pp2=%s, qv1=%s, qv2=%s, mv1=%s, mv2=%s", 
                httpRequest.getRequestURI(), 
                pp1, pp2, qv1, qv2, mv1, mv2);
    }

    @GET @Path("injection2/{pp1}")
    public String m17(
            @PathParam("pp1") int pp1) {
        return String.format("%s => m17() pp1=%d", 
                httpRequest.getRequestURI(), pp1);
    }
    
    @GET @Path("injection3/{pp1 : \\d+}")
    public String m18(
            @PathParam("pp1") int pp1) {
        return String.format("%s => m18() pp1=%d", 
                httpRequest.getRequestURI(), pp1);
    }

    @GET @Path("injection4/{pp1 : \\d+}")
    public String m19(
            @PathParam("pp1") PathSegment pp1) {
        String value = pp1.getPath();
        String units = pp1.getMatrixParameters().getFirst("units");
        return String.format("%s => m19() pp1=%s value=%s, units=%s", 
                httpRequest.getRequestURI(), pp1, value, units);
    }

    @GET @Path("injection5/{pp : .+}")
    public String m20(
            @PathParam("pp") List<PathSegment> pp) {
        return String.format("%s => m20() pp=%s", 
                httpRequest.getRequestURI(), pp);
    }

    @GET @Path("injection6/{pp : .+}")
    public String m21(
            @Context UriInfo info) {
        URI requestUri=info.getRequestUri();
        URI absPath=info.getAbsolutePath();
        URI baseUri=info.getBaseUri();
        String path=info.getPath();
        List<String> matchedUris=info.getMatchedURIs();
        List<Object> matchedResources=info.getMatchedResources();
        List<PathSegment> pathSegments=info.getPathSegments();
        MultivaluedMap<String, String> queryParams=info.getQueryParameters();
        return String.format("%s => m21() \n" +
        		"absPath=%s\n" +
        		"baseUri=%s\n" +
        		"path=%s\n" +
        		"matchedUris=%s\n" +
        		"matchedResources=%s\n" +
        		"pathSegments=%s\n" +
        		"queryParams=%s", 
                requestUri, 
                absPath, baseUri, path, matchedUris, 
                matchedResources, pathSegments, queryParams);
    }
    
    @POST @Path("form")
    //@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String m22(
            @FormParam("fp1") String fp1,
            @FormParam("fp2") int fp2) {
        return String.format("%s => m22() fp1=%s fp2=%d", 
                httpRequest.getRequestURI(), fp1, fp2);
    }

    @POST @Path("headers")
    public String m23(
            @FormParam("fp1") String fp1,
            @FormParam("fp2") int fp2,
            @HeaderParam("Content-Type") String contentType,
            @HeaderParam("Content-Length") int contentLength,
            @HeaderParam("Host") String host,
            @HeaderParam("Connection") String connection,
            @HeaderParam("User-Agent") String userAgent) {
        return String.format("%s => m23() fp1=%s fp2=%d\n" +
        		"contentType=%s\n" +
        		"contentLength=%d\n" +
        		"host=%s\n" +
        		"connection=%s\n" +
        		"userAgent=%s", 
                httpRequest.getRequestURI(), fp1, fp2,
                contentType, contentLength, host, connection, userAgent);
    }

    @POST @Path("headers2")
    public String m24(
            @FormParam("fp1") String fp1,
            @FormParam("fp2") int fp2,
            @Context HttpHeaders headers) {
        List<MediaType> acceptedTypes=headers.getAcceptableMediaTypes();
        Map<String, Cookie> cookies=headers.getCookies();
        MediaType mediaType=headers.getMediaType();
        MultivaluedMap<String, String> requestHeaders=headers.getRequestHeaders();
        return String.format("%s => m24() fp1=%s fp2=%d\n" +
                "acceptedTypes=%s\n" +
                "cookies=%s\n" +
                "mediaType=%s\n" +
                "header.keys=%s\n" +
                "header.values=%s",
                httpRequest.getRequestURI(), fp1, fp2,
                acceptedTypes, cookies, mediaType,
                Arrays.toString(requestHeaders.keySet().toArray()),
                requestHeaders.values());
    }
    
    @POST @Path("cookies")
    public Response m25a() {
        NewCookie cookie1 = new NewCookie("cp1", "hello", null,
                httpRequest.getServerName(), 1, "m25 cookie", 1000, false);
        NewCookie cookie2 = new NewCookie("cp2", "13");
        String response=String.format("%s => 25a() setting cookies cp1=%s, cp2=%s", 
                httpRequest.getRequestURI(), cookie1, cookie2);
        return Response.ok(response, MediaType.TEXT_PLAIN)
            .cookie(cookie1, cookie2)
            .build();
    }
    
    @GET @Path("cookies")
    public String m25b(
            @CookieParam("cp1") String cv1,
            @CookieParam("cp2") int cv2) {
        return String.format("%s => m25b() cv1=%s, cv2=%d", 
                httpRequest.getRequestURI(), cv1, cv2);
    }
    
    @GET @Path("cookies2")
    public String m25c(
            @CookieParam("cp1") Cookie cv1,
            @CookieParam("cp2") Cookie cv2) {
        String domain=cv1.getDomain();
        String name=cv1.getName();
        String path=cv1.getPath();
        int version=cv1.getVersion();
        return String.format("%s => m25c() cv1=%s, cv2=%s\n" +
        		"domain=%s\n" +
        		"name=%s\n" +
        		"path=%s\n" +
        		"version=%d", 
                httpRequest.getRequestURI(), cv1, cv2,
                domain, name, path, version);
    }
    
    @PUT @Path("collection")
    public String m26(@QueryParam("name") List<String> names) {
        return String.format("%s => m26() names=%s", 
                httpRequest.getRequestURI(), names);
    }

    @PUT @Path("collection2")
    public String m27(@QueryParam("name") List<Name> names) {
        return String.format("%s => m27() names=%s", 
                httpRequest.getRequestURI(), names);
    }
    
    
    @GET @Path("default")
    public String m28(@QueryParam("qp1") String qv1,
                      @QueryParam("qp2") @DefaultValue("hello") String qv2,
                      @QueryParam("qp3") int qv3,
                      @QueryParam("qp4") Integer qv4,
                      @QueryParam("qp5") @DefaultValue("100") int qv5) {
        return String.format("%s => m28() qv1=%s qv2=%s, qv3=%d, qv4=%s, qv5=%d", 
                httpRequest.getRequestURI(), qv1, qv2, qv3, qv4, qv5);
    }
}
