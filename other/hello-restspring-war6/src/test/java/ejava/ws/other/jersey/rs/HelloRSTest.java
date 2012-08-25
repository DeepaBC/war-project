package ejava.ws.other.jersey.rs;

import static org.junit.Assert.*;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
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

import ejava.ws.other.jersey.model.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/test-config.xml"})
public class HelloRSTest {
	protected static final Logger log = LoggerFactory.getLogger(HelloRSTest.class);

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
	public void testSayHello() {
		log.info("*** testSayHello ***");
		URI uri = UriBuilder.fromUri(helloURI).path(HelloRS.class, "sayHello").build();
		WebResource resource = client.resource(uri);
		resource.accept(MediaType.TEXT_PLAIN_TYPE)
		        .header(HttpHeaders.IF_MATCH, "bar")
		        .type(MediaType.TEXT_PLAIN_TYPE);
		log.debug("GET {}", resource.getURI());

		ClientResponse response = resource.get(ClientResponse.class);
		assertEquals("unexpected status", Response.Status.OK.getStatusCode(), response.getStatus());
		String value = response.getEntity(String.class);
		log.info("response.value={}", value);
	}
	
	@Test
	public void testAddName() {
		log.info("*** testAddName ***");
		
		Person person = new Person();
		person.setFirstName("john");
		person.setLastName("doe");

        WebResource addName = client.resource(UriBuilder
                .fromUri(helloURI)
                .path(HelloRS.class, "addName").build());
        addName.accept(MediaType.APPLICATION_XML_TYPE)
                .accept(MediaType.TEXT_PLAIN_TYPE);

        ClientResponse response = addName.post(ClientResponse.class, person);
        log.debug(response.toString());
        if (Response.Status.CREATED.getStatusCode() == response.getStatus()) {
            log.info("response.value={}", response.getEntity(String.class));
            log.info("location={}", response.getHeaders().getFirst(HttpHeaders.LOCATION));
        }
        else {
            log.error("unexpected status {}: {}", response.getStatus(), response.getEntity(String.class));
            fail();
        }
        
        String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);        
        assertNotNull("no created URI", location);
        WebResource getName = client.resource(location);
        getName.accept(MediaType.APPLICATION_XML_TYPE);
        response = getName.get(ClientResponse.class);
        log.debug(response.toString());
        log.info("response.value={}", response.getEntity(String.class));
        //log.info("response.person={}", response.getEntity(Person.class));
        assertEquals("unexpected status", Response.Status.OK.getStatusCode(), response.getStatus());
	}

}
