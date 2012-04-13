package ejava.examples.restintro.rest;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ejava.examples.restintro.dmv.svc.ApplicationsService;
import ejava.examples.restintro.dmv.svc.ResidentsService;

/**
 * This class provides the Spring Integration Test configuration for 
 * accessing the HTTP-focused ResidentsResource example.
 */
@Configuration
@PropertySource(value="classpath:it.properties")
public class DmvHttpITConfig {
    /**
     * Tells the proxy which JAX-RS implementation to contact.
     */
    @Bean String implContext() { return "http"; }

    @Bean
    public ApplicationsService applicationsService() {
        return new ApplicationsServiceProxy();
    }
}
