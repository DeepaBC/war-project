package ejava.ws.examples.hello.war6;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import ejava.ws.examples.hello.war6.rest.HelloResource;
import ejava.ws.examples.hello.war6.svc.HelloService;
import ejava.ws.examples.hello.war6.svc.HelloServiceImpl;

/**
 * This class provides a factory for POJOs used for testing.
 */
@Configuration
@PropertySource("classpath:/test.properties")
public class HelloTestConfig {
    protected static final Logger log = LoggerFactory.getLogger(HelloTestConfig.class);

    @Inject
    Environment env;

    @Value("${testProp}")
    public String testProp;
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public String testName() {
        String testName=env.getProperty("testName");
        log.info("testName=" + testName + ", testProp=" + testProp);
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
