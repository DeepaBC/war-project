package ejava.examples.jaxrsscale.jaxrs;

import static org.junit.Assert.*;


import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.jaxrsscale.caching.dto.CacheCheck;
import ejava.examples.jaxrsscale.dmv.lic.dto.Person;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a local unit test demonstration of JAX-RS Methods.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ScaleTestConfig.class})
public class CachingTest {
	protected static final Logger log = LoggerFactory.getLogger(CachingTest.class);
	protected static Server server;
	@Inject protected Environment env;
    @Inject protected URI appURI; 
    @Inject protected URI cachingURI; 
	@Inject protected HttpClient httpClient;
	
    @Before
    public void setUp() throws Exception {  
        startServer();
    }
    
    protected void startServer() throws Exception {
        if (appURI.getPort()>=9092) {
            if (server == null) {
                String path=env.getProperty("servletContext", "/");
                server = new Server(9092);
                WebAppContext context = new WebAppContext();
                context.setResourceBase("src/test/resources/local-web");
                context.setContextPath(path);
                context.setParentLoaderPriority(true);
                server.setHandler(context);
                server.start();
            }
        }
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception {
        if (server != null) {
            server.stop();
            server.destroy();
            server = null;
        }
    }
    
    protected <T> T getResponse(Class<T> type, HttpGet get) throws Exception {
        log.info("calling: {} @ {}", get.getRequestLine(), new Date());
        HttpResponse response = httpClient.execute(get);
        try {
            assertEquals("unexpected status", Response.Status.OK.getStatusCode(), 
                    response.getStatusLine().getStatusCode());
            T object = JAXBHelper.unmarshall(
                    response.getEntity().getContent(), type, null);
            log.info("received: {}", object);
            return object;
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }

    /**
     * This method provides a test of the expires header.
     * @throws Exception
     */
    @Test
    public void testExpires() throws Exception {
        log.info("*** testExpires ***");
        int timeout=3;
        HttpGet get = new HttpGet(new URI(String.format(cachingURI + "/expires?delaySecs=%d",timeout))); 
        doTestCache(get, true); 
    }
    
    /**
     * This method provides a test of the Cache Control header max-age property.
     * @throws Exception
     */
    @Test
    public void testMaxAge() throws Exception {
        log.info("*** testMaxAge ***");
        int timeout=3;
        HttpGet get = new HttpGet(new URI(String.format(cachingURI + "/max-age?delaySecs=%d",timeout))); 
        doTestCache(get, true); 
    }

    /**
     * This method provides a test of whether a client can by-pass the cache 
     * based on provided Cache-Control headers.
     * @throws Exception
     */
    @Test
    public void testNoCache() throws Exception {
        log.info("*** testNoCache ***");
        int timeout=3;
        HttpGet get = new HttpGet(new URI(String.format(cachingURI + "/max-age?delaySecs=%d",timeout)));
        get.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        doTestCache(get, false); 
    }

    public void doTestCache(HttpGet get, boolean usesCache) throws Exception {
            //create a request and register the timeout
        get.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
        
            //get an initial token back
        CacheCheck start = getResponse(CacheCheck.class, get);
        int initialToken = start.getToken();

            //continue to call the method until timeout expires
        while (System.currentTimeMillis() < start.getExpiresDate().getTime()) {
            CacheCheck check = getResponse(CacheCheck.class, get);
                //we expect to get back the same token from the cache each time
                //expires value only granual to second -- not millisec
            if (System.currentTimeMillis() < start.getExpiresDate().getTime()-1000) {
                boolean equal = initialToken == check.getToken();
                if (usesCache) {
                    assertTrue("token not cached", equal);
                }
                else {
                    assertFalse("token cached", equal);
                }
            }
            Thread.sleep(1000);
        }
            //make sure the cache is beyond the timeout
        while(System.currentTimeMillis() <= start.getExpiresDate().getTime()+1000) {
            Thread.sleep(1000); //wait for expiration
        }
        
            //check that we get a new token value after cache expires
        assertFalse("unexpected expired token", 
                initialToken==getResponse(CacheCheck.class, get).getToken());
    }
    
    /**
     * This method provides a test of the If-Modified-Since header processing.
     * @throws Exception
     */
    @Test
    public void testConditionalDate() throws Exception {
        log.info("*** testConditionalDate ***");
        
        HttpGet get = new HttpGet(new URI(String.format(cachingURI + "/conditional")));
        get.setHeader("Accept", MediaType.APPLICATION_XML);
        
            //we calling for first time -- should get a 200 with a response object
        Header lastModifiedHeader = null;
        HttpResponse response = httpClient.execute(get);
        try {
            assertEquals("unexpected status", Response.Status.OK.getStatusCode(), 
                    response.getStatusLine().getStatusCode());
            JAXBHelper.unmarshall(response.getEntity().getContent(), CacheCheck.class, null);
            lastModifiedHeader = response.getFirstHeader(HttpHeaders.LAST_MODIFIED);
            assertNotNull("lastModified header not supplied", lastModifiedHeader);
            log.info("lastModified={}", lastModifiedHeader.getValue());
        } finally {
            EntityUtils.consume(response.getEntity());
        }
        
            //create a new request using the last modified as a basis
        get.setHeader(HttpHeaders.IF_MODIFIED_SINCE, lastModifiedHeader.getValue());
        response = httpClient.execute(get);
        try {
            assertEquals("unexpected status", Response.Status.NOT_MODIFIED.getStatusCode(), 
                    response.getStatusLine().getStatusCode());
            log.info("object not modified, nothing returned to client");
        } finally {
            EntityUtils.consume(response.getEntity());
        }        
    }

    /**
     * This method provides a demonstration and test for the use of the ETag
     * header.
     * @throws Exception
     */
    @Test
    public void testConditionalETag() throws Exception {
        log.info("*** testConditionalETag ***");
        
        HttpGet get = new HttpGet(new URI(String.format(cachingURI + "/conditional2")));
        get.setHeader("Accept", MediaType.APPLICATION_XML);
        
            //we calling for first time -- should get a 200 with a response object
        Header eTagHeader = null;
        HttpResponse response = httpClient.execute(get);
        try {
            assertEquals("unexpected status", Response.Status.OK.getStatusCode(), 
                    response.getStatusLine().getStatusCode());
            JAXBHelper.unmarshall(response.getEntity().getContent(), CacheCheck.class, null);
            eTagHeader = response.getFirstHeader(HttpHeaders.ETAG);
            assertNotNull("eTag header not supplied", eTagHeader);
            log.info("eTag={}", eTagHeader.getValue());
        } finally {
            EntityUtils.consume(response.getEntity());
        }
        
            //create a new request using the last modified as a basis
        get.setHeader(HttpHeaders.IF_NONE_MATCH, eTagHeader.getValue());
        response = httpClient.execute(get);
        try {
            assertEquals("unexpected status", Response.Status.NOT_MODIFIED.getStatusCode(), 
                    response.getStatusLine().getStatusCode());
            log.info("object not modified, nothing returned to client");
        } finally {
            EntityUtils.consume(response.getEntity());
        }        
    }

    /**
     * This method provides a demonstration and test of cache revalidation.
     * @throws Exception
     */
    @Test
    public void testRevalidation() throws Exception {
        log.info("*** testRevalidation ***");
        
        HttpGet get = new HttpGet(new URI(String.format(cachingURI + "/revalidation?delaySecs=5")));
        get.setHeader("Accept", MediaType.APPLICATION_XML);
        
            //we calling for first time -- should get a 200 with a response object
        Header eTagHeader = null;
        Header lastModifiedHeader = null;
        CacheCheck check = null;
        log.info("calling {} {}", get.getMethod(), get.getURI());
        HttpResponse response = httpClient.execute(get);
        try {
            assertEquals("unexpected status", Response.Status.OK.getStatusCode(), 
                    response.getStatusLine().getStatusCode());
            check=JAXBHelper.unmarshall(response.getEntity().getContent(), CacheCheck.class, null);
            lastModifiedHeader = response.getFirstHeader(HttpHeaders.LAST_MODIFIED);
            eTagHeader = response.getFirstHeader(HttpHeaders.ETAG);
            
            assertNotNull("lastModified header not supplied", lastModifiedHeader);
            assertNotNull("eTag header not supplied", eTagHeader);
            log.info(String.format("%s lastModified=%s, eTag=%s)", 
                    new Date(), lastModifiedHeader.getValue(), eTagHeader.getValue()));
        } finally {
            EntityUtils.consume(response.getEntity());
        }
        
            //this is before the cache periods ends
        while(System.currentTimeMillis() < check.getExpiresDate().getTime()-1000) {
            log.info("calling {} {}", get.getMethod(), get.getURI());
            response = httpClient.execute(get);
            try {
                assertEquals("unexpected status", Response.Status.OK.getStatusCode(), 
                        response.getStatusLine().getStatusCode());
                check=JAXBHelper.unmarshall(response.getEntity().getContent(), CacheCheck.class, null);
                lastModifiedHeader = response.getFirstHeader(HttpHeaders.LAST_MODIFIED);
                eTagHeader = response.getFirstHeader(HttpHeaders.ETAG);
                
                assertNotNull("lastModified header not supplied", lastModifiedHeader);
                assertNotNull("eTag header not supplied", eTagHeader);
                log.info(String.format("%s lastModified=%s, eTag=%s)", 
                        new Date(), lastModifiedHeader.getValue(), eTagHeader.getValue()));
                Thread.sleep(1000);
            } finally {
                EntityUtils.consume(response.getEntity());
            }
        }
        log.info("cache period ended -- revalidate should occur soon");
        
            //wait for the cache period to completely end
        while(System.currentTimeMillis() <= check.getExpiresDate().getTime()+1000) {
            Thread.sleep(1000);
        }
        
            //this is after the cache periods ends
        while(System.currentTimeMillis() < check.getExpiresDate().getTime()+3000) {
            log.info("calling {} {}", get.getMethod(), get.getURI());
            response = httpClient.execute(get);
            try {
                assertEquals("unexpected status", Response.Status.OK.getStatusCode(), 
                        response.getStatusLine().getStatusCode());
                check=JAXBHelper.unmarshall(response.getEntity().getContent(), CacheCheck.class, null);
                lastModifiedHeader = response.getFirstHeader(HttpHeaders.LAST_MODIFIED);
                eTagHeader = response.getFirstHeader(HttpHeaders.ETAG);
                
                assertNotNull("lastModified header not supplied", lastModifiedHeader);
                assertNotNull("eTag header not supplied", eTagHeader);
                log.info(String.format("%s lastModified=%s, eTag=%s)", 
                        new Date(), lastModifiedHeader.getValue(), eTagHeader.getValue()));
                Thread.sleep(1000);
            } finally {
                EntityUtils.consume(response.getEntity());
            }
        }
    
    }
}
