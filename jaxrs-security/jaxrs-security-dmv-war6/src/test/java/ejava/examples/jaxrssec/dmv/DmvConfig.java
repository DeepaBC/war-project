package ejava.examples.jaxrssec.dmv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;


import java.net.URI;
import java.net.URL;

import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.ejb.SessionContext;
import javax.inject.Inject;

import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import ejava.common.test.stub.SessionContextStub;
import ejava.examples.jaxrssec.dmv.client.ProtocolClient;
import ejava.examples.jaxrssec.dmv.rs.ApplicationsRS;
import ejava.examples.jaxrssec.dmv.rs.ApplicationsRSEJB;
import ejava.examples.jaxrssec.dmv.rs.DmvRSEJB;
import ejava.examples.jaxrssec.dmv.rs.PhotosRS;
import ejava.examples.jaxrssec.dmv.rs.ResidentsRS;
import ejava.examples.jaxrssec.dmv.svc.ApplicationsService;
import ejava.examples.jaxrssec.dmv.svc.ApplicationsServiceStub;
import ejava.examples.jaxrssec.dmv.svc.PhotosService;
import ejava.examples.jaxrssec.dmv.svc.PhotosServiceStubEJB;
import ejava.examples.jaxrssec.dmv.svc.ResidentsService;
import ejava.examples.jaxrssec.dmv.svc.ResidentsServiceStubEJB;

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

    @Bean
    public SessionContext sessionContext() {
        SessionContext ctx = new SessionContextStub();
        return ctx;
    }
    
    @Bean
    public DmvRSEJB dmvRS() {
        return new DmvRSEJB();
    }

    @Bean @Singleton
    public ApplicationsService applicationsService() {
        return new ApplicationsServiceStub();
    }
    
    @Bean @Singleton
    public ResidentsService residentsService() {
        return new ResidentsServiceStubEJB();
    }
    
    @Bean @Singleton
    public PhotosService photosService() {
        return new PhotosServiceStubEJB();
    }
    
    //the following beans are used within the Jetty development env and are
    //shared between resteasy and spring
    @Bean @Singleton
    public ApplicationsRS applicationsRS() {
        return new ApplicationsRSEJB();
    }
    
    @Bean @Singleton
    public ResidentsRS residentsRS() {
        return new ResidentsRS();
    }
    
    @Bean @Singleton
    public PhotosRS photosRS() {
        return new PhotosRS();
    }
    
    private class FollowRedirectStrategy extends DefaultRedirectStrategy {
        @Override
        public boolean isRedirected(HttpRequest request,
                HttpResponse response, HttpContext context)
                throws ProtocolException {
            boolean isRedirect=false;
            try {
                isRedirect=super.isRedirected(request, response, context);
            } catch (ProtocolException ex) {
                throw new RuntimeException("ProtocolException durint isRedirected:" 
                        + ex.getLocalizedMessage());
            }
            if (!isRedirect) {
                int status = response.getStatusLine().getStatusCode();
                return (status == 301 || status == 302);
            }
            return false;
        }
    }
    
    public HttpClient createClient(String username, String password) 
            throws KeyStoreException, IOException, GeneralSecurityException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.setRedirectStrategy(new FollowRedirectStrategy());
        
        System.setProperty("https.protocols", "TLSv1");
        String trustStorePath=env.getProperty("javax.net.ssl.trustStore");
        String trustStorePassword=env.getProperty("javax.net.ssl.trustStorePassword");
        
        KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream instream = new FileInputStream(new File(trustStorePath));
        try {
            trustStore.load(instream, trustStorePassword.toCharArray());
        } finally {
            try { instream.close(); } catch (Exception ignore) {}
        }

        SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
        Scheme sch = new Scheme("https", 8443, socketFactory);
        httpClient.getConnectionManager().getSchemeRegistry().register(sch);

        if (username != null) {
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                    new AuthScope(null, -1, "ApplicationRealm"), 
                    new UsernamePasswordCredentials(username, password));
            httpClient.setCredentialsProvider(credsProvider);
        }

        /*
        CacheConfig cacheConfig = new CacheConfig();  
        cacheConfig.setMaxCacheEntries(1000);
        cacheConfig.setMaxObjectSizeBytes(8192);
        HttpClient httpClientCached = new CachingHttpClient(httpClient, cacheConfig);
        return httpClientCached;
        */
        return httpClient;
    }
    
    @Bean @Singleton
    public HttpClient httpClient() throws KeyStoreException, IOException, GeneralSecurityException {
        log.info("creating anonymous HttpClient");
        return createClient(null, null);
    }
    
    
    @Bean @Singleton
    public HttpClient adminClient() throws KeyStoreException, IOException, GeneralSecurityException {        
        log.info("creating admin HttpClient");
        String username = env.getProperty("admin.username", "admin1");
        String password = env.getProperty("admin.password", "password");
        return createClient(username, password);
    }
    
    @Bean @Singleton
    public HttpClient userClient() throws KeyStoreException, IOException, GeneralSecurityException {
        log.info("creating user HttpClient");
        log.info("creating admin HttpClient");
        String username = env.getProperty("user.username", "user1");
        String password = env.getProperty("user.password", "password");
        return createClient(username, password);
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

    @Bean 
    public URI dmvURI() {
        return UriBuilder.fromUri(appURI())
                .path("rest")
                .path(DmvRSEJB.class)
                .build();
    }

    @Bean
    public ProtocolClient dmv() {
        return new ProtocolClient();
    }
}
