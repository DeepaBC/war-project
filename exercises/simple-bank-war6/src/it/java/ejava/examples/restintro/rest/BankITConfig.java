package ejava.examples.restintro.rest;

import java.net.URI;

import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import ejava.exercises.simple.bank.resources.BankRS;

/**
 * This class provides the Spring Integration Test configuration. It will
 * be used to override or augment the unit test configuration.
 */
@Configuration
@PropertySource(value="classpath:it.properties")
public class BankITConfig {
    protected @Inject Environment env;
    
    /**
     * Create a primary URI to the service under test.
     * @return
     */
    @Bean 
    public URI bankURI() {
        try {
            String host = env.getProperty("host", "localhost");
            int port = env.getProperty("port", Integer.class, 8080);
            String path = env.getProperty("servletContext", "/war6");
            URI baseUri = new URI("http", null, host, port, path, null, null);

            return UriBuilder.fromUri(baseUri)
                    .path(BankRS.class)
                    .build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException("error building uri", ex);
        } 
    }
}
