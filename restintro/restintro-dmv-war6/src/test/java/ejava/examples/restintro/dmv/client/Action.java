package ejava.examples.restintro.dmv.client;

import java.util.Map;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.restintro.dmv.dto.Link;
import ejava.rs.util.RESTHelper;

/**
 * This class is a base class for all actions that act on representations.
 */
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
        RESTHelper.Result<?> result = getResult();
        return result == null ? 0 : result.status;
    }
    public Map<String, String> getResultHeaders() {
        RESTHelper.Result<?> result = getResult();
        return result == null ? null : result.headers;        
    }
    
    protected abstract RESTHelper.Result<?> getResult();
}
