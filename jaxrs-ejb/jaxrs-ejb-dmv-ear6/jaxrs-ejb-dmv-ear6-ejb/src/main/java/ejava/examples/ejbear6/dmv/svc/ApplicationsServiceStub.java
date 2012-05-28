package ejava.examples.ejbear6.dmv.svc;

import java.util.ArrayList;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbear6.dmv.lic.dto.Application;
import ejava.examples.ejbear6.dmv.lic.dto.Applications;
import ejava.examples.ejbear6.dmv.lic.dto.DrvLicRepresentation;
import ejava.examples.ejbear6.dmv.lic.dto.ResidentID;
import ejava.examples.ejbear6.dmv.lic.dto.ResidentIDApplication;
import ejava.util.rest.Link;
import ejava.util.xml.JAXBHelper;

/**
 * This class provides a functional, in-memory implementation of the 
 * application service interface.
 */
@Singleton
public class ApplicationsServiceStub implements ApplicationsService {
    private static final Logger log = LoggerFactory.getLogger(ApplicationsServiceStub.class);
    private long applicationId=new Random().nextInt(100);
    private Map<Long, Application> applications = new HashMap<Long, Application>();
    private @Inject ResidentsService residents;
    
    private Link createLink(String name) {
        return new Link(name, DrvLicRepresentation.DRVLIC_MEDIA_TYPE);
    }

    @Override
    public Application createApplication(ResidentIDApplication app) throws BadArgument {
        if (app == null) {
            throw new RuntimeException("application not provided");
        }
        else if (app.getIdentity() == null) {
            throw new BadArgument("identity missing");
        }
        else if (app.getIdentity().getFirstName() == null ||
                app.getIdentity().getLastName() == null ||
                app.getIdentity().getFirstName().length() == 0 ||
                app.getIdentity().getLastName().length() == 0) {
            throw new BadArgument("missing first or last name");
        }
            //allow integration test to reliably cause a server error
        else if (app.getIdentity().getFirstName().equals("throw") &&
                app.getIdentity().getLastName().equals("500")) {
            throw new RuntimeException("server error completing request");
        }
        else {
            app.setId(applicationId++);
            app.setCreated(new Date());
            app.setUpdated(app.getCreated());
            applications.put(app.getId(), app);
            log.debug("creating resident ID app {}:{}", app.getId(), app);
            
            //add next state links
            app.clearLinks();
            app.addLink(createLink(DrvLicRepresentation.SELF_REL));
            app.addLink(createLink(DrvLicRepresentation.CANCEL_REL));
            app.addLink(createLink(DrvLicRepresentation.REJECT_REL));
            app.addLink(createLink(DrvLicRepresentation.APPROVE_REL));
            return app;
        }
    }

    @Override
    public Application getApplication(long id) {
        Application app = applications.get(id);        
        log.debug("getting application {}:{}", id, app);
        if (app instanceof ResidentIDApplication) {
            ResidentID resid = ((ResidentIDApplication)app).getResid();
            if (resid != null) {
                resid = residents.getResident(resid.getId());
            }
            if (resid == null || !resid.isReady()) {
                app.setCompleted(null);
            }
            else if (app.getCompleted() == null) {
                app.setCompleted(new Date());
                app.setUpdated(app.getCompleted());
            }
        }
        return app;
    }

    @Override
    public int updateApplication(Application app) {
        Application dbApp = applications.get(app.getId());
        if (dbApp != null) {
            if (dbApp.getCompleted() != null) {
                log.debug("requested update conflicted with completed state:{}", 
                        JAXBHelper.toString(dbApp));
                return 1;
            }
            app.setUpdated(new Date());
            log.debug("replacing:{}", JAXBHelper.toString(dbApp));
            log.debug("with:{}", JAXBHelper.toString(app));
            applications.put(app.getId(), app);
            return 0;
        }
        else {
            return -1;
        }
    }

    @Override
    public int deleteApplication(long id) {        
        Application dbApp = applications.get(id);
        if (dbApp != null) {
            if (dbApp.getCompleted() != null) {
                log.debug("cannot delete completed application {}:{}", id, dbApp);
                return 1;
            }
            log.debug("deleted application {}:{}", id, dbApp);
            applications.remove(id);
            dbApp.clearLinks();
            dbApp.addLink(createLink(DrvLicRepresentation.SELF_REL));
            return 0;
        }
        else {
            log.debug("cannot find application {}", id);
            return -1;
        }
    }
    
    
    @Override
    public void purgeApplications() {
        log.info("purging all applications");
        applications.clear();
    }

    @Override
    public Applications getApplications(Boolean active, int start, int count) {
        Applications page = new Applications();
        List<Application> apps = new ArrayList<Application>();
        apps.addAll(applications.values());
        //conditionally pay attention to start and count if >0
        for (int i=0; i<apps.size()&&(count<=0||page.size()<count); i++) {
            if (start <= 0 || i >= start && //factor in paging
                    (active == null ||      //don't care whether active 
                         (active && apps.get(i).getCompleted() != null))) {
                page.add(apps.get(i));
            }
        }
        log.debug(String.format("returning applications (start=%d, count=%d)=%d",
                start, count, page.size()));
        return page;
    }

    @Override
    public int approve(long id) {
        Application dbApp = applications.get(id);
        if (dbApp != null) {
            if (dbApp.getCompleted() != null) {
                log.debug("cannot approve completed application {}:{}", id, dbApp);
                return 1;
            }
            log.debug("approving application {}:{}", id, dbApp);
            dbApp.setApproved(new Date());
            dbApp.setUpdated(dbApp.getApproved());
            
            dbApp.clearLinks();
            dbApp.addLink(createLink(DrvLicRepresentation.SELF_REL));
            dbApp.addLink(createLink(DrvLicRepresentation.CANCEL_REL));
            dbApp.addLink(createLink(DrvLicRepresentation.PAYMENT_REL));
            return 0;
        }
        else {
            log.debug("cannot find application {}", id);
            return -1;
        }
    }

    @Override
    public int payment(long id) {
        Application dbApp = applications.get(id);
        if (dbApp != null) {
            if (dbApp.getCompleted() != null) {
                log.debug("cannot pay completed application {}:{}", id, dbApp);
                return 1;
            }
            else if (dbApp.getPayment() != null) {
                log.debug("cannot re-pay paid application {}:{}", id, dbApp);
                return 2;
            }
            log.debug("paying application {}:{}", id, dbApp);
            dbApp.setPayment(new Date());
            dbApp.setUpdated(dbApp.getPayment());
            
            dbApp.clearLinks();
            dbApp.addLink(createLink(DrvLicRepresentation.SELF_REL));
            dbApp.addLink(createLink(DrvLicRepresentation.REFUND_REL));

            if (dbApp instanceof ResidentIDApplication) {
                    //create a ResidentID to be filled out
                ResidentIDApplication resapp = (ResidentIDApplication)dbApp;
                ResidentID resid = new ResidentID();
                resid.setIdentity(resapp.getIdentity());
                resapp.setResid(residents.createResident(resid));
                dbApp.addLink(createLink(DrvLicRepresentation.RESID_REL));
            }
            return 0;
        }
        else {
            log.debug("cannot find application {}", id);
            return -1;
        }
    }

    @Override
    public int refund(long id) {
        Application dbApp = applications.get(id);
        if (dbApp != null) {
            if (dbApp.getCompleted() != null) {
                log.debug("cannot refund completed application {}:{}", id, dbApp);
                return 1;
            }
            else if (dbApp.getPayment() == null) {
                log.debug("cannot refund unpaid application {}:{}", id, dbApp);
                return 2;
            }
            log.debug("refunding application {}:{}", id, dbApp);
            dbApp.setPayment(null);
            dbApp.setUpdated(new Date());

            dbApp.clearLinks();
            dbApp.addLink(createLink(DrvLicRepresentation.SELF_REL));
            dbApp.addLink(createLink(DrvLicRepresentation.CANCEL_REL));
            dbApp.addLink(createLink(DrvLicRepresentation.PAYMENT_REL));
            return 0;
        }
        else {
            log.debug("cannot find application {}", id);
            return -1;
        }
    }
}
