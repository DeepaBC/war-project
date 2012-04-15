package ejava.examples.restintro.dmv.svc;

import ejava.examples.restintro.dmv.lic.dto.Photo;

/**
 * This interface defines the methods available for managing photos.
 */
public interface PhotosService {
    Photo createPhoto(Photo photo);
    Photo getPhoto(long id);
}
