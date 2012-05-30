package ejava.ws.examples.hello.ear6.rest;

import java.net.URI;

import java.net.URISyntaxException;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import ejava.ws.examples.hello.ear6.svc.HelloService;

/**
 * This class provides the Spring Integration Test configuration. It will
 * be used to override or augment the unit test configuration.
 */
@Configuration
@PropertySource("classpath:/it.properties")
public class HelloEAR6ITConfig {
    @Inject
    protected Environment env;
    
    @Bean
    URI serviceURI() {
        try {
            String host=env.getProperty("host", "localhost");
            int port=env.getProperty("port",int.class, 8080);
            String path=env.getProperty("servicePath","/hello-rs-ear6");            
            return new URI("http", null, host, port, path, null, null);
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Bean
    public HelloService helloService() {
        return new HelloServiceProxy(serviceURI());
    }
}
