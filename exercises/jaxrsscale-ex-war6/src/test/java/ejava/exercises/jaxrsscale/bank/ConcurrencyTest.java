package ejava.exercises.jaxrsscale.bank;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
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

import ejava.common.test.ServerConfig;
import ejava.exercises.jaxrsscale.bank.dto.Account;
import ejava.exercises.jaxrsscale.bank.dto.Accounts;
import ejava.exercises.jaxrsscale.bank.dto.Bank;
import ejava.exercises.jaxrsscale.bank.dto.BankRepresentation;
import ejava.exercises.jaxrsscale.bank.dto.BankRepresentation.Link;

/**
 * This class implements a local unit test of the Bank and Accounts services 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BankConfig.class, ServerConfig.class})
public class ConcurrencyTest {
	protected static final Logger log = LoggerFactory.getLogger(ConcurrencyTest.class);
	
	protected @Inject Environment env;
	protected @Inject URI bankURI;
	protected @Inject HttpClient httpClient;
	protected Account account;
	protected String eTag="";
	
	protected boolean fixed=false;//TODO: 1 -- change this to true
	
	@Before
	public void setUp() throws Exception {	
	    log.debug("=== AccountsTest.setUp() ===");
        log.debug("bankURI={}", bankURI);
        account=cleanup();
	}
	
    /**
     * This helper method purges any former bank state, initializes a new
     * bank instance, creates an account, and deposits an initial amount to 
     * work with.
     * @return
     * @throws Exception
     */
	protected Account cleanup() throws Exception {
	        //clear bank of existing accounts
	    HttpDelete resetBank = new HttpDelete(bankURI);
	    HttpResponse response = httpClient.execute(resetBank);
        EntityUtils.consume(response.getEntity());
        assertEquals("unexpected status from reset", 204, response.getStatusLine().getStatusCode());
        
            //open bank for business
	    Bank bank = new Bank();
	    bank.setName("Forbes Bank");
	    HttpPut updateBank = new HttpPut(bankURI);
	    updateBank.addHeader("Content-Type", MediaType.APPLICATION_XML);
	    updateBank.setEntity(new StringEntity(bank.toXML(), "UTF-8"));
	    response = httpClient.execute(updateBank);
        EntityUtils.consume(response.getEntity());
        assertEquals("unexpected status from update", 204, response.getStatusLine().getStatusCode());
        
            //verify bank can now work with accounts
        HttpGet getBank = new HttpGet(bankURI);
        getBank.addHeader("Accept", MediaType.APPLICATION_XML);
        response = httpClient.execute(getBank);
        assertEquals("unexpected status from bank", 200, response.getStatusLine().getStatusCode());
        bank = BankRepresentation.unmarshall(
                response.getEntity().getContent(), 
                Bank.class);
        Link accountsLink = bank.getLink(BankRepresentation.ACCOUNTS_REL);
        assertNotNull("accountsLink null after reset", accountsLink);
        
            //create an account
        Account accountRequest = new Account();
        accountRequest.setOwnerName("warren buffet");
        HttpPost createAccount = new HttpPost(accountsLink.getHref());
        createAccount.addHeader("Content-Type", MediaType.APPLICATION_XML);
        createAccount.addHeader("Allow", MediaType.APPLICATION_XML);
        createAccount.setEntity(new StringEntity(accountRequest.toXML(), "UTF-8"));
        response = httpClient.execute(createAccount);
        assertEquals("unexpected status from createAccount", 201, response.getStatusLine().getStatusCode());
        Account account = BankRepresentation.unmarshall(
                response.getEntity().getContent(), 
                Account.class);
        assertTrue("missing account number", account.getId() > 0);
        assertEquals("unexpected balance", 0, (int)account.getBalance());
        Link accountLink = account.getLink(BankRepresentation.SELF_REL);
        assertNotNull("null self link", accountLink);
        Link depositLink = account.getLink(BankRepresentation.DEPOSIT_REL);
        assertNotNull("null depositLink", depositLink);

            //put some money into the account
        int amount = 1*1000*1000;
        HttpPost deposit = new HttpPost(depositLink.getHref());
        deposit.addHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED);
        deposit.addHeader("Allow", MediaType.APPLICATION_XML);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", Integer.toString(account.getId())));
            params.add(new BasicNameValuePair("amount", Integer.toString(amount)));
        deposit.setEntity(new UrlEncodedFormEntity(params));
        response = httpClient.execute(deposit);
        assertEquals("unexpected status from deposit", 200, response.getStatusLine().getStatusCode());
        assertEquals("unexpected content type", MediaType.APPLICATION_XML, response.getFirstHeader("Content-Type").getValue());
        account = BankRepresentation.unmarshall(
                response.getEntity().getContent(), 
                Account.class);
        eTag = response.getFirstHeader(HttpHeaders.ETAG).getValue();
        assertEquals("unexpected balance", (int)amount, (int)account.getBalance());
        return account;
	}
	
	@Test
	public void testWithdraw() throws Exception {
	    log.info("*** testWithdraw ***");
	    
	        //save off current balance
	    float balance = account.getBalance();
	    
	        //get withdraw link
	    final Link withdrawLink = account.getLink(BankRepresentation.WITHDRAW_REL);
	    assertNotNull("null withdraw link", withdrawLink);
	    
	        //make a withdraw
	    final int amount=1000;
	    Object[] result = withdraw(withdrawLink, amount);
	    account=(Account) result[0];
	    eTag = (String) result[1];
	    
	        //we should have 1 successful withdraw
        balance -= amount;
        assertEquals("unexpected balance", (int)balance, (int)account.getBalance());
        
            //make a withdraw from several threads
        for (int i=0; i<10; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        log.info("withdraw from thread {}", this);
                        withdraw(withdrawLink, amount);
                    }
                    catch (Exception ex) {
                        fail("unexpected exception:" + ex.getLocalizedMessage());
                    }
                    finally {}
                }
                
            };
            thread.run();
        }
        Thread.sleep(3);
        
        //we should have only 1/2nd successful withdraw -- all others fail
        balance -= amount;
        account=getAccount(account.getLink(BankRepresentation.SELF_REL));
        if (fixed) {
            assertEquals("unexpected balance", (int)balance, (int)account.getBalance());
        }
	}
	
	/**
	 * This helper method will withdraw the requested amount of money from the 
	 * account under test. The account instance will be updated by the method 
	 * upon completion.
	 * @param withdrawLink
	 * @param amount
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws JAXBException
	 */
	private Object[] withdraw(Link withdrawLink, int amount) 
	        throws IOException, IllegalStateException, JAXBException {
            HttpPost withdraw = new HttpPost(withdrawLink.getHref());
            withdraw.setHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED);
            withdraw.addHeader("Allow", MediaType.APPLICATION_XML);
            
            withdraw.addHeader(HttpHeaders.IF_MATCH,eTag);
            
            List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", Integer.toString(account.getId())));
                params.add(new BasicNameValuePair("amount", Integer.toString(amount)));
            withdraw.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpClient.execute(withdraw);
            int status = response.getStatusLine().getStatusCode();
            assertTrue("unexpected status from withdraw:" + status, status==200 || status==412);
            Account account = BankRepresentation.unmarshall(
                    response.getEntity().getContent(), 
                    Account.class);
            String eTag = response.getFirstHeader(HttpHeaders.ETAG).getValue();
            return new Object[] { account, eTag };
	}
	
	private Account getAccount(Link accountLink) 
	        throws IllegalStateException, JAXBException, IOException {
        HttpGet getAccount = new HttpGet(accountLink.getHref());
        getAccount.addHeader("Allow", MediaType.APPLICATION_XML);
        HttpResponse response = httpClient.execute(getAccount);
        assertEquals("unexpected status from getAccount", 200, response.getStatusLine().getStatusCode());
        Account account = BankRepresentation.unmarshall(
                response.getEntity().getContent(), 
                Account.class);
        return account;
	}
	
}
