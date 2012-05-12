package ejava.examples.jaxrsrep.dmv.svc;

import ejava.examples.jaxrsrep.dmv.lic.dto.Photo;

/**
 * This interface defines the methods available for managing photos.
 */
public interface PhotosService {
    Photo createPhoto(Photo photo);
    Photo getPhoto(long id);
}
