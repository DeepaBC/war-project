package ejava.ws.other.jersey.rs;

import java.net.URI;
import java.net.URL;

import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(name="test-props", value="classpath:config/test.properties")
//@ImportResource(value="classpath:config/test-config.xml")
public class AppConfig {
    @Inject
    private Environment env;
    
    @Bean
    public String serverProtocol() { return env.getProperty("server.protocol", "http"); }
    
    @Bean
    public String serverHost() { return env.getProperty("server.host", "localhost"); }
    
    @Bean
    public int serverPort() { return env.getProperty("server.port", Integer.class, 8080); }
    
    @Bean
    public String contextPath() { return env.getProperty("contextPath", "/"); }
    
    @Bean
    public URI appURI() {
        try {
            URL url=new URL(serverProtocol(), serverHost(), serverPort(), contextPath());
            return url.toURI();
        } catch (Exception ex) {
            throw new RuntimeException("error creating appURI", ex);
        } finally {}
    }
    
    @Bean
    public URI helloURI() {
        return UriBuilder.fromUri(appURI())
                         .path("rest").build();
    }

    /* easily expressed in XML
    @Bean
    public Client client() {
        Client client = Client.create();
        client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        return client;
    }
    */
}
