package ejava.examples.restintro.dmv.client;

import java.net.URI;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
    
import ejava.examples.restintro.dmv.dto.DMVRepresentation;
import ejava.examples.restintro.dmv.lic.dto.DrvLicRepresentation;
import ejava.util.rest.Link;
import ejava.util.rest.Representation;

/**
 * This class defines a starting and reference point for interacting with the 
 * DMV licensing process. This class should get injected with a properly 
 * configured HttpClient and bootstrap URI for DMV licenses. The client can
 * start the process by using the CreateApplication provided by this class.
 */
public class ProtocolClient {
    private static final Logger log = LoggerFactory.getLogger(ProtocolClient.class);
    private static final Map<String, Class<? extends Action>> actions = new HashMap<String, Class<? extends Action>>();
    
    static {
        actions.put(DMVRepresentation.SELF_REL, GetDMV.class);
        actions.put(DMVRepresentation.RESID_APP_REL, CreateApplication.class);
        actions.put(DrvLicRepresentation.SELF_REL, GetApplicationAction.class);
        actions.put(DrvLicRepresentation.CANCEL_REL, CancelApplicationAction.class);
        actions.put(DrvLicRepresentation.REJECT_REL, RejectApplicationAction.class);
        actions.put(DrvLicRepresentation.APPROVE_REL, ApproveApplicationAction.class);
        actions.put(DrvLicRepresentation.PAYMENT_REL, PayApplicationAction.class);
        actions.put(DrvLicRepresentation.REFUND_REL, RefundApplicationAction.class);
        actions.put(DrvLicRepresentation.RESID_REL, GetResidentIDAction.class);
    }

    protected @Inject HttpClient httpClient;
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
    
    protected @Inject URI dmvURI;
    public URI getDmvURI() { return dmvURI; }
    public void setDmvURI(URI dmvURI) {
        this.dmvURI = dmvURI;
    }

    protected @Inject URI dmvlicURI;
    public URI getDmvLicenseURI() { return dmvlicURI; }
    public void setDmvLicenseURI(URI dmvlicURI) {
        this.dmvlicURI = dmvlicURI;
    }
    
    public GetDMV getDMV() {
        GetDMV action = new GetDMV();
        action.setHttpClient(httpClient);
        action.setLink(new Link(DMVRepresentation.SELF_REL, dmvURI, DMVRepresentation.DMV_MEDIA_TYPE));
        return action;
    }
    
    /**
     * This method returns an action class that can carry out the provided
     * link if known.
     * @param link
     * @return action object if link is known -- otherwise null
     */
    public Action createAction(Link link) {
        Action action = null;
        Class<? extends Action> clazz = actions.get(link.getRel());
        if (clazz != null) {
            try {
                action = clazz.newInstance();
                action.setHttpClient(httpClient);
                action.setLink(link);
            } catch (InstantiationException ex) {
                ex.printStackTrace();
                throw new RuntimeException("error instantiating action class", ex);
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
        return action;
    }
    
    /**
     * Returns a list of action classes that are valid for the state of the
     * provided representation.
     * @param rep
     * @return
     */
    public List<Action> getActions(Representation rep) {
        if (rep == null) { return null; }
        List<Action> actions = new ArrayList<Action>(); 
        for (Link link : rep.getLinks()) {
            Action action = createAction(link);
            if (action != null) {
                actions.add(action);
            }
            else {
                log.info("rep {} has unknown link {}", rep.getClass(), link);
            }
        }
        return actions;
    }

    /**
     * Returns the requested action type.
     * @param clazz
     * @param rep
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends Action> T getAction(Class<T> clazz, Representation rep) {
        if (rep == null) { return null; }
        for (Action action : getActions(rep)) {
            if (action.getClass() == clazz) {
                return (T) action;
            }            
        }
        return null;
    }

    /**
     * Returns the requested action by relation name
     * @param rel
     * @param rep
     * @return
     */
    public Action getAction(String rel, Representation rep) {
        for (Action action : getActions(rep)) {
            if (action.getLink().getRel().equalsIgnoreCase(rel)) {
                return action;
            }            
        }
        return null;
    }
}
