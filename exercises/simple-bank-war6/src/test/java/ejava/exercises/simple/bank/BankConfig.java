package ejava.exercises.simple.bank;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import ejava.exercises.simple.bank.rs.AccountsRS;
import ejava.exercises.simple.bank.rs.BankRS;
import ejava.exercises.simple.bank.svc.AccountsService;
import ejava.exercises.simple.bank.svc.AccountsServiceStub;
import ejava.exercises.simple.bank.svc.BankService;
import ejava.exercises.simple.bank.svc.BankServiceStub;

/**
 * This class provides a factory for POJOs used for unit testing.
 */
@Configuration
@PropertySource("classpath:/test.properties")
public class BankConfig {
    protected static final Logger log = LoggerFactory.getLogger(BankConfig.class);
    
    @Inject
    public Environment env;

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean @Singleton
    public BankService bankService() {
        return new BankServiceStub();
    }
    
    @Bean @Singleton
    public AccountsService accountsService() {
        return new AccountsServiceStub();
    }
    
    //the following beans are used within the Jetty development env and are
    //shared between resteasy and spring
    @Bean @Singleton
    public AccountsRS accountsRS() {
        return new AccountsRS();
    }
    
    @Bean @Singleton
    public BankRS bankRS() {
        return new BankRS();
    }

    @Bean @Singleton
    public HttpClient httpClient() {
        log.info("creating non-cached HttpClient");
        final long jettyDelay=env.getProperty("jetty.delay", Long.class, 100L);
        log.info("creating non-cached HttpClient");
        HttpClient httpClient = new DefaultHttpClient() {
            @Override
            public HttpContext createHttpContext() {
                //try to avoid the Jetty deadlocks
                try { Thread.sleep(jettyDelay); } catch (Exception ex) {}
                return super.createHttpContext();
            }
        };
        return httpClient;
    }
    
    @Bean 
    public URI bankURI() {
        try {
            //this is the URI of the local jetty instance for unit testing
            String host=env.getProperty("host", "localhost");
            //default to http.server.port and allow a http.client.port override
            int port=Integer.parseInt(env.getProperty("http.client.port",
                env.getProperty("http.server.port")
                ));
            String path=env.getProperty("servletContext", "/");
            return new URI("http", null, host, port, path + "/bank", null, null);
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            throw new RuntimeException("error creating URI:" + ex, ex);
        }
    }
}
