package ejava.examples.jaxrsrep.jaxrs;

import static org.junit.Assert.*;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
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

import ejava.examples.jaxrsrep.dmv.lic.dto.Application;
import ejava.examples.jaxrsrep.dmv.lic.dto.Person;
import ejava.examples.jaxrsrep.dmv.lic.dto.ResidentIDApplication;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a local unit test demonstration of JAX-RS Methods.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={RepresentationsTestConfig.class})
public class XMLHandlerTest {
	protected static final Logger log = LoggerFactory.getLogger(XMLHandlerTest.class);
	protected static Server server;
	@Inject protected Environment env;
    @Inject protected URI appURI; 
    @Inject protected URI xmlHandlerURI; 
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
    
    /**
     * This test will verify the functionality to register a JAXBContext
     * with the provider so that it can properly demarshal objects that are
     * more than the simple/default case.
     */
    @Test
    public void jaxbContextTest() throws Exception {
        HttpPut put = new HttpPut(xmlHandlerURI + "/jaxbContext");
        put.setHeader("Content-Type", MediaType.APPLICATION_XML);
        put.setHeader("Accept", MediaType.APPLICATION_XML);
        ResidentIDApplication resId = new ResidentIDApplication();
        Person person = new Person("cat", "inhat");
        resId.setIdentity(person);
        put.setEntity(new StringEntity(JAXBHelper.toString(resId)));
        HttpResponse response = httpClient.execute(put);
        try {
            assertEquals("unexpected status", 200, response.getStatusLine().getStatusCode());
            Application app = JAXBHelper.unmarshall(
                    response.getEntity().getContent(), 
                    ResidentIDApplication.class, null,
                    ResidentIDApplication.class,
                    Application.class);
            assertEquals("unexpected firstName", 
                    resId.getIdentity().getFirstName(),
                    ((ResidentIDApplication)app).getIdentity().getFirstName());
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
	
}
