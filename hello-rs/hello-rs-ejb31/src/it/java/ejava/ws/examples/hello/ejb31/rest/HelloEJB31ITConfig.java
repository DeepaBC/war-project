package ejava.ws.examples.hello.ejb31.rest;

import java.net.URI;

import java.net.URISyntaxException;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import ejava.ws.examples.hello.ejb31.svc.HelloServiceEJB;

@Configuration
@PropertySource("classpath:/it.properties")
public class HelloEJB31ITConfig {
    @Inject
    protected Environment env;
    
    protected URI serviceURI;
    
    @Bean
    URI serviceURI() {
        if (serviceURI != null) {
            return serviceURI;
        }
        try {
            String host=env.getProperty("host", "localhost");
            int port=env.getProperty("port",int.class, 8080);
            String path=env.getProperty("servicePath","/hello-rs-ejb31");            
            serviceURI=new URI("http", null, host, port, path, null, null);
            return serviceURI;
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Bean
    public HelloResource helloResource() {
        return new HelloResource();
    }
    
    @Bean
    public HelloServiceEJB helloService() {
        return new HelloServiceEJB();
    }
}
