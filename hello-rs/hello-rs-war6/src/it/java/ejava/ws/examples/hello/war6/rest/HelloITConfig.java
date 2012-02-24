package ejava.ws.examples.hello.war6.rest;

import static org.junit.Assert.fail;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ejava.ws.examples.hello.war6.rest.HelloResource;

/**
 * This class is used to create object implementations that are appropriate
 * for integration testing.
 */
@Configuration
public class HelloITConfig {
    static final Logger log = LoggerFactory.getLogger(HelloITConfig.class);
    static final Properties itProps = new Properties();
    static { try { itProps.load(ClassLoader.getSystemResourceAsStream("it.properties")); } 
             catch (Exception ex) { fail(ex.getMessage()); } }

    /**
     * Create a primary URI to the service under test.
     * @return
     */
    @Bean
    public URI serviceURI() {
        log.debug("creating serviceURI");
        try {
            return new URI(String.format("http://%s:%s/hello-rs-war6",
                    itProps.getProperty("host","localhost"),
                    Integer.parseInt(itProps.getProperty("port","8080"))));
        } catch (NumberFormatException ex) {
            throw new RuntimeException("error parsing port property", ex);
        } catch (URISyntaxException ex) {
            throw new RuntimeException("error building uri", ex);
        } finally {}
    }

    @Bean
    public HelloResource helloResource() {
        log.debug("creating REST proxy for helloService");
        return new HelloResourceProxy();
    }
}
