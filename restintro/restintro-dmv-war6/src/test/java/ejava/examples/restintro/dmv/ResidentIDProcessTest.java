package ejava.examples.restintro.dmv;

import static org.junit.Assert.*;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;



import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ejava.common.test.ServerConfig;
import ejava.examples.restintro.dmv.client.ApproveApplicationAction;
import ejava.examples.restintro.dmv.client.CancelApplicationAction;
import ejava.examples.restintro.dmv.client.CreateApplication;
import ejava.examples.restintro.dmv.client.CreatePhotoAction;
import ejava.examples.restintro.dmv.client.GetApplicationAction;
import ejava.examples.restintro.dmv.client.GetResidentIDAction;
import ejava.examples.restintro.dmv.client.PayApplicationAction;
import ejava.examples.restintro.dmv.client.ProtocolClient;
import ejava.examples.restintro.dmv.client.RefundApplicationAction;
import ejava.examples.restintro.dmv.client.SetPhotoAction;
import ejava.examples.restintro.dmv.client.UpdateResidentIDAction;
import ejava.examples.restintro.dmv.dto.DMV;
import ejava.examples.restintro.dmv.lic.dto.Application;
import ejava.examples.restintro.dmv.lic.dto.ContactInfo;
import ejava.examples.restintro.dmv.lic.dto.ContactType;
import ejava.examples.restintro.dmv.lic.dto.Person;
import ejava.examples.restintro.dmv.lic.dto.DrvLicRepresentation;
import ejava.examples.restintro.dmv.lic.dto.Photo;
import ejava.examples.restintro.dmv.lic.dto.PhysicalDetails;
import ejava.examples.restintro.dmv.lic.dto.PhysicalDetails.HairColor;
import ejava.examples.restintro.dmv.lic.dto.PhysicalDetails.Sex;
import ejava.examples.restintro.dmv.lic.dto.ResidentID;
import ejava.examples.restintro.dmv.lic.dto.ResidentIDApplication;
import ejava.examples.restintro.dmv.lic.dto.PhysicalDetails.EyeColor;
import ejava.examples.restintro.dmv.svc.ApplicationsService;
import ejava.util.rest.Representation;

/**
 * This class implements a local unit test of the ApplicationsService 
 * implementing the residentID application process.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DmvConfig.class, ServerConfig.class})
public class ResidentIDProcessTest {
    protected static final Logger log = LoggerFactory.getLogger(ResidentIDProcessTest.class);

    @Inject protected Environment env;
    @Inject protected ApplicationsService svcImpl;
    @Inject protected ProtocolClient dmv;
    
    @Before
    public void setUp() throws Exception {	
        log.debug("=== ResidentIDProcessTest.setUp() ===");
        log.debug("dmv=" + dmv);
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
        DMV dmvResource = dmv.getDMV().get();
        assertNotNull("unable to locate DMV", dmvResource);
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
    public void testFillInPhysicalDetails() {
        log.info("*** testFillInPhysicalDetails ***");
        
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
        assertEquals("unexpected number of links", 3, residentId.getLinks().size());
        assertNotNull("null self link", residentId.getLink(DrvLicRepresentation.SELF_REL));
        assertNotNull("null photo link", residentId.getLink(DrvLicRepresentation.SET_PHOTO_REL));
        assertNotNull("null createPhoto link", residentId.getLink(DrvLicRepresentation.CREATE_PHOTO_REL));
        
        PhysicalDetails physicalDetails = new PhysicalDetails();
        physicalDetails.setEyeColor(EyeColor.BROWN);
        physicalDetails.setHairColor(HairColor.BROWN);
        physicalDetails.setHeight((5*12)+10);
        physicalDetails.setWeight(185);
        physicalDetails.setSex(Sex.M);
        residentId.setPhysicalDetails(physicalDetails);

        UpdateResidentIDAction updateId=dmv.createAction(
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

    /**
     * This test will verify the ability to add a photo to the resident ID.
     * The resident gets a change to pick their photo.
     * @throws IOException 
     */
    @Test
    public void testAddPhoto() throws IOException {
        log.info("*** testAddPhoto ***");
        
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

            //add the photo
        CreatePhotoAction createPhoto = dmv.getAction(CreatePhotoAction.class, residentId);
        assertNotNull("null createPhoto", createApp);
        Photo photo = new Photo();
        InputStream pictureFile = new FileInputStream("src/test/resources/photos/driver-photo.jpg");
        assertNotNull("null driver photo", pictureFile);
        byte[] image = IOUtils.toByteArray(pictureFile);
        photo.setImage(image);
        Photo createdPhoto = createPhoto.post(photo);
        assertNotNull("null createdPhoto", createdPhoto);
        assertNull("unexpected source photo timestamp", photo.getTimestamp());
        assertNotNull("null created photo timestamp", createdPhoto.getTimestamp());
        
            //relate the photo with the resident
        SetPhotoAction setPhoto = dmv.getAction(SetPhotoAction.class, residentId);
        assertNotNull("null setPhoto", setPhoto);
        Representation rep = setPhoto.put(createdPhoto.getSelf());
        assertNotNull("null photo representation", rep);
    }

    /**
     * This test will verify that the residentID application will be complete
     * once all required information has been supplied.
     * @throws IOException
     */
    @Test
    public void testCompleteResidentID() throws IOException {
        log.info("*** testCompleteResidentID ***");
        
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
        ResidentID residentId = getResidentID.get();
        
            //add physical details
        PhysicalDetails physicalDetails = new PhysicalDetails();
        physicalDetails.setEyeColor(EyeColor.BROWN);
        physicalDetails.setHairColor(HairColor.BROWN);
        physicalDetails.setHeight((5*12)+10);
        physicalDetails.setWeight(185);
        physicalDetails.setSex(Sex.M);
        residentId.setPhysicalDetails(physicalDetails);
        UpdateResidentIDAction updateId=dmv.createAction(UpdateResidentIDAction.class, residentId);
        @SuppressWarnings("unused")
        ResidentID updatedId = updateId.put(residentId);

            //add the photo
        CreatePhotoAction createPhoto = dmv.getAction(CreatePhotoAction.class, residentId);
        Photo photo = new Photo();
        InputStream pictureFile = new FileInputStream("src/test/resources/photos/driver-photo.jpg");
        byte[] image = IOUtils.toByteArray(pictureFile);
        photo.setImage(image);
        photo = createPhoto.post(photo);
        
            //relate the photo with the resident
        SetPhotoAction setPhoto = dmv.getAction(SetPhotoAction.class, residentId);
        setPhoto.put(photo.getSelf());
        
            //verify application is now complete
        GetApplicationAction getApp = dmv.createAction(GetApplicationAction.class, app);
        app = getApp.get();
        assertNotNull("application not completed", app.getCompleted());
        log.info("congrats! your application was completed on {}", app.getCompleted());
    }
}
