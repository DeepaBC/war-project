package ejava.examples.jaxrscs.dmv.svc;

import java.util.Date;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.jaxrscs.dmv.lic.dto.DrvLicRepresentation;
import ejava.examples.jaxrscs.dmv.lic.dto.Photo;
import ejava.util.rest.Link;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a stub for the business logic and storage of 
 * photos.
 */
@Singleton
public class PhotosServiceStub implements PhotosService {
    private static final Logger log = LoggerFactory.getLogger(PhotosServiceStub.class);
    private long photoId=new Random().nextInt(1000);
    private Map<Long, Photo> photos = new HashMap<Long, Photo>();

    private Link createLink(String name) {
        return new Link(name, DrvLicRepresentation.DRVLIC_MEDIA_TYPE);
    }

    @Override
    public Photo createPhoto(Photo photo) {
        if (photo == null) {
            throw new RuntimeException("photo not provided");
        }
        photo.setId(photoId++);
        photo.setTimestamp(new Date());
        photo.clearLinks();
        photo.addLink(createLink(DrvLicRepresentation.SELF_REL));
        photo.addLink(createLink(DrvLicRepresentation.CREATE_PHOTO_REL));
        photos.put(photo.getId(), photo);
        log.debug("created photo {}", JAXBHelper.toString(photo));
        return photo;
    }

    @Override
    public Photo getPhoto(long id) {
        return photos.get(id);
    }

}
