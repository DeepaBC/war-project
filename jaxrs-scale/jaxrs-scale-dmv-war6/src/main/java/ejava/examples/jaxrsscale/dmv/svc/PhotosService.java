package ejava.examples.jaxrsscale.dmv.svc;

import ejava.examples.jaxrsscale.dmv.lic.dto.Photo;

/**
 * This interface defines the methods available for managing photos.
 */
public interface PhotosService {
    Photo createPhoto(Photo photo);
    Photo getPhoto(long id);
}
