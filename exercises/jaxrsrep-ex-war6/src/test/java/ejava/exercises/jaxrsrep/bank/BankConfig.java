package ejava.exercises.jaxrsrep.bank;

import java.net.URI;


import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import ejava.exercises.jaxrsrep.bank.rs.AccountsRS;
import ejava.exercises.jaxrsrep.bank.svc.AccountsService;
import ejava.exercises.jaxrsrep.bank.svc.AccountsServiceStub;
import ejava.exercises.jaxrsrep.rs.DataAccessRS;
import ejava.exercises.jaxrsrep.bank.rs.BankRS;
import ejava.exercises.jaxrsrep.bank.svc.BankService;
import ejava.exercises.jaxrsrep.bank.svc.BankServiceStub;

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
    
    @Bean @Singleton
    public BankRS bankRS() {
        return new BankRS();
    }

    @Bean @Singleton
    public AccountsRS accountsRS() {
        return new AccountsRS();
    }
    
    @Bean 
    public DataAccessRS dataRS() {
        return new DataAccessRS();
    }
    
    @Bean @Singleton
    public HttpClient httpClient() {
        log.info("creating non-cached HttpClient");
        HttpClient httpClient = new DefaultHttpClient();
        return httpClient;
    }

    @Bean
    public URI appURI() {
        try {
            //this is the URI of the local jetty instance for unit testing
            String host=env.getProperty("host", "localhost");
            //default to http.server.port and allow a http.client.port override
            int port=Integer.parseInt(env.getProperty("http.client.port",
                env.getProperty("http.server.port")
                ));
            String path=env.getProperty("servletContext", "/");
            URI uri = new URI("http", null, host, port, path, null, null);
            return uri;
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            throw new RuntimeException("error creating URI:" + ex, ex);
        }
    }
    
    @Bean 
    public URI accountsURI() {
        try {
            return new URI(appURI() + "rest/accounts");
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            throw new RuntimeException("error creating URI:" + ex, ex);
        }
    }

    @Bean 
    public URI dataURI() {
        try {
            return new URI(appURI() + "rest/data");
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            throw new RuntimeException("error creating URI:" + ex, ex);
        }
    }

    @Bean 
    public URI dataSolutionURI() {
        try {
            return new URI(appURI() + "rest/data-solution");
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            throw new RuntimeException("error creating URI:" + ex, ex);
        }
    }
}
