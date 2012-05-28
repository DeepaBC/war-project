package ejava.examples.ejbear6.dmv.svc;

import java.util.Date;



import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbear6.dmv.lic.dto.DrvLicRepresentation;
import ejava.examples.ejbear6.dmv.lic.dto.Photo;
import ejava.util.rest.Link;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a stub for the business logic and storage of 
 * photos.
 */
@Local(PhotosService.class)
@Singleton
public class PhotosServiceStubEJB implements PhotosService {
    private static final Logger log = LoggerFactory.getLogger(PhotosServiceStubEJB.class);
    private long photoId=new Random().nextInt(1000);
    private Map<Long, Photo> photos = new HashMap<Long, Photo>();
    private @Resource SessionContext ctx;

    @PostConstruct
    public void init() {
        log.info("*** PhotoServiceStubEJB ***");
        log.info("ctx={}", ctx);
    }
    
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
