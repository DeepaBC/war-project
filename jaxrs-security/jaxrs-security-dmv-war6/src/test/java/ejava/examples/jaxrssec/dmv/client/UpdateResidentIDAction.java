package ejava.examples.jaxrssec.dmv.client;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import ejava.examples.jaxrssec.dmv.lic.dto.ResidentID;
import ejava.util.rest.PutAction;
import ejava.util.xml.JAXBHelper;

/**
 * This action updates a residentID.
 */
public class UpdateResidentIDAction extends PutAction<ResidentID>{
    @Override
    protected ResidentID unmarshallResult(byte[] resultBytes)
            throws JAXBException, IOException {
        return JAXBHelper.unmarshall(resultBytes, ResidentID.class, null, ResidentID.class);
    }
}
