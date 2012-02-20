package ejava.ws.examples.hello.war6.rest;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.Properties;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class HelloResourceIT extends HelloResourceTest {
	static final Properties testProps = new Properties();
	static { try { testProps.load(ClassLoader.getSystemResourceAsStream("it.properties")); } 
		     catch (Exception ex) { fail(ex.getMessage()); } }
	
	protected HttpClient httpClient = new DefaultHttpClient();
	protected URI serviceURI;  
	
	@Override
	public void setUp() throws Exception {
		serviceURI = new URI(
			String.format("http://%s:%s/hello-rs-war6",
				testProps.getProperty("host","localhost"),
				Integer.parseInt(testProps.getProperty("port","8080"))));
		super.restImpl = new HelloResourceProxy(serviceURI);
		log.info("serviceURI=" + serviceURI);
	}
}
