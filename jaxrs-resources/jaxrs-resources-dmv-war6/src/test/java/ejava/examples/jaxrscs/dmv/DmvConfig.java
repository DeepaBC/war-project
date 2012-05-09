package ejava.examples.jaxrscs.dmv;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

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

import ejava.examples.jaxrscs.dmv.client.ProtocolClient;
import ejava.examples.jaxrscs.dmv.rs.ApplicationsRS;
import ejava.examples.jaxrscs.dmv.rs.PhotosRS;
import ejava.examples.jaxrscs.dmv.rs.ResidentsRS;
import ejava.examples.jaxrscs.dmv.svc.ApplicationsService;
import ejava.examples.jaxrscs.dmv.svc.ApplicationsServiceStub;
import ejava.examples.jaxrscs.dmv.svc.PhotosService;
import ejava.examples.jaxrscs.dmv.svc.PhotosServiceStub;
import ejava.examples.jaxrscs.dmv.svc.ResidentsService;
import ejava.examples.jaxrscs.dmv.svc.ResidentsServiceStub;

/**
 * This class provides a factory for POJOs used for unit testing.
 */
@Configuration
@PropertySource("classpath:/test.properties")
public class DmvConfig {
    protected static final Logger log = LoggerFactory.getLogger(DmvConfig.class);
    
    @Inject
    public Environment env;

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean @Singleton
    public ApplicationsService applicationsService() {
        return new ApplicationsServiceStub();
    }
    
    @Bean @Singleton
    public ResidentsService residentsService() {
        return new ResidentsServiceStub();
    }
    
    @Bean @Singleton
    public PhotosService photosService() {
        return new PhotosServiceStub();
    }
    
    //the following beans are used within the Jetty development env and are
    //shared between resteasy and spring
    @Bean @Singleton
    public ApplicationsRS applicationsRS() {
        return new ApplicationsRS();
    }
    
    @Bean @Singleton
    public ResidentsRS residentsRS() {
        return new ResidentsRS();
    }
    
    @Bean @Singleton
    public PhotosRS photosRS() {
        return new PhotosRS();
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
    public URI dmvURI() {
        try {
            String host=env.getProperty("host", "localhost");
            int port=Integer.parseInt(env.getProperty("port", "9092"));
            String path=env.getProperty("servletContext", "/");
            URL url=new URL("http", host, port, path + "/dmv");
            log.debug("server URI={}", url.toURI());
            return url.toURI();
        } catch (MalformedURLException ex) {
            throw new RuntimeException("error creating URL:" + ex, ex);
        } catch (URISyntaxException ex) {
            throw new RuntimeException("error creating URI:" + ex, ex);
        }
    }

    @Bean
    public ProtocolClient dmv() {
        return new ProtocolClient();
    }
}
