package ejava.examples.restintro.rest;

import java.net.URI;



import java.util.List;

import javax.inject.Inject;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.restintro.rest.dto.ContactInfo;
import ejava.examples.restintro.rest.dto.Resident;
import ejava.examples.restintro.rest.dto.Residents;
import ejava.examples.restintro.svc.DMVService;
import ejava.rs.util.RESTHelper;
import ejava.rs.util.RESTHelper.Result;

/**
 * This class implements a HTTP proxy to test the HelloResource deployed
 * to the server.
 */
public class ResidentsResourceProxy implements DMVService {
	protected static final Logger log = LoggerFactory.getLogger(ResidentsResourceProxy.class);
	
	protected HttpClient httpClient = new DefaultHttpClient();
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
    protected @Inject URI serviceURI;
    public void setServiceURI(URI serviceURI) {
        this.serviceURI = serviceURI;
    }
    protected @Inject String implContext;
	public void setImplContext(String implContext) {
        this.implContext = implContext;
    }

    /**
	 * This helper function allows derived classes to validate result-specific
	 * responses.
	 * @param result
	 * @return
	 */
	protected <T> Result<T> doCheckCreateResult(Result<T> result) {
	    return result;
	}
    protected <T> Result<T> doCheckGetResult(Result<T> result) {
        return result;
    }
    protected <T> Result<T> doCheckPutResult(Result<T> result) {
        return result;
    }
    protected <T> Result<T> doCheckDeleteResult(Result<T> result) {
        return result;
    }

    @Override
    public Resident createResident(Resident resident) {
        String uri = String.format("%s/rest/%s/residents", serviceURI,implContext);
        List<NameValuePair> params = RESTHelper.createArgsList();
        RESTHelper.add(params, "firstName", resident.getFirstName());
        RESTHelper.add(params, "lastName", resident.getLastName());
        if (resident.getContactInfo().size() > 0) {
            ContactInfo info = resident.getContactInfo().get(0);
            RESTHelper.add(params, "street", info.getStreet());
            RESTHelper.add(params, "city", info.getCity());
            RESTHelper.add(params, "state", info.getState());
            RESTHelper.add(params, "zip", info.getZip());
        }
        return doCheckCreateResult(
                RESTHelper.postX(Resident.class, httpClient, uri, 
                null, null, RESTHelper.toArray(params))).entity;
    }

    @Override
    public List<Resident> getResidents() {
        // internal method -- no need to implement
        return null;
    }

    @Override
    public List<Resident> getResidents(int start, int count) {
        String uri = String.format("%s/rest/%s/residents", serviceURI,implContext);
        return doCheckGetResult(
            RESTHelper.getX(Residents.class, httpClient, uri, null, null,
                new BasicNameValuePair("start", new Integer(start).toString()),
                new BasicNameValuePair("count", new Integer(count).toString())
                )).entity;
    }

    @Override
    public Resident getResidentById(long id) {
        String uri = String.format("%s/rest/%s/residents/%d", serviceURI, implContext, id);
        return RESTHelper.getX(Resident.class, httpClient, uri, null, null).entity;
    }

    @Override
    public boolean updateResident(Resident resident) {
        String uri = String.format("%s/rest/%s/residents/%d", serviceURI, implContext, resident.getId());
        Result<Void> result=doCheckPutResult(
                RESTHelper.putXML(Void.class, httpClient, uri, null, resident));
        if (result.status >= 400) {
            log.debug("update failed {}:{}", result.status, result.errorMsg);
        }
        return result.status >= 200 && result.status <= 299;
    }

    @Override
    public int deleteResident(long id) {
        String uri = String.format("%s/rest/%s/residents/%d", serviceURI,implContext, id);
        return doCheckDeleteResult(
            RESTHelper.deleteX(Integer.class, httpClient, uri, null, null)).entity;
    }

    @Override
    public String getResidentNames() {
        String uri = String.format("%s/rest/%s/residents/names", serviceURI,implContext);
        return doCheckGetResult(
            RESTHelper.getX(String.class, httpClient, uri, null, null)).entity;
    }

    @Override
    public boolean isSamePerson(long p1, long p2) {
        String uri = String.format("%s/rest/%s/residents/same", serviceURI,implContext);
        return doCheckGetResult(
            RESTHelper.getX(Boolean.class, httpClient, uri, null, null,
                new BasicNameValuePair("p1", new Long(p1).toString()),
                new BasicNameValuePair("p2", new Long(p2).toString())
                )).entity;
    }

}
