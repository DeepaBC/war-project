package ejava.examples.ejbear6.rest;

import java.io.IOException;
import java.net.URI;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbear6.dmv.lic.dto.Application;
import ejava.examples.ejbear6.dmv.lic.dto.Applications;
import ejava.examples.ejbear6.dmv.lic.dto.DrvLicRepresentation;
import ejava.examples.ejbear6.dmv.lic.dto.ResidentIDApplication;
import ejava.examples.ejbear6.dmv.svc.ApplicationsService;
import ejava.examples.ejbear6.dmv.svc.BadArgument;
import ejava.rs.util.RESTHelper;
import ejava.util.rest.HttpResult;
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
    protected @Inject String protocol;
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public Application createApplication(ResidentIDApplication app)
            throws BadArgument {
        URI uri=UriBuilder.fromUri(serviceURI)
                .path("/applications")
                .build(); 
        try {
            String appXML = JAXBHelper.toString(app);
            Header[] headers = new Header[] {
                    new BasicHeader("Content-Type", "application/xml"),
                    new BasicHeader("Accept", protocol)
            };
            HttpResult<byte[]> result=RESTHelper.postXML(byte[].class, httpClient, uri, 
                    null, headers, appXML);
            if (result.status == 201) {
                Application createdApp = JAXBHelper.unmarshall(
                        result.entity, ResidentIDApplication.class, null, 
                        DrvLicRepresentation.class,
                        Application.class,
                        ResidentIDApplication.class);
                log.debug("created:{}", JAXBHelper.toString(createdApp));
                return createdApp;
            }
            else if (result.status == Status.BAD_REQUEST.getStatusCode()) {
                throw new BadArgument(result.errorMsg);
            }
            else {
                throw new RuntimeException(new String(result.errorMsg));
            }
        } catch (JAXBException ex) {
            throw new RuntimeException("JAXBException unmarshalling result", ex);
        } catch (IOException ex) {
            throw new RuntimeException("IOException unmarshalling result", ex);
        }
    }

    @Override
    public Application getApplication(long id) {
        URI uri=UriBuilder.fromUri(serviceURI)
                .path("/applications/{id}")
                .build(id); 
        try {
            Header headers[] = new Header[] {
                    new BasicHeader("Accept", protocol)
            };
            HttpResult<byte[]> result=RESTHelper.getX(byte[].class, httpClient, uri.toString(), 
                    null, headers);
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
                .path("/applications/{id}")
                .build(app.getId()); 
        HttpResult<Void> result=RESTHelper.putXML(Void.class, httpClient, uri.toString(), null, app);
        if (result.status >= 400) {
            log.debug("update failed {}:{}", result.status, result.errorMsg);
        }
        if (result.status >= 200 && result.status <= 299) {
            return 0;
        }
        else if (result.status == 404) {
            return -1;
        }
        else if (result.status == 409) {
            return 1;
        }
        return  -99;
    }

    @Override
    public int deleteApplication(long id) {
        URI uri=UriBuilder.fromUri(serviceURI)
                .path("/applications/{id}")
                .build(id); 
        HttpResult<Void> result=RESTHelper.deleteX(Void.class, httpClient, uri.toString(), null, null);
        if (result.status >= 400) {
            log.debug("delete failed {}:{}", result.status, result.errorMsg);
        }
        if (result.status >= 200 && result.status <= 299) {
            return 0;
        }
        else if (result.status == 405) {
            return 1;
        }
        return -1;
    }

    @Override
    public void purgeApplications() {
        URI uri=UriBuilder.fromUri(serviceURI)
                .path("/applications")
                .build(); 
        log.debug("calling DELETE/purge...");
        HttpResult<Void> result=RESTHelper.deleteX(Void.class, httpClient, uri.toString(), null, null);
        log.debug("...returned from DELETE/purge");
        if (result.status >= 400) {
            log.debug("purge failed {}:{}", result.status, result.errorMsg);
        }
    }

    @Override
    public Applications getApplications(Boolean active, int start, int count) {
        URI uri=UriBuilder.fromUri(serviceURI)
                .path("/applications")
                .build(); 
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

    @Override
    public int approve(long id) {
        //not needed
        return 0;
    }
    
    @Override
    public int payment(long id) {
        //not needed
        return 0;
    }

    @Override
    public int refund(long id) {
        //not needed
        return 0;
    }
}
