package ejava.examples.restintro.rest;

import java.net.URI;


import java.util.List;

import javax.inject.Inject;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.restintro.rest.dto.Resident;
import ejava.examples.restintro.rest.dto.Residents;
import ejava.examples.restintro.rest.resources.ResidentsResource;
import ejava.rs.util.RESTHelper;
import ejava.rs.util.RESTHelper.Result;

/**
 * This class implements a HTTP proxy to test the HelloResource deployed
 * to the server.
 */
public class ResidentsResourceProxy extends ResidentsResource {
	protected static final Logger log = LoggerFactory.getLogger(ResidentsResourceProxy.class);
	protected URI serviceURI;
	protected HttpClient httpClient = new DefaultHttpClient();

	@Inject
    public void setServiceURI(URI serviceURI) {
        this.serviceURI = serviceURI;
    }

    @Override
    public Resident createResident(String firstName, String lastName,
            String street, String city, String state, String zip) {
        String uri = String.format("%s/rest/residents", serviceURI);
        return RESTHelper.postX(Resident.class, httpClient, uri, null, null, 
                new BasicNameValuePair("firstName", firstName),
                new BasicNameValuePair("lastName", lastName),
                new BasicNameValuePair("street", street),
                new BasicNameValuePair("city", city),
                new BasicNameValuePair("state", state),
                new BasicNameValuePair("zip", zip)
                ).entity;
    }

    @Override
    public List<Resident> getResidents(int start, int count) {
        String uri = String.format("%s/rest/residents", serviceURI);
        return RESTHelper.getX(Residents.class, httpClient, uri, null, null,
                new BasicNameValuePair("start", new Integer(start).toString()),
                new BasicNameValuePair("count", new Integer(count).toString())
                ).entity;
    }

    @Override
    public Resident getResident(long id) {
        String uri = String.format("%s/rest/residents/%d", serviceURI, id);
        return RESTHelper.getX(Resident.class, httpClient, uri, null, null).entity;
    }

    @Override
    public void updateResident(Resident resident) {
        String uri = String.format("%s/rest/residents/%d", serviceURI, resident.getId());
        Result<Void> result=RESTHelper.putXML(Void.class, httpClient, uri, null, resident);
        if (result.status >= 400) {
            throw new RuntimeException(
                    String.format("update failed %d:%s", result.status, result.errorMsg));
        }
    }

    @Override
    public int deleteResident(long id) {
        String uri = String.format("%s/rest/residents/%d", serviceURI, id);
        return RESTHelper.deleteX(Integer.class, httpClient, uri, null, null).entity;
    }

    @Override
    public String getResidentNames() {
        String uri = String.format("%s/rest/residents/names", serviceURI);
        return RESTHelper.getX(String.class, httpClient, uri, null, null).entity;
    }

    @Override
    public boolean isSamePerson(long p1, long p2) {
        String uri = String.format("%s/rest/residents/same", serviceURI);
        return RESTHelper.getX(Boolean.class, httpClient, uri, null, null,
                new BasicNameValuePair("p1", new Long(p1).toString()),
                new BasicNameValuePair("p2", new Long(p2).toString())
                ).entity;
    }
	
	
}
