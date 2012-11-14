package ejava.exercises.jaxrscs.bank;

import static org.junit.Assert.*;


import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.common.test.ServerConfig;
import ejava.exercises.jaxrscs.bank.dto.Account;
import ejava.exercises.jaxrscs.bank.dto.BankRepresentation;
import ejava.exercises.jaxrscs.bank.rs.AccountsRS;

/**
 * This class implements a local unit test of the Bank and Accounts services 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BankConfig.class, ServerConfig.class})
public class AccountsTest {
    protected static final Logger log = LoggerFactory.getLogger(AccountsTest.class);
    
    protected @Inject Environment env;
    protected @Inject URI appURI;
    protected @Inject URI accountsURI;
    protected @Inject HttpClient httpClient;
    
    @Before
    public void setUp() throws Exception {  
        log.debug("=== AccountsTest.setUp() ===");
        log.debug("appURI={}", appURI);
        log.debug("accountsURI={}", accountsURI);
    }
    
    @AfterClass()
    public static void tearDownClass() {
        log.debug("create breakpoint here to access browser for last step");
    }
    
    
    /**
     * This test will verify that we can communicate with the accounts resource
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    //TODO: 1a - activate this test
    @Ignore 
    @Test
    public void testAccountsAccess() throws Exception {
        HttpOptions options = new HttpOptions(accountsURI);
        HttpResponse response = httpClient.execute(options);
        try {
            assertEquals("cannot communicate with Accounts:" + options.getURI().getPath(), 200, response.getStatusLine().getStatusCode());
            log.info(response.getFirstHeader("Allow").toString());
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
    
    /**
     * This test will verify we can GET a text string from a resource method
     */
    //TODO: 2a activate this test
    @Ignore
    @Test
    public void testGetHello() throws Exception {
        HttpGet get = new HttpGet(accountsURI);
        get.addHeader("Accept", MediaType.TEXT_PLAIN);
        HttpResponse response = httpClient.execute(get);
        try {
            assertEquals("unexpected status from getHello:" + get.getURI().getPath(), 200, response.getStatusLine().getStatusCode());
            String reply = EntityUtils.toString(response.getEntity());
            assertEquals("unexpected reply string", "hello", reply);
            log.info("reply was {}", reply);
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }

    /**
     * This test will verify we can pass a template parameter to a method.
     * @throws Exception
     */
    //TODO: 3a activate this test
    @Ignore
    @Test
    public void testGreeting() throws Exception {
        HttpGet get = new HttpGet(accountsURI + "/greeting/afternoon");
        get.addHeader("Accept", MediaType.TEXT_PLAIN);
        HttpResponse response = httpClient.execute(get);
        try {
            assertEquals("unexpected status from getGeeting:" + get.getURI().getPath(), 200, response.getStatusLine().getStatusCode());
            String reply = EntityUtils.toString(response.getEntity());
            assertEquals("unexpected reply string", "good afternoon!", reply);
            log.info("reply was {}", reply);
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
    
    /**
     * This test will verify that we can inject a helper object to 
     * into the service resource to determine the URI being called.
     */
    //TODO: 4a activate this test
    @Ignore
    @Test
    public void testGetMyPath() throws Exception {
        HttpGet get = new HttpGet(accountsURI + "/inject");
        get.addHeader("Accept", MediaType.TEXT_PLAIN);
        HttpResponse response = httpClient.execute(get);
        try {
            assertEquals("unexpected status from getMyPath:" + get.getURI().getPath(), 200, response.getStatusLine().getStatusCode());
            String reply = EntityUtils.toString(response.getEntity());
            assertEquals("unexpected path", 
                    UriBuilder.fromUri("/")
                        .path(AccountsRS.class)
                        .path(AccountsRS.class, "getMyPath")
                        .build()
                        .getPath(), 
                        reply);
            log.info("reply was {}", reply);
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
    
    /**
     * This test will verify that we can pass a query parameter into the 
     * service resource method.
     */
    //TODO: 5a activate this test
    @Ignore
    @Test
    public void testMyLine() throws Exception {
        boolean encoded = false;
        String questions=
                "q1=" + (!encoded ? "do you develop software?" : URLEncoder.encode("do you develop software?", "UTF-8")) +
                "&q2=" + (!encoded ? "JavaEE?" : URLEncoder.encode("JavaEE?", "UTF-8"));
        HttpGet get = new HttpGet(accountsURI + "/query?" + questions);
        get.addHeader("Accept", MediaType.TEXT_PLAIN);
        HttpResponse response = httpClient.execute(get);
        try {
            assertEquals("unexpected status from getMyPath:" + get.getURI().getPath(), 200, response.getStatusLine().getStatusCode());
            String reply = EntityUtils.toString(response.getEntity());
            assertEquals("unexpected answer", "yes", reply);
            log.info("reply was {}", reply);
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
    
    /**
     * This test will verify we can POST form encoded parameters to a resource
     * method.
     * @throws Exception
     */
    //TODO: 6a activate this test
    @Ignore
    @Test
    public void testCreateAccountForm() throws Exception {
        HttpPost post = new HttpPost(accountsURI);
        post.addHeader("Accept", MediaType.APPLICATION_XML);
        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        formParams.add(new BasicNameValuePair("ownerName", "jim"));
        formParams.add(new BasicNameValuePair("amount", "1000"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, "UTF-8");
        post.setEntity(entity);
        HttpResponse response = httpClient.execute(post);
        try {
            assertEquals("unexpected status from getMyPath:" + post.getURI().getPath(), 201, response.getStatusLine().getStatusCode());
            Account account = BankRepresentation.unmarshall(response.getEntity().getContent(), Account.class);
            assertEquals("unexpected owner", "jim", account.getOwnerName());
            log.info("reply was {} ", account.toXML());
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
    
}
