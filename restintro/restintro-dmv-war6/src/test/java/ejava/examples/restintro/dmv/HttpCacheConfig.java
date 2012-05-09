package ejava.examples.restintro.dmv;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Add this configuration to your environment to add caching for URI resources.
 */
@Configuration
@PropertySource("classpath:/test.properties")
public class HttpCacheConfig {
    protected static final Logger log = LoggerFactory.getLogger(HttpCacheConfig.class);

    @Inject
    public Environment env;
    
    @Bean @Singleton
    public HttpClient httpClient() {
        log.info("creating cached HttpClient");
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
        
        CacheConfig cacheConfig = new CacheConfig();  
        cacheConfig.setMaxCacheEntries(1000);
        cacheConfig.setMaxObjectSizeBytes(8192);
        HttpClient httpClientCached = new CachingHttpClient(httpClient, cacheConfig);
        
        return httpClientCached;
    }
}
