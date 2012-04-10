package ejava.examples.restintro.dmv;

import static org.junit.Assert.*;

import java.util.Date;


import javax.inject.Inject;

import org.jboss.resteasy.spi.NotFoundException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.restintro.DmvConfig;
import ejava.examples.restintro.dmv.client.ApproveApplicationAction;
import ejava.examples.restintro.dmv.client.CreateApplication;
import ejava.examples.restintro.dmv.client.ProtocolClient;
import ejava.examples.restintro.dmv.dto.Application;
import ejava.examples.restintro.dmv.dto.Applications;
import ejava.examples.restintro.dmv.dto.ContactInfo;
import ejava.examples.restintro.dmv.dto.ContactType;
import ejava.examples.restintro.dmv.dto.Person;
import ejava.examples.restintro.dmv.dto.Representation;
import ejava.examples.restintro.dmv.dto.ResidentIDApplication;
import ejava.examples.restintro.dmv.svc.ApplicationsService;
import ejava.examples.restintro.dmv.svc.BadArgument;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a local unit test of the ApplicationsService 
 * implementing the residentID application process.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class})
public class ResidentIDProcessTest {
	protected static final Logger log = LoggerFactory.getLogger(ResidentIDProcessTest.class);
	
    @Inject
    protected ApplicationsService svcImpl;

	@Inject
	protected ProtocolClient dmvlic;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
        Server server = new Server(9092);
        WebAppContext context = new WebAppContext();
        context.setResourceBase("src/test/resources/local-web");
        context.setContextPath("/");
        context.setParentLoaderPriority(true);
        server.setHandler(context);
        server.start();
	}
	
	@Before
	public void setUp() throws Exception {	
	    log.debug("=== ResidentIDProcessTest.setUp() ===");
        log.debug("dmvlic=" + dmvlic);
        cleanup();
	}
	
	protected void cleanup() {
	    svcImpl.purgeApplications();
	}
	
	protected ResidentIDApplication makeApplication() {
        Person person = new Person();
        person.setFirstName("cat");
        person.setLastName("inhat");
        ContactInfo info = new ContactInfo();
        info.setType(ContactType.RESIDENCE);
        info.setStreet("1600 Penn Ave, NW");
        info.setCity("Washington");
        info.setState("DC");
        info.setZip("20500");
        person.getContactInfo().add(info);
        return new ResidentIDApplication().setIdentity(person);
	}
	
	/**
	 * This test verifies that an application can be created.
	 * @throws Exception 
	 */
	@Test
	public void testCreateApplication() throws Exception {
		log.info("*** testCreateApplication ***");
		    //gather information for the resident application
        ResidentIDApplication resapp = makeApplication();

            //locate the bootstrap action to start the resident ID process
		CreateApplication createApp = dmvlic.createApplication();
		    //initiate the process
		Application app = createApp.createApplication(resapp);
		assertNotNull("null application", app);		
		assertEquals("unexpected number of links", 4, app.getLinks().size());
		assertNotNull("null self link", app.getLink(Representation.SELF_REL));
        assertNotNull("null cancel link", app.getLink(Representation.CANCEL_REL));
        assertNotNull("null reject link", app.getLink(Representation.REJECT_REL));
        assertNotNull("null approve link", app.getLink(Representation.APPROVE_REL));
	}
	
	/**
	 * This test verifies the application can be approved.
	 */
	@Test
	public void testApproveApplication() {
	    log.info("*** testApproveApplication ***");
	    
        ResidentIDApplication resapp = makeApplication();
        CreateApplication createApp = dmvlic.createApplication();
        Application app = createApp.createApplication(resapp);
        
        ApproveApplicationAction approval = dmvlic.getAction(ApproveApplicationAction.class, app);
        assertNotNull("null approval", approval);
        
        Application approvedApp = approval.approve();
        assertNotNull("null approvedApp", approvedApp);
        assertEquals("unexpected number of links", 3, approvedApp.getLinks().size());
        assertNotNull("null self link", approvedApp.getLink(Representation.SELF_REL));
        assertNotNull("null cancel link", approvedApp.getLink(Representation.CANCEL_REL));
        assertNotNull("null payment link", approvedApp.getLink(Representation.PAYMENT_REL));
	}
	
}
