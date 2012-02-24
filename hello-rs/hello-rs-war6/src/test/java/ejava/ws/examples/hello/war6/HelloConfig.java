package ejava.ws.examples.hello.war6;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ejava.ws.examples.hello.war6.rest.HelloResource;
import ejava.ws.examples.hello.war6.svc.HelloService;
import ejava.ws.examples.hello.war6.svc.HelloServiceImpl;

/**
 * This class provides a factory for POJOs used for testing.
 */
@Configuration
public class HelloConfig {
    protected static final Logger log = LoggerFactory.getLogger(HelloConfig.class); 
    @Bean
    public HelloResource helloResource() {
        log.debug("creating simple POJO for helloResource");
        HelloResource resource=new HelloResource();
        //resource.setHelloService(helloService()); spring will take care of this
        return resource;
    }
    
    @Bean
    public HelloService helloService() {
        log.debug("creating simple POJO for helloService");
        return new HelloServiceImpl();
    }
}
