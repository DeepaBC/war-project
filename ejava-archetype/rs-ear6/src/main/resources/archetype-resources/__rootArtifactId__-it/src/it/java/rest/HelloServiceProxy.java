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

import ${package}.svc.HelloService;
import ejava.rs.util.RESTHelper;

/**
 * This class implements a HTTP proxy to test the HelloResource deployed
 * to the server.
 */
public class HelloServiceProxy implements HelloService {
	protected static final Logger log = LoggerFactory.getLogger(HelloServiceProxy.class);
	protected URI serviceURI;
	protected HttpClient httpClient = new DefaultHttpClient();
	
	public HelloServiceProxy(URI serviceURI) {
		this.serviceURI = serviceURI;
	}

	@Override
	public String sayHello(String name) {
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
