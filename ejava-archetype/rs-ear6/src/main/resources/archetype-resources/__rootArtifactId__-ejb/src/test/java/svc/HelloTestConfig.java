#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.svc;

import javax.ejb.SessionContext;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import ejava.common.test.stub.SessionContextStub;
import ${package}.rest.HelloResource;
import ${package}.svc.HelloService;
import ${package}.svc.HelloServiceEJB;

/**
 * This class provides a factory for POJOs used for unit testing.
 */
@Configuration
@PropertySource("classpath:/test.properties")
public class HelloTestConfig {
    
    protected @Value("${symbol_dollar}{user.identity}") String identity;
    
    /**
     * Used by spring to support PropertySource ${symbol_dollar}{} Value injection
     * @return
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     * The EJB is instantiated as a plain POJO during unit testing.
     * @return
     */
    @Bean
    public HelloService helloService() {
        return new HelloServiceEJB();
    }
    
    /**
     * Injected into HelloServiceEJB to support the calls to SessionContext.
     * @return
     */
    @Bean
    public SessionContext sessionContext() {
        SessionContext ctx = new SessionContextStub();
        ((SessionContextStub)ctx).setCallerPrincipal(identity);
        return ctx;
    }
    
    @Bean
    public HelloResource helloResource() {
        return new HelloResource();
    }
}
