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
import ejava.examples.restintro.rest.resources.ResidentsResource;
import ejava.rs.util.RESTHelper;

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

	public String sayHelloREST(String name) {
        String uri = String.format("%s/rest/hello", serviceURI);
		return RESTHelper.getX(String.class, httpClient, uri, null,
		        new BasicNameValuePair("name", name)).entity;
	}

    @Override
    public Resident createResident(String firstName, String lastName,
            String street, String city, String state, String zip) {
        // TODO Auto-generated method stub
        return super.createResident(firstName, lastName, street, city, state, zip);
    }

    @Override
    public List<Resident> getResidents(int start, int count) {
        // TODO Auto-generated method stub
        return super.getResidents(start, count);
    }

    @Override
    public Resident getResident(long id) {
        String uri = String.format("%s/rest/residents", serviceURI);
        return RESTHelper.getX(Resident.class, httpClient, uri, null,  
                new BasicNameValuePair("id", ""+id)).entity;
    }

    @Override
    public void updateResident(Resident resident) {
        // TODO Auto-generated method stub
        super.updateResident(resident);
    }

    @Override
    public int deleteResident(long id) {
        // TODO Auto-generated method stub
        return super.deleteResident(id);
    }

    @Override
    public String getResidentNames() {
        // TODO Auto-generated method stub
        return super.getResidentNames();
    }

    @Override
    public boolean isSamePerson(long p1, long p2) {
        // TODO Auto-generated method stub
        return super.isSamePerson(p1, p2);
    }
	
	
}
