package org.myorg.war6;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.myorg.war6.rest.HelloResource;
import org.myorg.war6.svc.HelloService;
import org.myorg.war6.svc.HelloServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;


/**
 * This class provides a factory for POJOs used for unit testing.
 */
@Configuration
@PropertySource("classpath:/test.properties")
public class HelloTestConfig {
    protected static final Logger log = LoggerFactory.getLogger(HelloTestConfig.class);
    
    public Environment env;
    @Inject
    public void setEnvironment(Environment env) {
        log.info("***************** ** env injected, env=" + env);
        this.env = env;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean
    public String testName() {
        String testName=env.getProperty("testName");
        return testName;
    }
    
    @Bean @Singleton
    public HelloResource helloResource() {
        log.debug("creating simple POJO for helloResource");
        HelloResource resource=new HelloResource();
        return resource;
    }
    
    @Bean @Singleton
    public HelloService helloService() {
        log.debug("creating simple POJO for helloService");
        return new HelloServiceImpl();
    }
}
