package ejava.examples.restintro;

import javax.inject.Inject;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import ejava.examples.restintro.rest.resources.ResidentsResource;
import ejava.examples.restintro.svc.DMVService;
import ejava.examples.restintro.svc.DMVServiceStub;

/**
 * This class provides a factory for POJOs used for unit testing.
 */
@Configuration
@PropertySource("classpath:/test.properties")
public class DmvConfig {
    protected static final Logger log = LoggerFactory.getLogger(DmvConfig.class);
    
    @Inject
    public Environment env;

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean @Singleton
    public ResidentsResource residentsResource() {
        return new ResidentsResource();
    }
    
    @Bean @Singleton
    public DMVService dmvService() {
        return new DMVServiceStub();
    }
}