package ejava.examples.jaxrsrep.jaxrs;

import java.net.MalformedURLException;

import java.net.URI;
import java.net.URL;

import java.net.URISyntaxException;

import javax.inject.Inject;



import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

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

import ejava.examples.jaxrsrep.handlers.ContentHandlerDemoRS;
import ejava.examples.jaxrsrep.handlers.XMLHandlerDemoRS;

/**
 * This class provides a factory for POJOs used for unit testing.
 */
@Configuration
@PropertySource("classpath:/test.properties")
public class RepresentationsTestConfig {
    protected static final Logger log = LoggerFactory.getLogger(RepresentationsTestConfig.class);
    
    @Inject
    public Environment env;

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean
    public URI appURI() {
        try {
            String host=env.getProperty("host", "localhost");
            int port=Integer.parseInt(env.getProperty("port", "9092"));
            String path=env.getProperty("servletContext", "/");
            URL url=new URL("http", host, port, path);
            log.debug("server URI={}", url.toURI());
            return url.toURI();
        } catch (MalformedURLException ex) {
            throw new RuntimeException("error creating URL:" + ex, ex);
        } catch (URISyntaxException ex) {
            throw new RuntimeException("error creating URI:" + ex, ex);
        }
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
    public URI contentHandlerURI() {
        return UriBuilder.fromUri(appURI())
                         .path(ContentHandlerDemoRS.class)
                         .build();
    }
    
    @Bean 
    public URI xmlHandlerURI() {
        return UriBuilder.fromUri(appURI())
                         .path(XMLHandlerDemoRS.class)
                         .build();
    }
}
