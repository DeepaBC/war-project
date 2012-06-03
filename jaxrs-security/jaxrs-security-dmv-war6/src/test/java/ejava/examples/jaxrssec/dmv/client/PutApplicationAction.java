package ejava.examples.jaxrssec.dmv.client;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import ejava.examples.jaxrssec.dmv.lic.dto.Application;
import ejava.examples.jaxrssec.dmv.lic.dto.ResidentIDApplication;
import ejava.util.rest.PutAction;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements the core Put actions for a DMV application.
 */
public abstract class PutApplicationAction extends PutAction<Application> {
    @Override
    protected Application unmarshallResult(byte[] resultBytes)
            throws JAXBException, IOException {
        return JAXBHelper.unmarshall(resultBytes, Application.class, null, 
                ResidentIDApplication.class);
    }
}
