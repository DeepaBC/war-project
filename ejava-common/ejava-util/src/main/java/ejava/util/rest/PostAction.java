package ejava.util.rest;

import java.io.IOException;


import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import ejava.util.xml.JAXBHelper;

/**
 * This class implements a reusable POST/create action.
 *
 * @param <T>
 */
public abstract class PostAction<T> extends Action {
    protected HttpResult<T> result; 

    public T post(T rep) {
        try {
            HttpPost request = new HttpPost(link.getHref());
            String protocol = link.getType()==null ? MediaType.APPLICATION_XML : link.getType();
            
            request.addHeader("Accept", protocol);
            request.addHeader("Content-Type", protocol);
            String xml = JAXBHelper.toString(rep);
            request.setEntity(new StringEntity(xml, "UTF-8"));
    
            log.debug("calling POST {}\n{}", request.getURI(), xml.substring(0, Math.min(xml.length(), 1000)));
            HttpResponse response=httpClient.execute(request);
            HttpResult<byte[]> reply = HttpResult.getResult(byte[].class, null, response);
            T resrep = null;
            if (reply.entity != null) {
                resrep = unmarshallResult(reply.entity);
            }
            result = new HttpResult<T>(reply.status, reply.rawHeaders, resrep, reply.errorMsg);     
            log.debug("received {}", JAXBHelper.toString(result.entity, 1000));
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
    protected HttpResult<?> getResult() {
        return result;
    }

}
