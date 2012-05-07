package ejava.examples.jaxrscs.jaxrs;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;



import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicNameValuePair;
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
public class HttpResponseTest {
	protected static final Logger log = LoggerFactory.getLogger(HttpResponseTest.class);
	protected static Server server;
	@Inject protected Environment env;
    @Inject protected URI httpResponsesURI; 
	@Inject protected HttpClient httpClient;
	
    @Before
    public void setUp() throws Exception {  
        startServer();
    }
    
    protected void startServer() throws Exception {
        if (httpResponsesURI.getPort()>=9092) {
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
	

	protected int doCall(HttpUriRequest method) throws ClientProtocolException, IOException {
	    HttpResponse response = null;
	    try {
	        response = httpClient.execute(method);
	        log.info(String.format("%s %s => %d",
	                method.getMethod(), 
                    method.getURI(),
	                response.getStatusLine().getStatusCode()));
	        return response.getStatusLine().getStatusCode();
	    } finally {
	        if (response != null) { //must consume body before reuse
	            EntityUtils.consume(response.getEntity());
	        }
	    }
	}
	
	protected void doTestResponseCode(URI uri, int expected) throws Exception {
        assertEquals("unexpected response status code", 
                expected,
                doCall(new HttpGet(uri + "?action=" + expected)));
	}

	@Test
	public void testDefaultResponseCodes() throws Exception {
	    doTestResponseCode(httpResponsesURI, 200);
        doTestResponseCode(httpResponsesURI, 204);
        doTestResponseCode(httpResponsesURI, 500);
	}

	/**
	 * This method calls PUT for which there is a matching URI but no matching
	 * PUT method assigned to that URI.
	 * @throws Exception
	 */
    @Test
    public void test405ResponseCode() throws Exception {
        assertEquals("unexpected response status code", 
                405,
                doCall(new HttpPut(httpResponsesURI)));
    }

    /**
     * This method will attempt to call a valid GET but ask for a content
     * type returned that is not supported.
     * @throws Exception
     */
    @Test
    public void test406ResponseCode() throws Exception {
	    HttpGet get = new HttpGet(httpResponsesURI);
	    get.setHeader("Accept", MediaType.APPLICATION_XML);
        assertEquals("unexpected response status code", 
                406,
                doCall(get));
    }

    /**
     * This method invokes the custom response method where direct control
     * of the HTTP response can be crafted.
     * @throws Exception
     */
    @Test
    public void testCustomResponse() throws Exception {
        doTestResponseCode(new URI(httpResponsesURI + "/custom"), 200);
        doTestResponseCode(new URI(httpResponsesURI + "/custom"), 204);
        doTestResponseCode(new URI(httpResponsesURI + "/custom"), 400);
        doTestResponseCode(new URI(httpResponsesURI + "/custom"), 500);
    }

    /**
     * This version provides a bit richer example of a response than the 
     * basic one above.
     * @throws Exception
     */
    @Test
    public void testCustomResponse2() throws Exception {
        doTestResponseCode(new URI(httpResponsesURI + "/custom2"), 200);
    }
    
    /**
     * This test verifies the provider can marshall a JAXB object wrapped
     * in a generic collection.
     * @throws Exception
     */
    @Test 
    public void testGenericEntity() throws Exception {
        doTestResponseCode(new URI(httpResponsesURI + "/photo/3"), 200);
        doTestResponseCode(new URI(httpResponsesURI + "/photos"), 200);
    }

    /**
     * This test verifies that we can return a custom response using a 
     * WebApplicationException.
     * @throws Exception
     */
    @Test
    public void testExceptions() throws Exception {
        doTestResponseCode(new URI(httpResponsesURI + "/exceptions"), 400);
        doTestResponseCode(new URI(httpResponsesURI + "/exceptions"), 500);
    }

    
    @Test
    public void testExceptionMapper() throws Exception {
        doTestResponseCode(new URI(httpResponsesURI + "/exception-mapper"), 500);
    }
}
