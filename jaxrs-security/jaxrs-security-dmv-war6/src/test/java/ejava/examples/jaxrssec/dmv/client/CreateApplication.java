package ejava.examples.jaxrssec.dmv.client;

import java.io.IOException;

import static org.junit.Assert.*;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import ejava.examples.jaxrssec.dmv.lic.dto.Application;
import ejava.examples.jaxrssec.dmv.lic.dto.DrvLicRepresentation;
import ejava.examples.jaxrssec.dmv.lic.dto.ResidentIDApplication;
import ejava.util.rest.Action;
import ejava.util.rest.HttpResult;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements an initial action in the DMV licensing workflow 
 * associated with creating an application.
 */
public class CreateApplication extends Action {
    protected HttpResult<Application> result; 

    public Application createApplication(Application app) {
        try {
            HttpPost request = new HttpPost(link.getHref());
            request.addHeader("Accept", DrvLicRepresentation.DRVLIC_MEDIA_TYPE);
            request.addHeader("Content-Type", DrvLicRepresentation.DRVLIC_MEDIA_TYPE);
            String appXML = JAXBHelper.toString(app);
            request.setEntity(new StringEntity(appXML, "UTF-8"));
    
            log.debug("calling POST {}\n{}", request.getURI(), appXML);
            HttpResponse response=httpClient.execute(request);
            try {
                assertEquals(String.format("unexpected error %d: %s", 
                        response.getStatusLine().getStatusCode(),response.getEntity()),
                        Response.Status.CREATED.getStatusCode(),
                        response.getStatusLine().getStatusCode());
                HttpResult<byte[]> reply = HttpResult.getResult(byte[].class, null, response);
                Application resapp = null;
                if (reply.entity != null) {
                    resapp = JAXBHelper.unmarshall(reply.entity, Application.class, null, 
                            ResidentIDApplication.class);
                }
                result = new HttpResult<Application>(reply.status, reply.rawHeaders, resapp, reply.errorMsg);     
                log.debug("received {}", JAXBHelper.toString(result.entity));
                return result.entity;
            } finally {
                EntityUtils.consume(response.getEntity());
            }
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
