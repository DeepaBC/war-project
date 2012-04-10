package ejava.examples.restintro.dmv.client;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import ejava.examples.restintro.dmv.dto.Application;
import ejava.examples.restintro.dmv.dto.Representation;
import ejava.examples.restintro.dmv.dto.ResidentIDApplication;
import ejava.rs.util.RESTHelper;
import ejava.rs.util.RESTHelper.Result;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements an initial action in the DMV licensing workflow 
 * associated with creating an application.
 */
public class CreateApplication extends Action {
    protected RESTHelper.Result<Application> result; 

    public Application createApplication(Application app) {
        try {
            HttpPost request = new HttpPost(link.getHref());
            request.addHeader("Accept", Representation.DMVLIC_MEDIA_TYPE);
            request.addHeader("Content-Type", Representation.DMVLIC_MEDIA_TYPE);
            String appXML = JAXBHelper.toString(app);
            request.setEntity(new StringEntity(appXML, "UTF-8"));
    
            log.debug("calling POST {}\n{}", request.getURI(), appXML);
            HttpResponse response=httpClient.execute(request);
            RESTHelper.Result<byte[]> reply = RESTHelper.getResult(byte[].class, null, response);
            Application resapp = null;
            if (reply.entity != null) {
                resapp = JAXBHelper.unmarshall(reply.entity, Application.class, null, 
                        ResidentIDApplication.class);
            }
            result = new Result<Application>(reply.status, reply.rawHeaders, resapp, reply.errorMsg);     
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
    protected Result<?> getResult() {
        return result;
    }
}
