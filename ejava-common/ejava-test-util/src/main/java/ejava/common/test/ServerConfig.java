package ejava.common.test;

import java.io.File;

import javax.inject.Inject;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.UserRealm;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * This class provides a factory for POJOs used for unit testing.
 */
@Configuration
public class ServerConfig implements DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(ServerConfig.class);
    private static Server jettyServer;
    
    @Inject
    public Environment env;
  
    /**
     * instantiates a server for use with local unit testing.
     * @return
     * @throws Exception
     */
    @Bean
    public Server server() throws Exception {
        if (jettyServer==null) {
            String path=env.getProperty("servletContext", "/");
            int port=Integer.parseInt(env.getProperty("http.server.port", "9000"));
            String resourceBase = env.getProperty("servlet.resourceBase", "src/main/webapp");
            String descriptor = env.getProperty("servlet.descriptor", "src/main/webapp/WEB-INF/web.xml");
            String overrrideDescriptor = env.getProperty("servlet.overrideDescriptor", "src/test/webapp/WEB-INF/web.xml");
            jettyServer = new Server(port);
            WebAppContext context = new WebAppContext();
            context.setResourceBase(resourceBase);
            if (descriptor != null && descriptor.length()>0) {
                context.setDescriptor(descriptor);
            }
            if (overrrideDescriptor != null && overrrideDescriptor.length()>0) {
                context.setOverrideDescriptor(overrrideDescriptor);
            }
            context.setContextPath(path);
            context.setParentLoaderPriority(true);
            jettyServer.setHandler(context);
            
            String realmProperties = env.getProperty("realm.properties", "src/test/resources/jetty/etc/realm.properties");
            if (new File(realmProperties).exists()) {
                log.debug("using real properties={}", new File(realmProperties).getAbsolutePath());
                HashUserRealm myrealm = new HashUserRealm("ApplicationRealm",realmProperties);
                jettyServer.setUserRealms(new UserRealm[]{myrealm});
            } else {
                log.info("new realm properties found");
            }
            
            log.debug("starting jetty server on port {}", port);
            jettyServer.start();
        }
        return jettyServer;
    }

    /**
     * This implements a general shutdown of the application context
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        log.debug("shutting down test resources");
        if (jettyServer != null) {
            if (jettyServer.isStarted()) {
                jettyServer.stop();
            }
            jettyServer.destroy();
            jettyServer=null;
        }
    }
}
