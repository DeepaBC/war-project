package ejava.examples.restintro.dmv;

import javax.inject.Singleton;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Add this configuration to your environment to add caching for URI resources.
 */
@Configuration
@PropertySource("classpath:/test.properties")
public class HttpCacheConfig {
    protected static final Logger log = LoggerFactory.getLogger(HttpCacheConfig.class);
    
    @Bean @Singleton
    public HttpClient httpClient() {
        log.info("creating cached HttpClient");
        HttpClient httpClient = new DefaultHttpClient();
        
        CacheConfig cacheConfig = new CacheConfig();  
        cacheConfig.setMaxCacheEntries(1000);
        cacheConfig.setMaxObjectSizeBytes(8192);
        HttpClient httpClientCached = new CachingHttpClient(httpClient, cacheConfig);
        
        return httpClientCached;
    }
}
