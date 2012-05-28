package ejava.examples.ejbwar6.dmv.svc;

import ejava.examples.ejbwar6.dmv.lic.dto.Photo;

/**
 * This interface defines the methods available for managing photos.
 */
public interface PhotosService {
    Photo createPhoto(Photo photo);
    Photo getPhoto(long id);
}
