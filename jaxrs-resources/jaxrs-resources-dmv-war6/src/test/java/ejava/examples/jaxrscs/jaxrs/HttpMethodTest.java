package ejava.examples.jaxrscs.jaxrs;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;



import javax.inject.Inject;
import javax.ws.rs.HttpMethod;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This class implements a local unit test demonstration of JAX-RS Methods.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ResourcesConfig.class})
public class HttpMethodTest {
	protected static final Logger log = LoggerFactory.getLogger(HttpMethodTest.class);
	protected static Server server;
	@Inject protected Environment env;
    @Inject protected URI httpMethodsURI; 
	@Inject protected HttpClient httpClient;
	
    @Before
    public void setUp() throws Exception {  
        startServer();
    }
    
    protected void startServer() throws Exception {
        if (httpMethodsURI.getPort()>=9092) {
            if (server == null) {
                String path=env.getProperty("servletContext", "/");
                server = new Server(9092);
                WebAppContext context = new WebAppContext();
                context.setResourceBase("src/test/resources/local-web");
                context.setContextPath(path);
                context.setParentLoaderPriority(true);
                server.setHandler(context);
            }
            server.start();
        }
    }
    
    @After
    public  void tearDown() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
        if (server != null) {
            server.destroy();
            server = null;
        }
    }
	

	protected String doCall(HttpUriRequest method) throws ClientProtocolException, IOException {
	    HttpResponse response = null;
	    try {
	        response = httpClient.execute(method);
	        log.info(String.format("%s %s => %d",
	                method.getMethod(), 
                    method.getURI(),
	                response.getStatusLine().getStatusCode()));
            //assertEquals("unexpected status code", 200, response.getStatusLine().getStatusCode());
	        if (response.getStatusLine().getStatusCode()!=200) {
	            return ""+response.getStatusLine().getStatusCode();
	        }
	        String reply = response.getEntity() == null ? "" :
	            EntityUtils.toString(response.getEntity());
	        response = null;
	        return reply;
	    } finally {
	        if (response != null) { //must consume body before reuse
	            EntityUtils.consume(response.getEntity());
	        }
	    }
	}

	protected String doCustom(String method) throws ClientProtocolException, IOException {
        HttpResponse response = null;
        try {
            HttpHost host = new HttpHost(httpMethodsURI.getHost(), httpMethodsURI.getPort());
            BasicHttpRequest foo = new BasicHttpRequest(method, httpMethodsURI.toString());
            HttpRequest request = new BasicHttpRequest(method, httpMethodsURI.toString());
            response = httpClient.execute(host, request);
            log.info(String.format("%s => %s",
                    request.getRequestLine(),
                    response.getStatusLine()));
            assertEquals("unexpected status code", 200, response.getStatusLine().getStatusCode());
            String reply = response.getEntity() == null ? "" :
                EntityUtils.toString(response.getEntity());
            response = null;
            return reply;
        } finally {
            if (response != null) { //must consume body before reuse
                EntityUtils.consume(response.getEntity());
            }
        }
    }
	
	@Test
	public void testHttpMethods() throws Exception {
	    log.info(doCall(new HttpGet(httpMethodsURI)));
        log.info(doCall(new HttpPut(httpMethodsURI)));
        log.info(doCall(new HttpPost(httpMethodsURI)));
        log.info(doCall(new HttpDelete(httpMethodsURI)));
        log.info(doCall(new HttpHead(httpMethodsURI)));
        log.info(doCall(new HttpOptions(httpMethodsURI)));
        log.info(doCustom("FOO"));
	}

	@Test
    public void testPaths() throws Exception {
        log.info(doCall(new HttpGet(httpMethodsURI + "/level2")));
        log.info(doCall(new HttpGet(httpMethodsURI + "/RG-3")));
        log.info(doCall(new HttpGet(httpMethodsURI + "/3-RG")));
    }

    @Test
    public void testExpressions1() throws Exception {
        log.info(doCall(new HttpGet(httpMethodsURI + "/re/hello")));
        log.info(doCall(new HttpGet(httpMethodsURI + "/re/123")));
    }

    @Test
    public void testExpressions2() throws Exception {
        log.info(doCall(new HttpGet(httpMethodsURI + "/anything/for/bar/baz")));
        log.info(doCall(new HttpGet(httpMethodsURI + "/anything")));
    }

    @Test
    public void testSpecial() throws Exception {
        String unencoded = "<jcs>";
        String encoded = URLEncoder.encode(unencoded,"UTF-8");
        log.info(doCall(new HttpGet(httpMethodsURI + String.format("/special/%s/readme.txt",encoded))));
        try {
            log.info(doCall(new HttpGet(httpMethodsURI + String.format("/special/%s/readme.txt",unencoded))));
        } catch (IllegalArgumentException ex) {
            log.debug("caught expected exception:" + ex);
        }
    }

    @Test
    public void testMatrix() throws Exception {
        log.info(doCall(new HttpGet(httpMethodsURI + "/matrix/RG;pick=2;pos=QB/3")));
        log.info(doCall(new HttpGet(httpMethodsURI + "/matrix/RG;pick=203;pos=OL/0")));
        log.info(doCall(new HttpGet(httpMethodsURI + "/matrix/AL/0")));
    }

    @Test
    public void testSubresource() throws Exception {
        log.info(doCall(new HttpPost(httpMethodsURI + "/subresource/resident")));
        log.info(doCall(new HttpPost(httpMethodsURI + "/subresource/driver")));
        log.info(doCall(new HttpGet(httpMethodsURI + "/subresource/resident/ssn-22")));
        log.info(doCall(new HttpGet(httpMethodsURI + "/subresource/driver/lic-33")));
    }
    
    @Test
    public void testParameterInjection() throws Exception {
        log.info(doCall(new HttpGet(httpMethodsURI + 
            "/injection/larry;mt2=constant/curley;mt1=first?qt1=where's&qt2=moe")));
    }
}
