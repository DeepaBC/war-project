package ejava.util.rest;

import java.util.Map;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This class is a base class for all actions that act on representations.
 */
//TODO: template this on result type instead of using the protected method
public abstract class Action {
    protected static final Logger log = LoggerFactory.getLogger(Action.class);
    
    protected HttpClient httpClient;
    protected Link link;

    public Action setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public Link getLink() { return link; }
    public Action setLink(Link link) {
        this.link = link;
        return this;
    }

    public int getStatus() {
        HttpResult<?> result = getResult();
        return result == null ? 0 : result.status;
    }
    public String getErrorMsg() {
        HttpResult<?> result = getResult();
        return result == null ? null : result.errorMsg;
    }
    public Map<String, String> getResultHeaders() {
        HttpResult<?> result = getResult();
        return result == null ? null : result.headers;        
    }
    
    protected abstract HttpResult<?> getResult();
}
