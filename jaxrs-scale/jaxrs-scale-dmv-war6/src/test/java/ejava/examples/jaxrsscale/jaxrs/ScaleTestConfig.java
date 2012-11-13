package ejava.examples.jaxrsscale.jaxrs;

import java.net.URI;

import java.net.URISyntaxException;

import javax.inject.Inject;



import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import ejava.examples.jaxrsscale.caching.CachingRS;
import ejava.examples.jaxrsscale.concurrency.ConcurrentRS;
import ejava.examples.jaxrsscale.concurrency.ConcurrentService;
import ejava.examples.jaxrsscale.concurrency.ConcurrentServiceImpl;

/**
 * This class provides a factory for POJOs used for unit testing.
 */
@Configuration
@PropertySource("classpath:/test.properties")
public class ScaleTestConfig {
    protected static final Logger log = LoggerFactory.getLogger(ScaleTestConfig.class);
    
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
        HttpClient httpClient = new DefaultHttpClient();

        log.info("creating cached HttpClient");
        CacheConfig cacheConfig = new CacheConfig();  
        cacheConfig.setMaxCacheEntries(1000);
        cacheConfig.setMaxObjectSizeBytes(8192);
        HttpClient httpClientCached = new CachingHttpClient(httpClient, cacheConfig);
        
        return httpClientCached;
    }
    
    @Bean 
    public URI cachingURI() {
        return UriBuilder.fromUri(appURI())
                         .path("rest")
                         .path(CachingRS.class)
                         .build();
    }

    @Bean 
    public URI concurrencyURI() {
        return UriBuilder.fromUri(appURI())
                         .path("rest")
                         .path(ConcurrentRS.class)
                         .build();
    }
    
    @Bean @Singleton
    public CachingRS cashingRS() {
        return new CachingRS();
    }
    
    @Bean
    public ConcurrentRS concurrentRS() {
        return new ConcurrentRS();
    }
    
    @Bean @Singleton
    public ConcurrentService concurrentService() {
        return new ConcurrentServiceImpl();
    }
}
