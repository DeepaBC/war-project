package ejava.examples.jaxrscs.jaxrs;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;



import javax.inject.Inject;

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
        log.info(doCall(new HttpGet(httpMethodsURI)));
	}
}
