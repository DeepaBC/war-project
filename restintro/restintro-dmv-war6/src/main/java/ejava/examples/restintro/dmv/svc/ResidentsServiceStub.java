package ejava.examples.restintro.dmv.svc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.restintro.dmv.lic.dto.DrvLicRepresentation;
import ejava.examples.restintro.dmv.lic.dto.ResidentID;
import ejava.util.rest.Link;
import ejava.util.rest.Representation;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a stub for the business logic and storage of 
 * residentIDs.
 */
@Singleton
public class ResidentsServiceStub implements ResidentsService {
    private static final Logger log = LoggerFactory.getLogger(ResidentsServiceStub.class);
    private long residentId=new Random().nextInt(1000);
    private Map<Long, ResidentID> residents = new HashMap<Long, ResidentID>();

    private Link createLink(String name) {
        return new Link(name, DrvLicRepresentation.DRVLIC_MEDIA_TYPE);
    }

    @Override
    public ResidentID createResident(ResidentID id) {
        if (id == null) {
            throw new RuntimeException("residentID not provided");
        }
        else if (id.getIdentity() == null) {
            throw new RuntimeException("resident identity not provided");
        }
        id.setId(residentId++);
        id.setUpdated(new Date());
        id.clearLinks();
        id.addLink(createLink(DrvLicRepresentation.SELF_REL));
        if (id.getPhoto() != null) {
            id.addLink(createLink(DrvLicRepresentation.PHOTO_REL));
        }
        id.addLink(createLink(DrvLicRepresentation.CREATE_PHOTO_REL));
        residents.put(id.getId(), id);
        log.debug("created residentId {}", JAXBHelper.toString(id));
        return id;
    }

    @Override
    public ResidentID updateResident(ResidentID update) {
        Representation dbId = residents.get(update.getId());
        if (dbId != null) {
            update.setUpdated(new Date());
            update.clearLinks();
            update.addLink(createLink(DrvLicRepresentation.SELF_REL));
            if (update.getPhoto() != null) {
                update.addLink(createLink(DrvLicRepresentation.PHOTO_REL));
            }
            update.addLink(createLink(DrvLicRepresentation.CREATE_PHOTO_REL));
            residents.put(update.getId(), update);
            return update;
        }
        return null;
    }

    @Override
    public ResidentID getResident(long id) {
        return residents.get(id);
    }

}
