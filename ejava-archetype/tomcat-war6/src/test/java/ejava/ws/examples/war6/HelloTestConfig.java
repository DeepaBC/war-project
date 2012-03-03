package ejava.ws.examples.war6;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import ejava.ws.examples.war6.rest.HelloResource;
import ejava.ws.examples.war6.svc.HelloService;
import ejava.ws.examples.war6.svc.HelloServiceImpl;

/**
 * This class provides a factory for POJOs used for unit testing.
 */
@Configuration
@PropertySource("classpath:/test.properties")
public class HelloTestConfig {
    protected static final Logger log = LoggerFactory.getLogger(HelloTestConfig.class);

    @Inject
    Environment env;

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public String testName() {
        String testName=env.getProperty("testName");
        return testName;
    }
    
    @Bean
    public HelloResource helloResource() {
        log.debug("creating simple POJO for helloResource");
        HelloResource resource=new HelloResource();
        return resource;
    }
    
    @Bean
    public HelloService helloService() {
        log.debug("creating simple POJO for helloService");
        return new HelloServiceImpl();
    }
}
