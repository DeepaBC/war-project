package ejava.examples.jaxrsscale.dmv.client;

import java.io.IOException;


import javax.xml.bind.JAXBException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import ejava.examples.jaxrsscale.dmv.lic.dto.Application;
import ejava.examples.jaxrsscale.dmv.lic.dto.DrvLicRepresentation;
import ejava.examples.jaxrsscale.dmv.lic.dto.ResidentIDApplication;
import ejava.util.rest.Action;
import ejava.util.rest.HttpResult;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements getting an existing DMV application.
 */
public class GetApplicationAction extends Action {
    protected HttpResult<Application> result;
    
    public Application get() {
        try {
            HttpGet request = new HttpGet(link.getHref());
            request.addHeader("Accept", DrvLicRepresentation.DRVLIC_MEDIA_TYPE);
    
            log.debug("calling {} {}", request.getMethod(), request.getURI());
            HttpResponse response=httpClient.execute(request);

            HttpResult<byte[]> reply = HttpResult.getResult(byte[].class, null, response);
            Application resapp = null;
            if (reply.entity != null) {
                resapp = JAXBHelper.unmarshall(reply.entity, Application.class, null, 
                        ResidentIDApplication.class);
            }
            result = new HttpResult<Application>(reply.status, reply.rawHeaders, resapp, reply.errorMsg);     
            log.debug("received {}", JAXBHelper.toString(result.entity));
            return result.entity; 
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("IO error reading stream", ex);
        } catch (JAXBException ex) {
            ex.printStackTrace();
            throw new RuntimeException("JAXB error demarshalling result", ex);
        } finally {}        
    }

    @Override
    protected HttpResult<?> getResult() {
        return result;
    }
}
