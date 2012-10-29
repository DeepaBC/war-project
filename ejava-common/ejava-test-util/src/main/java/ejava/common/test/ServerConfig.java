package ejava.common.test;

import javax.inject.Inject;


import org.mortbay.jetty.Server;
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
    protected static final Logger log = LoggerFactory.getLogger(ServerConfig.class);
    private Server jettyServer;
    
    @Inject
    public Environment env;
  
    /**
     * instantiates a server for use with local unit testing.
     * @return
     * @throws Exception
     */
    @Bean
    public Server server() throws Exception {
        String path=env.getProperty("servletContext", "/");
        int port=Integer.parseInt(env.getProperty("http.server.port", "9000"));
        jettyServer = new Server(port);
        WebAppContext context = new WebAppContext();
        /*
        context.setResourceBase("src/test/resources/local-web");
        */
        context.setResourceBase("src/main/webapp");
        context.setOverrideDescriptor("src/test/webapp/WEB-INF/web.xml");
        context.setContextPath(path);
        context.setParentLoaderPriority(true);
        jettyServer.setHandler(context);
        log.debug("starting jetty server on port {}", port);
        jettyServer.start();
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
