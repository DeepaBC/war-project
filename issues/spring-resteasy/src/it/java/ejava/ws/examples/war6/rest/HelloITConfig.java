package ejava.ws.examples.war6.rest;

import java.net.URI;

import java.net.URISyntaxException;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import org.myorg.war6.rest.HelloResource;

/**
 * This class provides the Spring Integration Test configuration. It will
 * be used to override or augment the unit test configuration.
 */
@Configuration
@PropertySource(value="classpath:it.properties")
public class HelloITConfig {
    static final Logger log = LoggerFactory.getLogger(HelloITConfig.class);
    
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

    @Bean
    public HelloResource helloResource() {
        log.debug("creating REST proxy for helloService");
        return new HelloResourceProxy();
    }
}
