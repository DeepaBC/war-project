package ejava.examples.jaxrssec.dmv.client;

import java.io.IOException;
import javax.xml.bind.JAXBException;

import ejava.examples.jaxrssec.dmv.lic.dto.ResidentID;
import ejava.util.rest.PutAction;
import ejava.util.rest.Representation;
import ejava.util.xml.JAXBHelper;

/**
 * This class will change/set the photo for a target resident
 */
public class SetPhotoAction extends PutAction<Representation> {
    @Override
    protected Representation unmarshallResult(byte[] resultBytes) throws JAXBException,
            IOException {
        return JAXBHelper.unmarshall(resultBytes, Representation.class, null, 
                ResidentID.class);
    }
}
