package ejava.examples.restintro.rest;

import ejava.examples.restintro.dmv.dto.Application;
import ejava.examples.restintro.dmv.dto.Applications;
import ejava.examples.restintro.dmv.dto.ResidentIDApplication;
import ejava.examples.restintro.dmv.svc.ApplicationsService;
import ejava.examples.restintro.dmv.svc.BadArgument;

public class ApplicationsServiceProxy implements ApplicationsService {

    @Override
    public Application createApplication(ResidentIDApplication app)
            throws BadArgument {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Application getApplication(long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int updateApplication(Application app) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int deleteApplication(long id) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Applications getApplications(Boolean active, int start, int count) {
        // TODO Auto-generated method stub
        return null;
    }

}
