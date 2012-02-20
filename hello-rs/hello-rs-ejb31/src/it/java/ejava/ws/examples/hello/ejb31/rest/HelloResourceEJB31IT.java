package ejava.ws.examples.hello.ejb31.rest;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.Properties;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * This class provides an integration test of the deployed service. It runs
 * during the integration-test phase after the application has been built 
 * and deployed to the server.
 */
public class HelloResourceEJB31IT extends HelloResourceEJB31Test {
	static final Properties testProps = new Properties();
	static { try { testProps.load(ClassLoader.getSystemResourceAsStream("it.properties")); } 
		     catch (Exception ex) { fail(ex.getMessage()); } }
	
	protected HttpClient httpClient = new DefaultHttpClient();
	protected URI serviceURI;  
	
	@Override
	public void setUp() throws Exception {
		serviceURI = new URI(
			String.format("http://%s:%s/hello-rs-ejb31",
				testProps.getProperty("host","localhost"),
				Integer.parseInt(testProps.getProperty("port","8080"))));
		super.restImpl = new HelloResourceProxy(serviceURI);
		log.info("serviceURI=" + serviceURI);
	}
}
