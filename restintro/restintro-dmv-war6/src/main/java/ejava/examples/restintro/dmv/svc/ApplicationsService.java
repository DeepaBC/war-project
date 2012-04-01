package ejava.examples.restintro.dmv.svc;

import ejava.examples.restintro.dmv.dto.Application;
import ejava.examples.restintro.dmv.dto.Applications;
import ejava.examples.restintro.dmv.dto.ResidentIDApplication;

/**
 * This interface defines the methods our business tier can perform on
 * an application.
 */
public interface ApplicationsService {
    Application createApplication(ResidentIDApplication app) throws BadArgument;
    Application getApplication(long id);
    int updateApplication(Application app);
    int deleteApplication(long id);
    void purgeApplications();
    Applications getApplications(Boolean active, int start, int count);
}
