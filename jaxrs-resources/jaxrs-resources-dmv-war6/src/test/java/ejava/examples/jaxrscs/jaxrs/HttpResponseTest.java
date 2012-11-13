package ejava.examples.jaxrscs.jaxrs;

import static org.junit.Assert.*;



import java.io.IOException;
import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.common.test.ServerConfig;
/**
 * This class implements a local unit test demonstration of JAX-RS Methods.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ResourcesTestConfig.class, ServerConfig.class})
public class HttpResponseTest {
    protected static final Logger log = LoggerFactory.getLogger(HttpResponseTest.class);
    @Inject protected Environment env;
    @Inject protected URI httpResponsesURI; 
    @Inject protected HttpClient httpClient;
	
    @Before
    public void setUp() throws Exception {  
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
