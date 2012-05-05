package ejava.examples.jaxrscs.dmv.client;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import ejava.examples.jaxrscs.dmv.lic.dto.Photo;
import ejava.util.rest.PostAction;
import ejava.util.xml.JAXBHelper;

/**
 * This class will create a new photo.
 */
public class CreatePhotoAction extends PostAction<Photo> {
    @Override
    protected Photo unmarshallResult(byte[] resultBytes) throws JAXBException,
            IOException {
        return JAXBHelper.unmarshall(resultBytes, Photo.class, null, Photo.class);
    }

}
