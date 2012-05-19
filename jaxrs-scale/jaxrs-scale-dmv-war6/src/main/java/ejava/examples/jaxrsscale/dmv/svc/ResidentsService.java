package ejava.examples.jaxrsscale.dmv.svc;

import ejava.examples.jaxrsscale.dmv.lic.dto.ResidentID;
import ejava.util.rest.Link;

/**
 * This interface defines the methods available for managing residentIDs.
 */
public interface ResidentsService {
    ResidentID createResident(ResidentID id);
    ResidentID updateResident(ResidentID update);
    ResidentID getResident(long id);
    ResidentID setPhoto(long id, Link photoUri);
}
