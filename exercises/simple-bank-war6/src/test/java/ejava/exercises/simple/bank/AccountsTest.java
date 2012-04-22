package ejava.exercises.simple.bank;

import static org.junit.Assert.*;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;



import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
 * This class implements a local unit test of the Bank and Accounts services 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BankConfig.class})
public class AccountsTest {
	protected static final Logger log = LoggerFactory.getLogger(AccountsTest.class);
	protected static Server server;
	
	protected @Inject Environment env;
	protected @Inject URI bankURI;
	protected @Inject HttpClient httpClient;
	
	@Before
	public void setUp() throws Exception {	
	    log.debug("=== AccountsTest.setUp() ===");
        log.debug("bankURI={}", bankURI);
        startServer();
        cleanup();
	}
	
	protected void startServer() throws Exception {
	    if (bankURI.getPort()>=9092) {
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
	public void tearDown() throws Exception {
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
	
	protected void cleanup() {
	}
	
	@Test
	public void testCreateAccount() throws ClientProtocolException, IOException {
	    log.info("*** testCreateAccount ***");
	    
	    HttpGet getBank = new HttpGet(bankURI);
	    getBank.addHeader("Accept", MediaType.APPLICATION_XML);
	    HttpResponse response = httpClient.execute(getBank);
	    assertEquals("unexpected status from bank", 200, response.getStatusLine().getStatusCode());
	}
}
