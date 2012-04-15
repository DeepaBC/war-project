package ejava.util.rest;

import java.io.IOException;


import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

import ejava.util.xml.JAXBHelper;

/**
 * This class implements the core Put actions for a DMV application.
 */
public abstract class PutAction<T> extends Action {
    private HttpResult<T> result;             
    
    public T put(Object update) {
        try {
            HttpPut request = new HttpPut(link.getHref());
            String protocol = link.getType()==null ? 
                    MediaType.APPLICATION_XML : link.getType();
            request.addHeader("Accept", protocol);
            log.debug("calling {} {}", request.getMethod(), request.getURI());
            if (update != null) {
                String xml = JAXBHelper.toString(update);
                request.addHeader("Content-Type", protocol);
                request.setEntity(new StringEntity(xml, "UTF-8"));
                log.debug(xml.substring(0, Math.min(xml.length(), 1000)));
            }
    
            HttpResponse response=httpClient.execute(request);
            HttpResult<byte[]> reply = HttpResult.getResult(byte[].class, null, response);
            T updated = null;
            if (reply.entity != null) {
                updated = unmarshallResult(reply.entity);
            }
            result = new HttpResult<T>(reply.status, reply.rawHeaders, updated, reply.errorMsg);     
            log.debug("received {}", JAXBHelper.toString(result.entity));
            return result.entity; 
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("IO error reading stream", ex);
        } catch (JAXBException ex) {
            ex.printStackTrace();
            throw new RuntimeException("JAXB error demarshalling result", ex);
        } finally{}        
    }

    protected abstract T unmarshallResult(byte[] resultBytes) throws JAXBException, IOException; 

    @Override
    protected HttpResult<?> getResult() {
        return result;
    }
}
