package ejava.examples.ejbwar6.rest;

import java.net.URI;

import javax.inject.Inject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.ejbwar6.dmv.ApplicationsServiceTest;
import ejava.examples.ejbwar6.dmv.DmvConfig;

/**
 * This class implements a remote test of the Applications service using a 
 * JAX-RS interface wrapper.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class, DmvRSITConfig.class, DmvHM2ITConfig.class})
public class ApplicationsHM2IT extends ApplicationsServiceTest {    
    protected static Server server;

    protected @Inject URI appURI;
    protected @Inject Environment env;
    
	//used to query application configuration
	protected @Inject ApplicationContext ctx;
	
	@Override
	public void setUp() throws Exception {
        log.debug("=== {}.setUp() ===", getClass().getSimpleName());
		log.info("appURI={}",appURI);
		startServer();
		super.setUp();
	}

    protected void startServer() throws Exception {
        if (appURI.getPort()>=9092) {
            if (server == null) {
                String path = env.getProperty("servletContext", "/");
                server = new Server(9092);
                WebAppContext context = new WebAppContext();
                context.setResourceBase("src/test/resources/local-web");
                context.setContextPath(path);
                context.setParentLoaderPriority(true);
                server.setHandler(context);
            }
            server.start();
        }
    }
    
    @After
    public void tearDown() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
        if (server != null) {
            server.destroy();
            server = null;
        }
    }
    
	//the @Tests are defined in the parent class
}
