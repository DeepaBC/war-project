package ejava.ws.examples.hello.ejb31.rest;

import javax.ejb.SessionContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import ejava.ws.examples.hello.ejb31.svc.HelloServiceEJB;
import ejava.ws.util.test.SessionContextStub;

/**
 * This class defines the beans and configuration used during testing.
 */
@Configuration
//@PropertySource("classpath:/test.properties")
public class HelloEJB31TestConfig {
    
    /**
     * Used by spring to support PropertySource ${} Value injection
     * @return
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public HelloResource helloResource() {
        return new HelloResource();
    }
    
    /**
     * The EJB is instantiated as a plain POJO during unit testing.
     * @return
     */
    @Bean
    public HelloServiceEJB helloService() {
        return new HelloServiceEJB();
    }
    
    /**
     * Injected into HelloServiceEJB to support the calls to SessionContext.
     * @return
     */
    @Bean
    public SessionContext sessionContext() {
        SessionContext ctx = new SessionContextStub();
        ((SessionContextStub)ctx).setCallerPrincipal("anonymous");
        return ctx;
    }
}
