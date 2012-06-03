package ejava.examples.jaxrssec.dmv.svc;

import ejava.examples.jaxrssec.dmv.lic.dto.Application;
import ejava.examples.jaxrssec.dmv.lic.dto.Applications;
import ejava.examples.jaxrssec.dmv.lic.dto.ResidentIDApplication;

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
    int approve(long id);
    int payment(long id);
    int refund(long id);
}
