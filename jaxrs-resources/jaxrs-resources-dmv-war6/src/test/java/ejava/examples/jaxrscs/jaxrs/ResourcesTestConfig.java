package ejava.examples.jaxrscs.jaxrs;

import java.net.URI;

import java.net.URISyntaxException;

import javax.inject.Inject;



import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import ejava.examples.jaxrscs.httpmethod.rs.HttpMethodDemoRS;
import ejava.examples.jaxrscs.httpmethod.rs.HttpResponseDemoRS;

/**
 * This class provides a factory for POJOs used for unit testing.
 */
@Configuration
@PropertySource("classpath:/test.properties")
public class ResourcesTestConfig {
    protected static final Logger log = LoggerFactory.getLogger(ResourcesTestConfig.class);
    
    @Inject
    public Environment env;

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    /**
     * Return the full URI to the base servlet context
     * @return
     */
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

    @Bean @Singleton
    public HttpClient httpClient() {
        log.info("creating non-cached HttpClient");
        HttpClient httpClient = new DefaultHttpClient();
        return httpClient;
    }
    
    @Bean 
    public URI httpMethodsURI() {
        return UriBuilder.fromUri(appURI())
                        .path("rest")
                        .path(HttpMethodDemoRS.class)
                        .build();
    }
    
    @Bean 
    public URI httpResponsesURI() {
        return UriBuilder.fromUri(appURI())
                        .path("rest")
                        .path(HttpResponseDemoRS.class)
                        .build();
    }
}
