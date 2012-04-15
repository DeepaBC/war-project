package ejava.util.rest;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.util.xml.JAXBHelper;

/**
 * This class encapsulates the full result of an HTTP call. 
 *
 * @param <T>
 */
public class HttpResult<T> {
    private static Logger log = LoggerFactory.getLogger(HttpResult.class);
    public final int status;
    public final T entity;
    public final Map<String, String> headers = new HashMap<String, String>();
    public final String errorMsg;
    public final Header[] rawHeaders;
    public HttpResult(int status, Header headers[], T entity) {
        this(status, headers, entity, null);
    }
    public HttpResult(int status, Header headers[], T entity, String errorMsg) {
        this.status = status;
        if (headers != null) {
            for (Header header : headers) {
                this.headers.put(header.getName(), header.getValue());
            }
        }
        this.entity = entity;
        this.errorMsg = errorMsg;
        this.rawHeaders = headers;
    }
    public String getFirstHeader(String name) {
        return headers.get(name);
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
    public static final <T> HttpResult<T> getResult(
            Class<T> clazz, Schema schema, HttpResponse response) 
            throws IllegalStateException, IOException, JAXBException {
        int status = response.getStatusLine().getStatusCode();
        log.debug("http.result={}", status);
        Header headers[] = response.getAllHeaders();
        InputStream is = response.getEntity()==null ? null :
            response.getEntity().getContent();
        try {
            if (status >= 400) {
                return new HttpResult<T>(status, headers, null, IOUtils.toString(is));
            }
            else if (status == 204) {
                return new HttpResult<T>(status, headers, null);
            }
            
            if (clazz.equals(Void.class) || clazz.equals(void.class)) {
                return new HttpResult<T>(status, headers, null);
            }
            else if (clazz.equals(String.class)) {
                return new HttpResult<T>(status, headers, (T) IOUtils.toString(is));
            }
            else if (clazz.equals(byte[].class)) {
                return new HttpResult<T>(status, headers, (T) IOUtils.toByteArray(is));
            }
            else if (clazz.getPackage().getName().startsWith("java.lang")) {
                String stringVal = IOUtils.toString(is);
                T val = null;
                try {
                    Constructor<T> ctor = clazz.getConstructor(String.class);
                    val = ctor.newInstance(stringVal);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                return new HttpResult<T>(status, headers, val);
            }
            else if (clazz.equals(Void.class)) {
                return new HttpResult<T>(status, headers, (T) null);
            }
            else {
                return new HttpResult<T>(status, headers, 
                        JAXBHelper.unmarshall(is, clazz, schema, clazz));
            }
        } finally {
            if (is != null) { is.close(); }
        }
    }
}