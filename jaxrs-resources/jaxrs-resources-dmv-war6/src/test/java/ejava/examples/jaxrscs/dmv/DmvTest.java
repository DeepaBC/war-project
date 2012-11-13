package ejava.examples.jaxrscs.dmv;

import static org.junit.Assert.*;




import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.common.test.ServerConfig;
import ejava.examples.jaxrscs.dmv.client.GetDMV;
import ejava.examples.jaxrscs.dmv.client.ProtocolClient;
import ejava.examples.jaxrscs.dmv.dto.DMV;
import ejava.examples.jaxrscs.dmv.dto.DmvRepresentation;

/**
 * This class implements a local unit test of the DMV. It verifies that the
 * DMV posts information and hyperlinks necessary for services to be accessed.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class, ServerConfig.class})
public class DmvTest {
	protected static final Logger log = LoggerFactory.getLogger(DmvTest.class);
	
	@Inject protected Environment env;
	@Inject protected ProtocolClient dmv;
	
	/**
	 * This test verifies that we can access the bootstrap DMV resource.
	 * @throws Exception 
	 */
	@Test
	public void testGetDMV() throws Exception {
		log.info("*** testGetDMV ***");
		
		    //get the action to access the DMV
		GetDMV getDMV = dmv.getDMV();
		DMV dmvResource = getDMV.get();

		    //verify the result
		assertNotNull("null application", dmvResource);		
		assertEquals("unexpected number of links", 2, dmvResource.getLinks().size());
		assertNotNull("null self link", dmvResource.getLink(DmvRepresentation.SELF_REL));
        assertNotNull("null cancel link", dmvResource.getLink(DmvRepresentation.RESID_APP_REL));
	}
}
