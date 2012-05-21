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
import org.apache.http.entity.StringEntity;
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
import ejava.examples.jaxrsscale.concurrency.dto.ConcurrencyCheck;
import ejava.examples.jaxrsscale.dmv.lic.dto.Person;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a local unit test demonstration of JAX-RS Methods.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ScaleTestConfig.class})
public class ConcurrencyTest {
	protected static final Logger log = LoggerFactory.getLogger(ConcurrencyTest.class);
	protected static Server server;
	@Inject protected Environment env;
    @Inject protected URI appURI; 
    @Inject protected URI concurrencyURI; 
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
    
    @Test 
    public void testConditionalUpdate() throws Exception {
        log.info("*** testConditionalUpdate ***");
        
        HttpGet get = new HttpGet(concurrencyURI);
        get.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
        get.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        
            //make an initial request to get the current eTag
        HttpResponse response = httpClient.execute(get);
        String eTag = null;
        ConcurrencyCheck state=null;
        try {
            assertEquals("unexpected status", Response.Status.OK.getStatusCode(),
                    response.getStatusLine().getStatusCode());
            eTag = response.getFirstHeader(HttpHeaders.ETAG).getValue();
            assertNotNull("null ETag", eTag);
            state = JAXBHelper.unmarshall(response.getEntity().getContent(), ConcurrencyCheck.class, null);
        } finally {
            EntityUtils.consume(response.getEntity());
        }
        
            //attempt to change the value with a different eTag value
        HttpPut put = new HttpPut(concurrencyURI);
        put.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
        put.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);
        put.setHeader(HttpHeaders.IF_MATCH, "12345678980123456");
        state.setModifier("bad eTag");
        state.setToken(state.getToken()+1);
        put.setEntity(new StringEntity(JAXBHelper.toString(state), "UTF-8"));
        response = httpClient.execute(put);
        try {
            assertEquals("unexpected status", Response.Status.PRECONDITION_FAILED.getStatusCode(),
                    response.getStatusLine().getStatusCode());
        } finally {
            EntityUtils.consume(response.getEntity());
        }
        
            //attempt to change the value with the proper eTag
        put.setHeader(HttpHeaders.IF_MATCH, eTag);
        state.setModifier("good eTag");
        put.setEntity(new StringEntity(JAXBHelper.toString(state), "UTF-8"));
        response = httpClient.execute(put);
        String eTag2 = null;
        try {
            assertEquals("unexpected status", Response.Status.OK.getStatusCode(),
                    response.getStatusLine().getStatusCode());
            eTag2 = response.getFirstHeader(HttpHeaders.ETAG).getValue();
            assertNotNull("eTag2 is null", eTag2);
            assertFalse("eTag signature did not change", eTag.equals(eTag2));
            JAXBHelper.unmarshall(response.getEntity().getContent(), ConcurrencyCheck.class, null);
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
    
    private class Adder implements Runnable {
        protected String name;
        protected HttpGet get;
        protected HttpPut put;
        String eTag = "start";
        ConcurrencyCheck state=new ConcurrencyCheck();
        
        public Adder(String name) {
            this.name = name;
            get = new HttpGet(concurrencyURI);
            get.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
            get.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");

            put = new HttpPut(concurrencyURI);
            put.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
            put.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);
        }
        
        protected int execute() throws Exception {
                //optimistically try to update value
            put.setHeader(HttpHeaders.IF_MATCH, eTag);
            state.setModifier(name);
            state.setToken(state.getToken()+1);
            put.setEntity(new StringEntity(JAXBHelper.toString(state), "UTF-8"));
            HttpResponse response = httpClient.execute(put);
            try {
                eTag = response.getFirstHeader(HttpHeaders.ETAG).getValue();
                assertNotNull("null ETag", eTag);
                if (response.getStatusLine().getStatusCode() == 
                        Response.Status.PRECONDITION_FAILED.getStatusCode()) {
                    EntityUtils.consume(response.getEntity());
                        //state was modified -- get a new copy of state
                    response = httpClient.execute(get);
                    try {
                        assertEquals("unexpected status", Response.Status.OK.getStatusCode(),
                                response.getStatusLine().getStatusCode());
                        eTag = response.getFirstHeader(HttpHeaders.ETAG).getValue();
                        assertNotNull("null ETag", eTag);
                        state = JAXBHelper.unmarshall(response.getEntity().getContent(), ConcurrencyCheck.class, null);
                    } finally {
                        EntityUtils.consume(response.getEntity());
                    }
                }
                return response.getStatusLine().getStatusCode();
            } finally {
                EntityUtils.consume(response.getEntity());
            }
        }

        @Override
        public void run() {
        }
    }
}
