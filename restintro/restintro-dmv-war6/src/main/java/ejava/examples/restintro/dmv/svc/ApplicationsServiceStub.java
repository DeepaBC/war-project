package ejava.examples.restintro.dmv.svc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.restintro.dmv.dto.Application;
import ejava.examples.restintro.dmv.dto.Applications;
import ejava.examples.restintro.dmv.dto.ResidentIDApplication;
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

    @Override
    public Application createApplication(ResidentIDApplication app) throws BadArgument {
        if (app == null) {
            throw new RuntimeException("application not provided");
        }
        else if (app.getIdentity() == null) {
            throw new BadArgument("identity missing");
        }
        else if (app.getIdentity().getFirstName() == null ||
                app.getIdentity().getLastName() == null) {
            throw new BadArgument("missing first or last name");
        }
        else {
            app.setId(applicationId++);
            app.setCreated(new Date());
            app.setUpdated(app.getCreated());
            applications.put(app.getId(), app);
            log.debug("creating resident ID app {}:{}", app.getId(), app);
            return app;
        }
    }

    @Override
    public Application getApplication(long id) {
        Application app = applications.get(id);
        log.debug("getting application {}:{}", id, app);
        return app;
    }

    @Override
    public int updateApplication(Application app) {
        Application dbApp = applications.get(app.getId());
        if (dbApp != null) {
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
        Application dbApp = applications.remove(id);
        log.debug("deleted application {}:{}", id, dbApp);
        return dbApp != null ? 0 : -1;
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
}
