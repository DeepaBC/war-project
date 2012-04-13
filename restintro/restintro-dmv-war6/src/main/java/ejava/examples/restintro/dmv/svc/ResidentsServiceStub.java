package ejava.examples.restintro.dmv.svc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.restintro.dmv.dto.Link;
import ejava.examples.restintro.dmv.dto.Representation;
import ejava.examples.restintro.dmv.dto.ResidentID;
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
        id.addLink(new Link(Representation.SELF_REL, null));
        if (id.getPhoto() != null) {
            id.addLink(new Link(Representation.PHOTO_REL, null));
        }
        id.addLink(new Link(Representation.CREATE_PHOTO_REL, null));
        residents.put(id.getId(), id);
        log.debug("created residentId {}", JAXBHelper.toString(id));
        return id;
    }

    @Override
    public ResidentID updateResident(ResidentID update) {
        ResidentID dbId = residents.get(update.getId());
        if (dbId != null) {
            update.setUpdated(new Date());
            update.clearLinks();
            update.addLink(new Link(Representation.SELF_REL, null));
            if (update.getPhoto() != null) {
                update.addLink(new Link(Representation.PHOTO_REL, null));
            }
            update.addLink(new Link(Representation.CREATE_PHOTO_REL, null));
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
