package ejava.examples.restintro.rest;

import static org.junit.Assert.assertEquals;
import java.net.URI;

import javax.inject.Inject;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.restintro.DmvConfig;
import ejava.examples.restintro.dmv.ResidentsServiceTest;
import ejava.examples.restintro.dmv.dto.Person;
import ejava.examples.restintro.dmv.dto.Persons;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a remote test of the Residents service using a 
 * JAX-RS interface wrapper that is more compliant with HTTP protocol.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class, DmvRSITConfig.class})
public class ResidentsRSIT extends ResidentsServiceTest {    
    
	//used to query application configuration
	protected @Inject ApplicationContext ctx;
	
	@Override
	public void setUp() throws Exception {
        log.debug("=== {}.setUp() ===", getClass().getSimpleName());
        URI serviceURI = ctx.getBean("serviceURI", URI.class);
        String implContext = ctx.getBean("implContext", String.class);
		log.info("serviceURI={}/{}",serviceURI,implContext);
		super.setUp();
	}

	//the @Tests are defined in the parent class

	/**
	 * This method extends the base test by adding a verification
	 * of the DTO size, start, and count values.
	 */
    @Override
    public void testGetResidents() {
        super.testGetResidents();
        
        Persons residents = (Persons)svcImpl.getResidents(1, 3);
        log.debug("{}", JAXBHelper.toString(residents));
        assertEquals("unexexpected residents", 3, residents.size());
        assertEquals("unexexpected start", 1, residents.getStart());
        assertEquals("unexexpected count", 3, residents.getCount());
    }
    
    /**
     * This is a test of the HTTP cache that can be enabled thru cache 
     * control headers. This evaluation of this test is not automated.
     * You must visually evaluate whether the cache is being used by the
     * presence of calls to the server. For the simple JAX-RS service,
     * nothing will be cached since no caching headers are being used. For 
     * the HTTP service -- the cache is used because there is more attention
     * given to supplying proper caching headers.
     */
    @Test
    public void testCacheHTTP() {
        log.info("*** testCache ***");
        URI serviceURI = ctx.getBean("serviceURI", URI.class);
        String implContext = ctx.getBean("implContext", String.class);

        CacheConfig cacheConfig = new CacheConfig();  
        cacheConfig.setMaxCacheEntries(1000);
        cacheConfig.setMaxObjectSizeBytes(8192);
        HttpClient httpClient = new DefaultHttpClient();
        HttpClient cachingClient = new CachingHttpClient(httpClient, cacheConfig);
        
        ResidentsServiceProxy proxy = new ResidentsServiceProxy();
        proxy.setServiceURI(serviceURI);
        proxy.setImplContext(implContext);  
        log.info("creating resident without cache");
        
        /* Create a Breakpoint Here and prepare to view the following */
        Person r=proxy.createResident(new Person("john", "doe"));
        for (int i=0; i<5; i++) {
            log.info("getting resident without cache", proxy.getResidentById(r.getId()));
        }
        proxy.setHttpClient(cachingClient);
        
        /* Create a 2nd Breakpoint Here and prepare to view the following */
        Person r2=proxy.createResident(new Person("john2", "doe2"));
        for (int i=0; i<5; i++) {
            log.info("getting resident with cache", proxy.getResidentById(r2.getId()));
        }
    }
    
}
