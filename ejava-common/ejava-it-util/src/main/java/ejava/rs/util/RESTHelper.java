package ejava.rs.util;

import java.io.IOException;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.util.xml.JAXBHelper;

/**
 * This class provides helper methods to turn the HttpClient library into 
 * an ready-to-use raw REST client.
 */
public class RESTHelper {
    private static final Logger log = LoggerFactory.getLogger(RESTHelper.class);
    
    
	/**
	 * This helper function will return a new args list.
	 * @return
	 */
	public static List<NameValuePair> createArgsList() {
		return new ArrayList<NameValuePair>();
	}
	
	/**
	 * This helper function will add the key/value to the args if the value
	 * of the key is non-null.
	 * @param args
	 * @param key
	 * @param value
	 */
	public static void add(List<NameValuePair> args, String key, Object value) {
		if (value != null) {
			args.add(new BasicNameValuePair(key, value.toString()));
		}
	}	
	
	public static class Result<T> {
	    public int status;
	    public T entity;
	    public Result(int status, T entity) {
	        this.status = status;
	        this.entity = entity;
	    }
	}
	
	/**
	 * This helper function will perform a GET to the provided URI with
	 * provided query args.
	 * @param clazz
	 * @param httpClient
	 * @param uri
	 * @return
	 * @throws IOException
	 * @throws JAXBException 
	 * @throws URISyntaxException 
	 */
	@SuppressWarnings("unchecked")
	public static <T> Result<T> get(
	        Class<T> clazz, HttpClient httpClient, URI uri, 
	        Schema schema, NameValuePair...params) 
			throws IOException, JAXBException, URISyntaxException {
	    uri = getURI(uri, params);
	    
        //make the service call
        HttpGet request = new HttpGet(uri);
        log.debug(String.format("calling GET %s",uri));
        HttpResponse response=httpClient.execute(request);
        return getResult(clazz, schema, response);
	}
	
    public static <T> Result<T> post(
            Class<T> clazz, HttpClient httpClient, URI uri, 
            Schema schema, Header headers[], NameValuePair...params) 
            throws IOException, JAXBException, URISyntaxException {
        
        //make the service call        
        HttpPost request = new HttpPost(uri);
        if (headers != null) {
            request.setHeaders(headers);
        }
        request.setEntity(new UrlEncodedFormEntity(Arrays.asList(params)));
        log.debug(String.format("calling POST %s",uri));
        HttpResponse response=httpClient.execute(request);
        return getResult(clazz, schema, response);
    }

    
    /**
	 * This helper function will form a URI with the optional args 
	 * URLEncoded into the query string.
	 * @param uri
	 * @param args
	 * @return
	 * @throws URISyntaxException
	 */
	protected static final URI getURI(URI uri, NameValuePair...args) 
	        throws URISyntaxException {
        return URIUtils.createURI(
                uri.getScheme(), 
                uri.getHost(), 
                uri.getPort(), 
                uri.getPath(),
                URLEncodedUtils.format(Arrays.asList(args),"UTF-8"),
                null);	    
	}
	
	/**
	 * This helper function will return a Result based on the HttpResponse. 
	 * @param clazz
	 * @param status
	 * @param schema
	 * @param is
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws JAXBException 
	 */
	@SuppressWarnings("unchecked")
    protected static final <T> Result<T> getResult(
	        Class<T> clazz, Schema schema, HttpResponse response) 
	        throws IllegalStateException, IOException, JAXBException {
	    int status = response.getStatusLine().getStatusCode();
        log.debug(String.format("http.result=%d", status));
	    InputStream is = response.getEntity().getContent();
	    try {
	        if (status >= 400) {
	            return new Result<T>(status, null);
	        }
            if (clazz.equals(String.class)) {
                return new Result<T>(status, (T) IOUtils.toString(is));
            }
            else if (clazz.equals(byte[].class)) {
                return new Result<T>(status, (T) IOUtils.toByteArray(is));
            }
            else {
                return new Result<T>(status, JAXBHelper.unmarshall(is, clazz, schema, clazz));
            }
	    } finally {
	        is.close();
	    }
	}

	/**
	 * This helper method defines a wrapper around the core get() method but
	 * replaces all checked exceptions thrown with a RuntimeException.
	 * @param clazz
	 * @param httpClient
	 * @param uri
	 * @param schema
	 * @param params
	 * @return
	 */
	public static final <T> Result<T> getX(
            Class<T> clazz, HttpClient httpClient, String uri, 
            Schema schema, NameValuePair...params) {
        try {
            return get(clazz, httpClient, new URI(uri), schema, params);
        } catch (IOException ex) {
            throw new RuntimeException("IOException:" + ex.getLocalizedMessage(), ex);
        } catch (JAXBException ex) {
            throw new RuntimeException("JAXBException:" + ex.getLocalizedMessage(), ex);
        } catch (URISyntaxException ex) {
            throw new RuntimeException("URISyntaxException:" + ex.getLocalizedMessage(), ex);
        } finally {}
    }

    public static final <T> Result<T> postX(Class<T> clazz, HttpClient httpClient,
            String uri, Schema schema, Header headers[], NameValuePair...params) {
        try {
            return post(clazz, httpClient, new URI(uri), schema, headers, params);
        } catch (IOException ex) {
            throw new RuntimeException("IOException:" + ex.getLocalizedMessage(), ex);
        } catch (JAXBException ex) {
            throw new RuntimeException("JAXBException:" + ex.getLocalizedMessage(), ex);
        } catch (URISyntaxException ex) {
            throw new RuntimeException("URISyntaxException:" + ex.getLocalizedMessage(), ex);
        } finally {}
    }

}
