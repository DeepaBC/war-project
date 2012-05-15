package ejava.exercises.simple.bank;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
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

import ejava.exercises.simple.bank.dto.Account;
import ejava.exercises.simple.bank.dto.Accounts;
import ejava.exercises.simple.bank.dto.Bank;
import ejava.exercises.simple.bank.dto.BankRepresentation;
import ejava.exercises.simple.bank.dto.BankRepresentation.Link;

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
	protected Bank bank;
	
	@Before
	public void setUp() throws Exception {	
	    log.debug("=== AccountsTest.setUp() ===");
        log.debug("bankURI={}", bankURI);
        startServer();
        bank=cleanup();
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
	
	protected Bank cleanup() throws Exception {
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
        return bank;
	}
	
	/**
	 * This test demonstrates a single create, get, update, and delete
	 * scenario for an account.
	 * @throws Exception
	 */
	@Test
	public void testSetupAccount() throws Exception {
	    log.info("*** testSetupAccount ***");
	    
	        //get a reference to accounts
	    Link accounts = bank.getLink(BankRepresentation.ACCOUNTS_REL);
	    assertNotNull("null accounts link", accounts);
	    
	        //create the account
	    Account accountRequest = new Account();
	    accountRequest.setOwnerName("warren buffet");
	    HttpPost createAccount = new HttpPost(accounts.getHref());
	    createAccount.addHeader("Content-Type", MediaType.APPLICATION_XML);
	    createAccount.addHeader("Allow", MediaType.APPLICATION_XML);
	    createAccount.setEntity(new StringEntity(accountRequest.toXML(), "UTF-8"));
	    HttpResponse response = httpClient.execute(createAccount);
	    assertEquals("unexpected status from createAccount", 201, response.getStatusLine().getStatusCode());
	    Account account = BankRepresentation.unmarshall(
	            response.getEntity().getContent(), 
	            Account.class);
	    assertTrue("missing account number", account.getId() > 0);
	    assertEquals("unexpected balance", 0, (int)account.getBalance());
	    Link accountLink = account.getLink(BankRepresentation.SELF_REL);
	    assertNotNull("null self link", accountLink);
	    
	        //update the name
	    account.setOwnerName("warren edward buffet");
	    account.getLinks().clear();
	    HttpPut updateAccount = new HttpPut(accountLink.getHref());
        updateAccount.addHeader("Content-Type", MediaType.APPLICATION_XML);
        updateAccount.setEntity(new StringEntity(account.toXML(), "UTF-8"));
        response = httpClient.execute(updateAccount);
        assertEquals("unexpected status from updateAccount", 204, response.getStatusLine().getStatusCode());
        Header location = response.getFirstHeader("Location");
        assertNotNull("null location", location);
        assertEquals("unexpected location", 
                accountLink.getHref().toString(), 
                location.getValue());
        
            //verify the name was updated
        HttpGet getAccount = new HttpGet(accountLink.getHref());
        getAccount.setHeader("Accept", MediaType.APPLICATION_XML);
        response = httpClient.execute(getAccount);
        assertEquals("unexpected status from getAccount", 200, response.getStatusLine().getStatusCode());
        Account account2 = BankRepresentation.unmarshall(
                response.getEntity().getContent(), 
                Account.class);
        assertEquals("unexpected name", account.getOwnerName(), account2.getOwnerName());
        
            //delete the account
        HttpDelete deleteAccount = new HttpDelete(accountLink.getHref());
        response = httpClient.execute(deleteAccount);
        assertEquals("unexpected delete status", 204, response.getStatusLine().getStatusCode());
        
            //verify the account no longer exists
        getAccount.setHeader("Accept", MediaType.APPLICATION_XML);
        response = httpClient.execute(getAccount);
        assertEquals("unexpected status from getAccount", 404, response.getStatusLine().getStatusCode());
        String errorMsg = IOUtils.toString(response.getEntity().getContent());
        assertNotNull("missing error message", errorMsg);
        log.debug("received expected error message: {}", errorMsg);
	}
	
	/**
	 * This test demonstrates non-XML updates to accounts using POST of 
	 * transactions
	 */
	@Test
	public void testAccountTransactions() throws Exception {
	    log.info("*** testAccountTransactions ***");
	    
            //get a reference to accounts
        Link accountsLink = bank.getLink(BankRepresentation.ACCOUNTS_REL);
        assertNotNull("null account link, bank not open", accountsLink);
        
            //create the account
        Account accountRequest = new Account();
        accountRequest.setOwnerName("warren buffett jr.");
        HttpPost createAccount = new HttpPost(accountsLink.getHref());
        createAccount.addHeader("Content-Type", MediaType.APPLICATION_XML);
        createAccount.addHeader("Allow", MediaType.APPLICATION_XML);
        createAccount.setEntity(new StringEntity(accountRequest.toXML(), "UTF-8"));
        HttpResponse response = httpClient.execute(createAccount);
        assertEquals("unexpected status from createAccount", 201, response.getStatusLine().getStatusCode());
        Account account = BankRepresentation.unmarshall(
                response.getEntity().getContent(), 
                Account.class);
        Link depositLink = account.getLink(BankRepresentation.DEPOSIT_REL);

        float balance = account.getBalance();
        
            //depost a million bucks
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
        balance += amount;
        assertEquals("unexpected content type", MediaType.APPLICATION_XML, response.getFirstHeader("Content-Type").getValue());
        account = BankRepresentation.unmarshall(
                response.getEntity().getContent(), 
                Account.class);
        assertEquals("unexpected balance", (int)balance, (int)account.getBalance());
        Link withdrawLink = account.getLink(BankRepresentation.WITHDRAW_REL);
        
            //withdraw lunch money
        amount = 500;
        HttpPost withdraw = new HttpPost(withdrawLink.getHref());
        withdraw.setHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED);
        withdraw.addHeader("Allow", MediaType.APPLICATION_XML);
        params.clear();
            params.add(new BasicNameValuePair("id", Integer.toString(account.getId())));
            params.add(new BasicNameValuePair("amount", Integer.toString(amount)));
        withdraw.setEntity(new UrlEncodedFormEntity(params));
        response = httpClient.execute(withdraw);
        assertEquals("unexpected status from withdraw", 200, response.getStatusLine().getStatusCode());
        balance -= amount;
        account = BankRepresentation.unmarshall(
                response.getEntity().getContent(), 
                Account.class);
        assertEquals("unexpected balance", (int)balance, (int)account.getBalance());
        Link transferLink = account.getLink(BankRepresentation.TRANSFER_REL);
        
            //create a slush fund
        Account slushRequest = new Account();
        slushRequest.setOwnerName("thin air");
        createAccount.setEntity(new StringEntity(slushRequest.toXML(), "UTF-8"));
        response = httpClient.execute(createAccount);
        assertEquals("unexpected status from createAccount", 201, response.getStatusLine().getStatusCode());
        Account slushAccount = BankRepresentation.unmarshall(
                response.getEntity().getContent(), 
                Account.class);
        assertEquals("unexpected account balance", 0, (int)slushAccount.getBalance());
        
            //stash some money into the slush account
        amount = 10*1000;
        HttpPost transfer = new HttpPost(transferLink.getHref());
        transfer.addHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED);
        transfer.setHeader("Accept", MediaType.APPLICATION_XML);
        params.clear();
            params.add(new BasicNameValuePair("from", Integer.toString(account.getId())));
            params.add(new BasicNameValuePair("to", Integer.toString(slushAccount.getId())));
            params.add(new BasicNameValuePair("amount", Integer.toString(amount)));
        transfer.setEntity(new UrlEncodedFormEntity(params));
        response = httpClient.execute(transfer);
        assertEquals("unexpected status from transfer", 200, response.getStatusLine().getStatusCode());
        balance -= amount;
        assertEquals("unexpected content type", MediaType.APPLICATION_XML, response.getFirstHeader("Content-Type").getValue());
        Accounts accounts = BankRepresentation.unmarshall(
                response.getEntity().getContent(), 
                Accounts.class);
        assertEquals("unexpected number of accounts", 2, accounts.getSize());
        Account account1 = accounts.get(0);
        Account account2 = accounts.get(1);
        if (account1.getId() == account.getId()) { 
            account = account1;
            slushAccount = account2;
        }
        else {
            account = account2;
            slushAccount = account1;
        }
        assertEquals("unexpected account balance", (int)balance, (int)account.getBalance());
        assertEquals("unexpected slush balance", amount, (int)slushAccount.getBalance());
        
            //verify total assets in the bank
        HttpGet getBank = new HttpGet(bankURI);
        getBank.addHeader("Accept", MediaType.APPLICATION_XML);
        response = httpClient.execute(getBank);
        assertEquals("unexpected status from bank", 200, response.getStatusLine().getStatusCode());
        bank = BankRepresentation.unmarshall(
                response.getEntity().getContent(), 
                Bank.class);
        assertEquals("unexpected bank assets", 
                (int)(account.getBalance()+slushAccount.getBalance()), 
                (int)bank.getTotalAssets());
	}
	
	/**
	 * This test verifies the functionality of getting accounts and the links
	 * they express.
	 */
	@Test
	public void testAccounts() throws Exception {
	    log.info("*** testAccounts ***");
	    
            //get a reference to accounts
        Link accountsLink = bank.getLink(BankRepresentation.ACCOUNTS_REL);
        assertNotNull("null account link, bank not open", accountsLink);
        
            //create the accounts
        int numAccounts = 20;
        HttpPost createAccount = new HttpPost(accountsLink.getHref());
        createAccount.addHeader("Content-Type", MediaType.APPLICATION_XML);
        createAccount.addHeader("Allow", MediaType.APPLICATION_XML);
        for (int i=0; i<numAccounts; i++) {
            Account accountRequest = new Account();
            accountRequest.setOwnerName("owner" + i);
            createAccount.setEntity(new StringEntity(accountRequest.toXML(), "UTF-8"));
            HttpResponse response = httpClient.execute(createAccount);
            assertEquals("unexpected status from createAccount", 201, response.getStatusLine().getStatusCode());
            EntityUtils.consume(response.getEntity());
        }
        
            //get accounts a page at a time
        int total=0;
        HttpGet getAccounts = new HttpGet();
        getAccounts.addHeader("Allow", MediaType.APPLICATION_XML);
        Link nextLink = accountsLink; 
        do {
            getAccounts.setURI(nextLink.getHref());
            HttpResponse response = httpClient.execute(getAccounts);
            int statusCode=response.getStatusLine().getStatusCode();
            if (statusCode==200) {
                Accounts accounts = BankRepresentation.unmarshall(
                        response.getEntity().getContent(), 
                        Accounts.class);
                total += accounts.size();
                nextLink=accounts.getLink(BankRepresentation.NEXT_REL);
            }
            else {
                String content = IOUtils.toString(response.getEntity().getContent());
                fail(String.format("unexpected status code: %d\n%s" + statusCode, content));
            }
        } while (nextLink != null);
        assertEquals("unexpected account total", numAccounts, total);
	}
}
