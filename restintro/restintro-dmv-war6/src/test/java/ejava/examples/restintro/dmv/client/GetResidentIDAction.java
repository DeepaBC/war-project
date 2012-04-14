package ejava.examples.restintro.dmv.client;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import ejava.examples.restintro.dmv.lic.dto.ResidentID;
import ejava.util.xml.JAXBHelper;

public class GetResidentIDAction extends GetAction<ResidentID>{
    @Override
    protected ResidentID unmarshallResult(byte[] resultBytes)
            throws JAXBException, IOException {
        return JAXBHelper.unmarshall(resultBytes, ResidentID.class, null, ResidentID.class);
    }

}
