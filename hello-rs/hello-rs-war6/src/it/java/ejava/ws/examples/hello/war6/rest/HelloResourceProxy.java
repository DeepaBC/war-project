package ejava.ws.examples.hello.war6.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.ws.util.RESTHelper;

/**
 * This class implements a HTTP proxy to test the HelloResource deployed
 * to the server.
 */
public class HelloResourceProxy extends HelloResource {
	protected static final Logger log = LoggerFactory.getLogger(HelloResourceProxy.class);
	protected URI serviceURI;
	protected HttpClient httpClient = new DefaultHttpClient();

	
	public HelloResourceProxy(URI serviceURI) {
		this.serviceURI = serviceURI;
	}

	@Override
	public String sayHelloREST(String name) {
		try {
            URI uri = new URI(String.format("%s/rest/hello", serviceURI));
			List<NameValuePair> args = new ArrayList<NameValuePair>();
			args.add(new BasicNameValuePair("name", name));
			return RESTHelper.get(String.class, httpClient, uri, args).entity;
		} catch (IOException ex) {
			throw new RuntimeException("error making HTTP call", ex);
		} catch (URISyntaxException ex) {
			throw new RuntimeException("error making HTTP call", ex);
		}
	}
}
