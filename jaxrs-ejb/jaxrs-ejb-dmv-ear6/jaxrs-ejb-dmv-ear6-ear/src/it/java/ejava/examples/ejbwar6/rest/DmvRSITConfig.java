package ejava.examples.ejbwar6.rest;

import java.net.URI;



import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import ejava.examples.ejbear6.dmv.rs.ApplicationsRS;
import ejava.examples.ejbear6.dmv.svc.ApplicationsService;
import ejava.examples.ejbear6.rest.ApplicationsServiceProxy;

/**
 * This class provides the Spring Integration Test configuration. It will
 * be used to override or augment the unit test configuration.
 */
@Configuration
@PropertySource(value="classpath:it.properties")
public class DmvRSITConfig {
    static final Logger log = LoggerFactory.getLogger(DmvRSITConfig.class);
    
    protected @Inject Environment env;
    
    /**
     * Create a primary URI to the service under test.
     * @return
     */
    @Bean
    public URI serviceURI() {
        try {
            String host = env.getProperty("host", "localhost");
            int port = env.getProperty("port", Integer.class, 8080);
            String path = env.getProperty("servletContext", "/war6");
            return new URI("http", null, host, port, path, null, null);
        } catch (URISyntaxException ex) {
            throw new RuntimeException("error building uri", ex);
        } 
    }
    
    /**
     * Tells the proxy which JAX-RS implementation to contact.
     */
    @Bean String implContext() { return ""; }

    /**
     * Defines the protocol types allowed.
     * @return
     */
    @Bean String protocol() { return "application/xml"; }

    @Bean 
    public ApplicationsService applicationsService() {
        return new ApplicationsServiceProxy();
    }

    @Bean 
    public URI dmvlicURI() {
        try {
            String host = env.getProperty("host", "localhost");
            int port = env.getProperty("port", Integer.class, 8080);
            String path = env.getProperty("servletContext", "/war6");
            URI baseUri = new URI("http", null, host, port, path, null, null);

            return UriBuilder.fromUri(baseUri)
                    .path(ApplicationsRS.class)
                    .build();
            
        } catch (URISyntaxException ex) {
            throw new RuntimeException("error building uri", ex);
        } 
    }
}
