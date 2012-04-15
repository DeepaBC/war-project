package ejava.examples.restintro.dmv;

import static org.junit.Assert.*;



import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.examples.restintro.dmv.client.ApproveApplicationAction;
import ejava.examples.restintro.dmv.client.CancelApplicationAction;
import ejava.examples.restintro.dmv.client.CreateApplication;
import ejava.examples.restintro.dmv.client.GetApplicationAction;
import ejava.examples.restintro.dmv.client.GetResidentIDAction;
import ejava.examples.restintro.dmv.client.PayApplicationAction;
import ejava.examples.restintro.dmv.client.ProtocolClient;
import ejava.examples.restintro.dmv.client.RefundApplicationAction;
import ejava.examples.restintro.dmv.client.UpdateResidentIDAction;
import ejava.examples.restintro.dmv.dto.DMV;
import ejava.examples.restintro.dmv.lic.dto.Application;
import ejava.examples.restintro.dmv.lic.dto.ContactInfo;
import ejava.examples.restintro.dmv.lic.dto.ContactType;
import ejava.examples.restintro.dmv.lic.dto.Person;
import ejava.examples.restintro.dmv.lic.dto.DrvLicRepresentation;
import ejava.examples.restintro.dmv.lic.dto.PhysicalDetails;
import ejava.examples.restintro.dmv.lic.dto.PhysicalDetails.HairColor;
import ejava.examples.restintro.dmv.lic.dto.PhysicalDetails.Sex;
import ejava.examples.restintro.dmv.lic.dto.ResidentID;
import ejava.examples.restintro.dmv.lic.dto.ResidentIDApplication;
import ejava.examples.restintro.dmv.lic.dto.PhysicalDetails.EyeColor;
import ejava.examples.restintro.dmv.svc.ApplicationsService;
import ejava.util.rest.GetAction;
import ejava.util.rest.Link;
import ejava.util.rest.PutAction;

/**
 * This class implements a local unit test of the ApplicationsService 
 * implementing the residentID application process.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class})
public class ResidentIDProcessTest {
	protected static final Logger log = LoggerFactory.getLogger(ResidentIDProcessTest.class);
	protected static Server server;
	
	@Inject 
	protected Environment env;
	
    @Inject
    protected ApplicationsService svcImpl;

	@Inject
	protected ProtocolClient dmv;
	
	@Before
	public void setUp() throws Exception {	
	    log.debug("=== ResidentIDProcessTest.setUp() ===");
        log.debug("dmv=" + dmv);
        startServer();
        cleanup();
	}
	
	protected void startServer() throws Exception {
	    if (dmv.getDmvLicenseURI().getPort()>=9092) {
	        if (server == null) {
	            String path=env.getProperty("servletContext", "/");
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
        DMV dmvResource = dmv.getDMV().get();
		CreateApplication createApp = dmv.getAction(CreateApplication.class, dmvResource);
		
		    //initiate the process
		Application app = createApp.createApplication(resapp);
		assertNotNull("null application", app);		
		assertEquals("unexpected number of links", 4, app.getLinks().size());
		assertNotNull("null self link", app.getLink(DrvLicRepresentation.SELF_REL));
        assertNotNull("null cancel link", app.getLink(DrvLicRepresentation.CANCEL_REL));
        assertNotNull("null reject link", app.getLink(DrvLicRepresentation.REJECT_REL));
        assertNotNull("null approve link", app.getLink(DrvLicRepresentation.APPROVE_REL));
	}
	
	@Test
	public void testBadApplication() throws Exception {
        log.info("*** testBadApplication ***");
            //issue an empty application
        ResidentIDApplication resapp = new ResidentIDApplication();
    
            //locate the bootstrap action to start the resident ID process
        DMV dmvResource = dmv.getDMV().get();
        CreateApplication createApp = dmv.getAction(CreateApplication.class, dmvResource);
            //initiate the process
        Application app = createApp.createApplication(resapp);
        assertNull("null application", app);     
        log.info("received expected failure {}:{}", createApp.getStatus(), createApp.getErrorMsg());
        assertEquals("unexpected status", Response.Status.BAD_REQUEST.getStatusCode(), createApp.getStatus());
	}
	
	/**
	 * This test verifies the application can be approved.
	 */
	@Test
	public void testApproveApplication() {
	    log.info("*** testApproveApplication ***");
	    
        ResidentIDApplication resapp = makeApplication();
        DMV dmvResource = dmv.getDMV().get();
        CreateApplication createApp = dmv.getAction(CreateApplication.class, dmvResource);
        Application app = createApp.createApplication(resapp);
        
        ApproveApplicationAction approval = dmv.getAction(ApproveApplicationAction.class, app);
        assertNotNull("null approval", approval);
        
        Application approvedApp = approval.approve();
        assertNotNull("null approvedApp", approvedApp);
        assertNotNull("null approval date", approvedApp.getApproved());
        assertEquals("unexpected number of links", 3, approvedApp.getLinks().size());
        assertNotNull("null self link", approvedApp.getLink(DrvLicRepresentation.SELF_REL));
        assertNotNull("null cancel link", approvedApp.getLink(DrvLicRepresentation.CANCEL_REL));
        assertNotNull("null payment link", approvedApp.getLink(DrvLicRepresentation.PAYMENT_REL));
	}
	
	/**
	 * This test will verify one can make a payment for an application
	 */
    @Test
    public void testPayApplication() {
        log.info("*** testPayApplication ***");
        
            //create the application
        ResidentIDApplication resapp = makeApplication();
        DMV dmvResource = dmv.getDMV().get();
        CreateApplication createApp = dmv.getAction(CreateApplication.class, dmvResource);
        Application app = createApp.createApplication(resapp);
        
            //approve the application
        ApproveApplicationAction approval = dmv.getAction(ApproveApplicationAction.class, app);
        assertNotNull("null approval", approval);
        Application approvedApp = approval.approve();
        
            //pay for the application
        PayApplicationAction payment = dmv.getAction(PayApplicationAction.class, approvedApp);
        assertNotNull("null payment", payment);
        Application paidApp = payment.payment();
        
            //verify result
        assertNotNull("null paidApp", paidApp);        
        assertNotNull("null payment date", paidApp.getPayment());
        assertEquals("unexpected number of links", 3, paidApp.getLinks().size());
        assertNotNull("null self link", paidApp.getLink(DrvLicRepresentation.SELF_REL));
        assertNotNull("null refund link", paidApp.getLink(DrvLicRepresentation.REFUND_REL));
        assertNotNull("null resid link", paidApp.getLink(DrvLicRepresentation.RESID_REL));
    }
    
    /**
     * This method will test the processing of a bad pay application request
     */
    @Test
    public void testBadPayApplication() {
        log.info("*** testBadPayApplication ***");
        
            //create the application
        ResidentIDApplication resapp = makeApplication();
        DMV dmvResource = dmv.getDMV().get();
        CreateApplication createApp = dmv.getAction(CreateApplication.class, dmvResource);
        Application app = createApp.createApplication(resapp);
        ApproveApplicationAction approval = dmv.getAction(ApproveApplicationAction.class, app);
        CancelApplicationAction cancel = dmv.getAction(CancelApplicationAction.class, app);
        
            //approve the application
        Application approvedApp = approval.approve();
        assertEquals("unexpected approval status", Response.Status.OK.getStatusCode(), approval.getStatus());
        
            //grab the payment action
        PayApplicationAction payment = dmv.getAction(PayApplicationAction.class, approvedApp);
        
            //cancel before paying
        cancel.cancel();
        assertEquals("unexpected cancel status", Response.Status.NO_CONTENT.getStatusCode(), cancel.getStatus());
        
            ///attempt to make a payment
        Application paidApp = payment.payment();
        assertNull("non-null paidApp", paidApp);
        assertEquals("unexpected payment status", Response.Status.NOT_FOUND.getStatusCode(), payment.getStatus());
        log.info("received expected error {}: {}", payment.getStatus(), payment.getErrorMsg());
    }

    /**
     * This method will test the ability to refund an application.
     */
    @Test
    public void testRefundApplication() {
        log.info("*** testRefundApplication ***");
        
            //create the application
        ResidentIDApplication resapp = makeApplication();
        DMV dmvResource = dmv.getDMV().get();
        CreateApplication createApp = dmv.getAction(CreateApplication.class, dmvResource);
        Application app = createApp.createApplication(resapp);
        
            //approve the application
        ApproveApplicationAction approval = dmv.getAction(ApproveApplicationAction.class, app);
        assertNotNull("null approval", approval);
        Application approvedApp = approval.approve();
        
            //pay for the application
        PayApplicationAction payment = dmv.getAction(PayApplicationAction.class, approvedApp);
        assertNotNull("null payment", payment);
        Application paidApp = payment.payment();
        
            //refund the application
        RefundApplicationAction refund = dmv.getAction(RefundApplicationAction.class, paidApp);
        Application refundedApp = refund.refund();
        
            //verify result
        assertNotNull("null refundedApp", refundedApp);        
        assertNull("non-null payment date", refundedApp.getPayment());
        assertEquals("unexpected number of links", 3, refundedApp.getLinks().size());
        assertNotNull("null self link", refundedApp.getLink(DrvLicRepresentation.SELF_REL));
        assertNotNull("null cancel link", refundedApp.getLink(DrvLicRepresentation.CANCEL_REL));
        assertNotNull("null refund link", refundedApp.getLink(DrvLicRepresentation.PAYMENT_REL));
    }
    
    /**
     * This test verifies the behavior for refunding an unpaid application.
     */
    @Test
    public void testBadRefundApplication() {
        log.info("*** testBadRefundApplication ***");
            
            //create the application
        ResidentIDApplication resapp = makeApplication();
        DMV dmvResource = dmv.getDMV().get();
        CreateApplication createApp = dmv.getAction(CreateApplication.class, dmvResource);
        Application app = createApp.createApplication(resapp);
        GetApplicationAction getApp = dmv.getAction(GetApplicationAction.class, app);
        
            //approve the application
        ApproveApplicationAction approval = dmv.getAction(ApproveApplicationAction.class, app);
        Application approvedApp = approval.approve();
        
            //pay for the application
        PayApplicationAction payment = dmv.getAction(PayApplicationAction.class, approvedApp);
        Application paidApp = payment.payment();
        
            //refund the application
        RefundApplicationAction refund = dmv.getAction(RefundApplicationAction.class, paidApp);
        refund.refund();
        assertEquals("unexpected valid refund status", Response.Status.OK.getStatusCode(), refund.getStatus());
        
            //attempt to refund a second time
        refund.refund();
        assertEquals("unexpected invalid refund status", 405, refund.getStatus());
        
            //verify result
        app = getApp.get();
        assertNotNull("null app", app);        
        assertNull("non-null payment date", app.getPayment());
        assertEquals("unexpected number of links", 3, app.getLinks().size());
        assertNotNull("null self link", app.getLink(DrvLicRepresentation.SELF_REL));
        assertNotNull("null cancel link", app.getLink(DrvLicRepresentation.CANCEL_REL));
        assertNotNull("null refund link", app.getLink(DrvLicRepresentation.PAYMENT_REL));
    }
	
    /**
     * This test will verify the ability to fill in the resident identity
     * details associated with the application.
     */
    @Test
    public void testFillInDetails() {
        log.info("*** testFillInDetails ***");
        
            //create the application
        ResidentIDApplication resapp = makeApplication();
        DMV dmvResource = dmv.getDMV().get();
        CreateApplication createApp = dmv.getAction(CreateApplication.class, dmvResource);
        Application app = createApp.createApplication(resapp);
            //approve the application
        ApproveApplicationAction approval = dmv.getAction(ApproveApplicationAction.class, app);
        Application approvedApp = approval.approve();
        
            //pay for the application
        PayApplicationAction payment = dmv.getAction(PayApplicationAction.class, approvedApp);
        Application paidApp = payment.payment();
        
            //get the resident ID
        GetResidentIDAction getResidentID = dmv.getAction(GetResidentIDAction.class, paidApp);
        assertNotNull(getResidentID);
        ResidentID residentId = getResidentID.get();
        
            //verify result
        assertNotNull("null residentId", residentId);        
        assertNotNull("null identity", residentId.getIdentity());
        assertEquals("unexpected firstName", 
                resapp.getIdentity().getFirstName(), 
                residentId.getIdentity().getFirstName());
        assertEquals("unexpected lastName", 
                resapp.getIdentity().getLastName(), 
                residentId.getIdentity().getLastName());
        assertEquals("unexpected number of links", 2, residentId.getLinks().size());
        assertNotNull("null self link", residentId.getLink(DrvLicRepresentation.SELF_REL));
        assertNotNull("null createPhoto link", residentId.getLink(DrvLicRepresentation.CREATE_PHOTO_REL));
        
        PhysicalDetails physicalDetails = new PhysicalDetails();
        physicalDetails.setEyeColor(EyeColor.BROWN);
        physicalDetails.setHairColor(HairColor.BROWN);
        physicalDetails.setHeight((5*12)+10);
        physicalDetails.setWeight(185);
        physicalDetails.setSex(Sex.M);
        residentId.setPhysicalDetails(physicalDetails);

        UpdateResidentIDAction updateId=dmv.getUpdateAction(
                UpdateResidentIDAction.class, 
                residentId);
        assertNotNull("null updateId action", updateId);
        ResidentID updatedId = updateId.put(residentId);
        
            //verify update
        assertNotNull("null updated residentID", updatedId);
        assertEquals("unexpected eye color", 
                physicalDetails.getEyeColor(), 
                updatedId.getPhysicalDetails().getEyeColor());
        assertEquals("unexpected weight", 
                physicalDetails.getWeight(), 
                updatedId.getPhysicalDetails().getWeight());
    }
    
}
