package ejava.examples.restintro.dmv.client;

import java.io.IOException;


import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import ejava.examples.restintro.dmv.lic.dto.DrvLicRepresentation;
import ejava.rs.util.RESTHelper;
import ejava.rs.util.RESTHelper.Result;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a generic get function.
 */
public abstract class GetAction<T> extends Action {
    protected Result<T> result;
    
    public T get() {
        try {
            HttpGet request = new HttpGet(link.getHref());
            request.addHeader("Accept", 
                link.getType()==null ? MediaType.APPLICATION_XML : link.getType());
    
            log.debug("calling {} {}", request.getMethod(), request.getURI());
            HttpResponse response=httpClient.execute(request);

            RESTHelper.Result<byte[]> reply = RESTHelper.getResult(byte[].class, null, response);
            T representation = null;
            if (reply.entity != null) {
                representation = unmarshallResult(reply.entity);
            }
            result = new Result<T>(reply.status, reply.rawHeaders, representation, reply.errorMsg);     
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
    
    protected abstract T unmarshallResult(byte[] resultBytes) throws JAXBException, IOException; 
    
    @Override
    protected Result<?> getResult() {
        return result;
    }
}
