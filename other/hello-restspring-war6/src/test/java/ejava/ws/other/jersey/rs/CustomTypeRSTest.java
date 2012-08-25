package ejava.ws.other.jersey.rs;

import static org.junit.Assert.*;


import info.ejava.organization.Org;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import ejava.ws.other.jersey.xml.MyOjectFactoryTest;

/**
 * This test will issue and receive Org/Organization objects. On the client
 * side, we will use JAXB-generated Org classes. On the server-side, we will
 * use custom Organization classes that have JPA-specific extensions and 
 * properties not part of the XML.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/test-config.xml"})
public class CustomTypeRSTest {
	protected static final Logger log = LoggerFactory.getLogger(CustomTypeRSTest.class);

	@Inject Server server;
	@Inject Client client;
	@Inject URI helloURI;
	
	@Before
	public void setUp() {
		log.debug("server={}", server);
		log.debug("client={}", client);
		log.debug("helloURI={}", helloURI);
	}

	@Test
	public void testCustomType() {
		log.info("*** testCustomType ***");
		
		Org org = MyOjectFactoryTest.createOrg();
		
		URI uri = UriBuilder.fromUri(helloURI).path(HelloRS.class, "createOrg").build();
		WebResource resource = client.resource(uri);

		ClientResponse response = resource
		        .type(MediaType.APPLICATION_XML)
		        .post(ClientResponse.class, org);
        log.debug(response.toString());
		assertEquals("unexpected status", Response.Status.OK.getStatusCode(), response.getStatus());
		String value = response.getEntity(String.class);
		log.info("response.value={}", value);
	}
	

}
