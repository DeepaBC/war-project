package ejava.examples.jaxrsrep.dmv.client;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import ejava.examples.jaxrsrep.dmv.dto.DMV;
import ejava.util.rest.GetAction;
import ejava.util.xml.JAXBHelper;

public class GetDMV extends GetAction<DMV>{

    @Override
    protected DMV unmarshallResult(byte[] resultBytes) throws JAXBException,
            IOException {
        return JAXBHelper.unmarshall(resultBytes, DMV.class, null, DMV.class);
    }

}
