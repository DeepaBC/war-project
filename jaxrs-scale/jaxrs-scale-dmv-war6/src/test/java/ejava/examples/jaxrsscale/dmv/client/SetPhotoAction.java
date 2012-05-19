package ejava.examples.jaxrsscale.dmv.client;

import java.io.IOException;
import javax.xml.bind.JAXBException;

import ejava.examples.jaxrsscale.dmv.lic.dto.ResidentID;
import ejava.util.rest.PutAction;
import ejava.util.xml.JAXBHelper;

/**
 * This class will change/set the photo for a target resident
 */
public class SetPhotoAction extends PutAction<ResidentID> {
    @Override
    protected ResidentID unmarshallResult(byte[] resultBytes) throws JAXBException,
            IOException {
        return JAXBHelper.unmarshall(resultBytes, ResidentID.class, null, ResidentID.class);
    }

}


