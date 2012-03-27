package ejava.examples.restintro.rest;

import java.io.IOException;
import java.net.URI;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.restintro.dmv.dto.Application;
import ejava.examples.restintro.dmv.dto.Applications;
import ejava.examples.restintro.dmv.dto.ResidentIDApplication;
import ejava.examples.restintro.dmv.svc.ApplicationsService;
import ejava.examples.restintro.dmv.svc.BadArgument;
import ejava.rs.util.RESTHelper;
import ejava.rs.util.RESTHelper.Result;
import ejava.util.xml.JAXBHelper;

/**
 * This class implements a proxy to the ApplicationsService deployed
 * to the server using a CRUD-style Web interface.
 */
public class ApplicationsServiceProxy implements ApplicationsService {
    private static final Logger log = LoggerFactory.getLogger(ApplicationsServiceProxy.class);

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

    @Override
    public Application createApplication(ResidentIDApplication app)
            throws BadArgument {
        URI uri=UriBuilder.fromUri(serviceURI)
                .path("/{implContext}/applications")
                .build(implContext); 
        try {
            String appXML = JAXBHelper.toString(app);
            byte[] result=RESTHelper.postXML(byte[].class, httpClient, uri, 
                    null, null, appXML).entity;
            return JAXBHelper.unmarshall(result, ResidentIDApplication.class, null, 
                    Application.class,
                    ResidentIDApplication.class);
        } catch (JAXBException ex) {
            throw new RuntimeException("JAXBException unmarshalling result", ex);
        } catch (IOException ex) {
            throw new RuntimeException("IOException unmarshalling result", ex);
        }
    }

    @Override
    public Application getApplication(long id) {
        URI uri=UriBuilder.fromUri(serviceURI)
                .path("/{implContext}/applications/{id}")
                .build(implContext, id); 
        try {
            Result<byte[]> result=RESTHelper.getX(byte[].class, httpClient, uri.toString(), 
                    null, null);
            if (result.status >= 200 && result.status <= 299) {
                return JAXBHelper.unmarshall(result.entity, ResidentIDApplication.class, null, 
                        Application.class,
                        ResidentIDApplication.class);
            }
            return null;
        } catch (JAXBException ex) {
            throw new RuntimeException("JAXBException unmarshalling result", ex);
        } catch (IOException ex) {
            throw new RuntimeException("IOException unmarshalling result", ex);
        }
    }

    @Override
    public int updateApplication(Application app) {
        URI uri=UriBuilder.fromUri(serviceURI)
                .path("/{implContext}/applications/{id}")
                .build(implContext, app.getId()); 
        Result<Void> result=RESTHelper.putXML(Void.class, httpClient, uri.toString(), null, app);
        if (result.status >= 400) {
            log.debug("update failed {}:{}", result.status, result.errorMsg);
        }
        return result.status >= 200 && result.status <= 299 ? 0 : -1;
    }

    @Override
    public int deleteApplication(long id) {
        URI uri=UriBuilder.fromUri(serviceURI)
                .path("/{implContext}/applications/{id}")
                .build(implContext, id); 
        Result<Void> result=RESTHelper.deleteX(Void.class, httpClient, uri.toString(), null, null);
        if (result.status >= 400) {
            log.debug("delete failed {}:{}", result.status, result.errorMsg);
        }
        return result.status >= 200 && result.status <= 299 ? 0 : -1;
    }

    @Override
    public Applications getApplications(Boolean active, int start, int count) {
        URI uri=UriBuilder.fromUri(serviceURI)
                .path("/{implContext}/applications")
                .build(implContext); 
        List<NameValuePair> params = RESTHelper.createArgsList();
        RESTHelper.add(params, "active", active);
        RESTHelper.add(params, "start", start);
        RESTHelper.add(params, "count", count);
        byte[] result=RESTHelper.getX(byte[].class, httpClient, uri.toString(), 
                null, null, RESTHelper.toArray(params)).entity;
        try {
            return JAXBHelper.unmarshall(result, Applications.class, null, 
                    Applications.class,
                    Application.class, 
                    ResidentIDApplication.class);
        } catch (JAXBException ex) {
            throw new RuntimeException("JAXBException unmarshalling result", ex);
        } catch (IOException ex) {
            throw new RuntimeException("IOException unmarshalling result", ex);
        }
    }
}
