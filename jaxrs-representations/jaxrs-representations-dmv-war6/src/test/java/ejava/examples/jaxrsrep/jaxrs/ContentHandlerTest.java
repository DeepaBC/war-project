package ejava.examples.jaxrsrep.jaxrs;

import static org.junit.Assert.*;


import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.EntityUtils;
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

import ejava.examples.jaxrsrep.dmv.lic.dto.Person;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a local unit test demonstration of JAX-RS Methods.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={RepresentationsTestConfig.class})
public class ContentHandlerTest {
	protected static final Logger log = LoggerFactory.getLogger(ContentHandlerTest.class);
	protected static Server server;
	@Inject protected Environment env;
    @Inject protected URI appURI; 
    @Inject protected URI contentHandlerURI; 
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
	

    /**
     * This helper method will test the ability to stream data objects back
     * from the service.
     */
    public void doTestStreamingOutput(URI uri) throws Exception {
        HttpGet get = new HttpGet(uri);
        get.addHeader("Accept", MediaType.APPLICATION_OCTET_STREAM);
        HttpResponse response = httpClient.execute(get);
        try {
            assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
            assertNull("unexpected lastModified", response.getFirstHeader("Last-Modified"));
            log.debug("Content-Length={}, length={}", response.getFirstHeader("Content-Length"), response.getEntity().getContentLength());
            ObjectInputStream ois = new ObjectInputStream(response.getEntity().getContent());
            Person p=null;
            while ((p=(Person) ois.readObject()) != null) {
                log.debug(JAXBHelper.toString(p));
            }
        } catch (EOFException done) {    
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
    
    /**
     * This method will verify the return of streamed data using an OutputStream
     * injected into the resource method.
     * @throws Exception
     */
    @Test
    public void testOutputStream() throws Exception {
        log.info("*** testOutputStream ***");
        doTestStreamingOutput(new URI(contentHandlerURI + "/outputStream"));
    }

    /**
     * This method will verify the return of streamed data using a StreamingOutput
     * response object.
     * @throws Exception
     */
    @Test
    public void testStreamingOutput() throws Exception {
        log.info("*** testStreamingOutput ***");
        doTestStreamingOutput(new URI(contentHandlerURI + "/streamingOutput"));
    }

    /**
     * This method verifies that the streaming response can be returned with
     * a more detailed response status.
     * @throws Exception
     */
    @Test
    public void testStreamingOutput2() throws Exception {
        log.info("*** testStreamingOutput2 ***");
        
        HttpGet get = new HttpGet(contentHandlerURI + "/streamingOutput2");
        get.addHeader("Accept", MediaType.APPLICATION_OCTET_STREAM);
        HttpResponse response = httpClient.execute(get);
        try {
            assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
            assertNotNull("no lastModified", response.getFirstHeader("Last-Modified"));
            ObjectInputStream ois = new ObjectInputStream(response.getEntity().getContent());
            Person p=null;
            while ((p=(Person) ois.readObject()) != null) {
                log.debug(JAXBHelper.toString(p));
            }
        } catch (EOFException done) {    
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }

   /**
    * This method will verify that a resource method can read information
    * from a stream. 
    * @throws Exception
    */
    @Test 
    public void testInputStream() throws Exception {
        log.info("*** testInputStream ***");
        HttpPut put = new HttpPut(contentHandlerURI + "/inputStream");
        put.setHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
        put.setHeader("Accept", MediaType.TEXT_PLAIN);
        
        //stash some data somewhere where we can read it using an InputStream
        File file = File.createTempFile("input", ".dat", new File("target"));
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        int count=5*10*1000;
        log.debug("building data file with {} objects", count);
        for (int i=0; i<count; i++) {
            Person p = new Person("person-"+i, "doe");
            oos.writeObject(p);
            if (i%1000==0) {
                log.debug("wrote {} of {}", i, count);
            }
        }
        oos.close();
        log.debug("done building data file with {} objects, size={} bytes", count, file.length());
        
        
        //form an input stream for the entity
        InputStream is = new FileInputStream(file);
        InputStreamEntity ise = new InputStreamEntity(is, file.length());        
        put.setEntity(ise);
        
        HttpResponse response = httpClient.execute(put);
        try {
            assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
            int countReceived = 
                Integer.parseInt(EntityUtils.toString(response.getEntity(), "UTF-8"));
            assertEquals("unexpected count", count, countReceived);
            
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
    
    

    /**
     * This method will verify that a resource method can read information
     * from a temporary file. Note the the fact we use a file on the client 
     * end and on the server end is independent. The data read from is a 
     * different file from the one staged in the server.
     * @throws Exception
     */
     @Test 
     public void testPutFromFile() throws Exception {
         log.info("*** testPutFromFile ***");
         HttpPut put = new HttpPut(contentHandlerURI + "/fromFile");
         put.setHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
         put.setHeader("Accept", MediaType.TEXT_PLAIN);
         
         //stash some data somewhere where we can read it using an InputStream
         File file = File.createTempFile("input", ".dat", new File("target"));
         FileOutputStream fos = new FileOutputStream(file);
         ObjectOutputStream oos = new ObjectOutputStream(fos);
         int count=5;
         for (int i=0; i<count; i++) {
             Person p = new Person("person-"+i, "doe");
             oos.writeObject(p);
         }
         oos.close();
         
         //form an input stream for the entity
         FileInputStream fis = new FileInputStream(file);
         InputStreamEntity ise = new InputStreamEntity(fis, file.length());
         put.setEntity(ise);
         log.debug("source file {}", file.getAbsolutePath());
         
         HttpResponse response = httpClient.execute(put);
         try {
             assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
             int countReceived = 
                 Integer.parseInt(EntityUtils.toString(response.getEntity(), "UTF-8"));
             assertEquals("unexpected count", count, countReceived);
             
         } finally {
             EntityUtils.consume(response.getEntity());
         }
     }
     
     /**
      * This test will verify the ability of a resource method to provide its 
      * response to the provider thru a file.
      * @throws Exception
      */
     @Ignore @Test //having issues with tmp file on windows 
     public void testGetFromFile() throws Exception {
         log.info("*** testGetFromFile ***");
         
         HttpGet get = new HttpGet(contentHandlerURI + "/fromFile");
         get.setHeader("Accept", MediaType.APPLICATION_OCTET_STREAM);
         HttpResponse response = httpClient.execute(get);
         try {
             assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
             log.debug("Content-Length={}, length={}", response.getFirstHeader("Content-Length"), response.getEntity().getContentLength());
             ObjectInputStream ois = new ObjectInputStream(response.getEntity().getContent());
             Person p=null;
             while ((p=(Person) ois.readObject()) != null) {
                 log.debug(JAXBHelper.toString(p));
             }
         } catch (EOFException done) {
         } finally {
             EntityUtils.consume(response.getEntity());
         }
     }
}
