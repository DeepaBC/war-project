#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.rest;

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${package}.rest.HelloResource;
import ejava.rs.util.RESTHelper;

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
			return RESTHelper.get(String.class, httpClient, uri,
			        new BasicNameValuePair("name", name)
			        ).entity;
		} catch (IOException ex) {
			throw new RuntimeException("error making HTTP call", ex);
		} catch (URISyntaxException ex) {
			throw new RuntimeException("error making HTTP call", ex);
		}
	}
}
