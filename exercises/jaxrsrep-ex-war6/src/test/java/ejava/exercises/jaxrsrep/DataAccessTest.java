package ejava.exercises.jaxrsrep;

import static org.junit.Assert.*;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
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

import ejava.exercises.jaxrsrep.bank.BankConfig;
import ejava.exercises.jaxrsrep.bank.dto.Account;
import ejava.exercises.jaxrsrep.bank.dto.BankRepresentation;
import ejava.exercises.jaxrsrep.bank.rs.AccountsRS;

/**
 * This class implements a local unit test of the Bank and Accounts services 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BankConfig.class})
public class DataAccessTest {
	protected static final Logger log = LoggerFactory.getLogger(DataAccessTest.class);
	protected static Server server;
	
	protected @Inject Environment env;
    protected @Inject URI appURI;
	protected @Inject URI dataURI;
    protected @Inject URI dataSolutionURI;
	protected @Inject HttpClient httpClient;
	protected URI targetURI;
	
	protected boolean useSolution=true; //TODO: 1) Change me to false to start
	
	@Before
	public void setUp() throws Exception {	
	    log.debug("=== AccountsTest.setUp() ===");
        log.debug("appURI={}", appURI);
        targetURI = useSolution ? dataSolutionURI : dataURI;
        log.debug("using " + targetURI);
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
    public void testDataBuffered() throws Exception {
        log.info("*** testDataBuffered ***");
        doTestDataAccess(new URI(targetURI + "/calcBuffered"));
    }
    @Test
    public void testDataBuffered2() throws Exception {
        log.info("*** testDataBuffered2 ***");
        doTestDataAccess(new URI(targetURI + "/calcBuffered2"));
    }
    @Test
    public void testDataStream() throws Exception {
        log.info("*** testDataStreamed ***");
        doTestDataAccess(new URI(targetURI + "/calcStreamed"));
    }
    @Test
    public void testDataStream2() throws Exception {
        log.info("*** testDataStreamed2 ***");
        doTestDataAccess(new URI(targetURI + "/calcStreamed2"));
    }

    public void doTestDataAccess(URI uri) throws Exception {
        HttpPost post = new HttpPost(uri);
        post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM);
        post.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_OCTET_STREAM);
        File tmpFile = new File("target/tmp.dat");
        FileOutputStream fos = new FileOutputStream(tmpFile);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        log.info("building input data file");
        int requests=0;
        for (int i=0; i<100; i++) {
            for (int j=1; j<10; j++) {
                for (String op: new String[]{"+", "-", "*", "/"}) {
                    osw.write(String.format("%d %s %d\n", i, op, j));
                    requests+=1;
                }
            }
        }
        osw.close();
        post.setEntity(new InputStreamEntity(new FileInputStream(tmpFile), tmpFile.length()));
        log.info("posting {} requests", requests);
        HttpResponse response = httpClient.execute(post);
        log.info("processing response");
        try {
            assertEquals("unexpected response", 200, response.getStatusLine().getStatusCode());
            int results=0;
            for (LineIterator itr=IOUtils.lineIterator(response.getEntity().getContent(), "UTF-8");
                    itr.hasNext();) {
                String line = itr.nextLine();
                //log.debug(line);
                System.out.print("=");
                results+=1;
            }
            assertEquals("unexpected number of results", requests, results);
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }
}
