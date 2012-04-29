package ejava.examples.restintro.dmv.client;

import java.io.IOException;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;

import ejava.examples.restintro.dmv.lic.dto.DrvLicRepresentation;
import ejava.util.rest.Action;
import ejava.util.rest.HttpResult;

/**
 * This class implements the cancellation of a DMV application.
 */
public class CancelApplicationAction extends Action {
    private HttpResult<Void> result;             
    
    public DrvLicRepresentation cancel() {
        try {
            HttpDelete request = new HttpDelete(link.getHref());
            request.addHeader("Accept", DrvLicRepresentation.DRVLIC_MEDIA_TYPE);
    
            log.debug("calling {} {}", request.getMethod(), request.getURI());
            HttpResponse response=httpClient.execute(request);
            result = HttpResult.getResult(Void.class, null, response);
            if (result.status >= 200 && result.status <= 299) {
                return new DrvLicRepresentation(); //no links
            }
            else {
                log.warn(String.format("error calling %s %s, %d:%s",
                        request.getMethod(), link,
                        result.status, result.errorMsg));
                return null;
            }
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
            throw new RuntimeException("State error reading stream", ex);
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
