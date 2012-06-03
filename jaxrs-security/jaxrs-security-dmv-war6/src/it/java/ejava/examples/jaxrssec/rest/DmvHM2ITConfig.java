package ejava.examples.jaxrssec.rest;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * This class provides the Spring Integration Test configuration for the
 * hypermedia (HM) implementation. 
 */
@Configuration
@PropertySource(value="classpath:it.properties")
public class DmvHM2ITConfig {
    @Bean String protocol() { return "application/xml; q=0.8, application/dmvlic.ejava.2+xml"; }
}
