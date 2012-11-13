package ejava.examples.jaxrsscale.rest;

import org.mortbay.jetty.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * This class provides the Spring Integration Test configuration. It will
 * be used to override or augment the unit test configuration.
 */
@Configuration
@PropertySource(value="classpath:it.properties")
public class RepresentationsITConfig {
    
    //turn off the unit test HTTP server
    @Bean
    public Server server() throws Exception {
        return null;
    }
}
