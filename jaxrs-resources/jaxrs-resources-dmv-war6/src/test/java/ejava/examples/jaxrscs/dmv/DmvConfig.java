package ejava.examples.jaxrscs.dmv;

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

import ejava.examples.jaxrscs.dmv.client.ProtocolClient;
import ejava.examples.jaxrscs.dmv.rs.ApplicationsRS;
import ejava.examples.jaxrscs.dmv.rs.DmvRS;
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
        HttpClient httpClient = new DefaultHttpClient();
        return httpClient;
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
    
    /**
     * Return the full URI to the bank REST service
     * @return
     */
    @Bean 
    public URI dmvURI() {
        URI uri = UriBuilder.fromUri(appURI())
                .path("rest")
                .path(DmvRS.class)
                .build();
        return uri;
    }

    /**
     * Return full URI to the applications REST service
     * @return
     */
    @Bean 
    public URI dmvlicURI() {
        URI uri = UriBuilder.fromUri(appURI())
                .path("rest")
                .path(ApplicationsRS.class)
                .build(); 
        return uri;
    }
    
    @Bean
    public ProtocolClient dmv() {
        return new ProtocolClient();
    }
}
