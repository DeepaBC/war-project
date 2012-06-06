package ejava.exercises.jaxrsscale.bank;

import static org.junit.Assert.*;


import java.net.URI;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
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

import ejava.exercises.jaxrsscale.bank.dto.Bank;
import ejava.exercises.jaxrsscale.bank.dto.BankRepresentation;
import ejava.exercises.jaxrsscale.bank.dto.BankRepresentation.Link;

/**
 * This class implements a local unit test of the Bank and Accounts services 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BankConfig.class})
public class BankTest {
	protected static final Logger log = LoggerFactory.getLogger(BankTest.class);
	protected static Server server;
	
	protected @Inject Environment env;
	protected @Inject URI bankURI;
	protected @Inject HttpClient httpClient;
	
	@Before
	public void setUp() throws Exception {	
	    log.debug("=== BankTest.setUp() ===");
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
	
	protected void cleanup() throws Exception {
            //reset bank state
	    HttpDelete resetBank = new HttpDelete(bankURI);
        HttpResponse response = httpClient.execute(resetBank);
        EntityUtils.consume(response.getEntity());
        assertEquals("unexpected status", 204, response.getStatusLine().getStatusCode());
	}
	
	/**
	 * This test demonstrates a single get and update scenario for an bank.
	 * @throws Exception
	 */
	@Test
	public void testSetupBank() throws Exception {
	    log.info("*** testSetupBank ***");
	    
	        //get current state of bank
	    HttpGet getBank = new HttpGet(bankURI);
	    getBank.addHeader("Accept", MediaType.APPLICATION_XML);
	    HttpResponse response = httpClient.execute(getBank);
	    assertEquals("unexpected status from bank", 200, response.getStatusLine().getStatusCode());
	    Bank bank = BankRepresentation.unmarshall(
	            response.getEntity().getContent(), Bank.class);
	    Link bankLink = bank.getLink(BankRepresentation.SELF_REL);
	    assertNotNull("null bankLink", bankLink);
	    assertNull("unexpected bank name", bank.getName());
	    Link accountsLink = bank.getLink(BankRepresentation.ACCOUNTS_REL);
	    assertNull("non-null accountsLink for anonymous bank", accountsLink);
	    
	        //change the name of the bank
	    bank.setName("Buffett S&L");
	    bank.getLinks().clear();
	    HttpPut updateBank = new HttpPut(bankLink.getHref());
	    updateBank.addHeader("Content-Type", MediaType.APPLICATION_XML);
        updateBank.setEntity(new StringEntity(bank.toXML(), "UTF-8"));
	    response = httpClient.execute(updateBank);
	    assertEquals("unexpected status", 204, response.getStatusLine().getStatusCode());

	        //verify the change was made
        response = httpClient.execute(getBank);
        assertEquals("unexpected status from bank", 200, response.getStatusLine().getStatusCode());
        Bank bank2 = BankRepresentation.unmarshall(
                response.getEntity().getContent(), Bank.class);
        assertEquals("unexpected bank name", bank.getName(), bank2.getName());
        
            //verify we now have a link to open accounts
        accountsLink = bank2.getLink(BankRepresentation.ACCOUNTS_REL);
        assertNotNull("null accountsLink named bank", accountsLink);
	}
}
