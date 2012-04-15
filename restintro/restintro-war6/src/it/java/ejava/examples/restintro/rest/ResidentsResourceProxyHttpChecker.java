package ejava.examples.restintro.rest;

import ejava.util.rest.HttpResult;
import ejava.util.xml.JAXBHelper;

public class ResidentsResourceProxyHttpChecker extends ResidentsResourceProxy {
    private String checkHeader(HttpResult<?> result, StringBuilder text, String name, boolean exists) {
        String value = result.getFirstHeader(name);
        if (exists) { 
            //assertNotNull(name + " header missing", value); 
            text.append(String.format("%s: %s\n", name, value));
        }
        else { 
            //assertNull("unexpected header " + name, value); 
        }
        return value;
    }
    
    @Override
    protected <T> HttpResult<T> doCheckCreateResult(HttpResult<T> result) {
        if (result.status == 201) {
            StringBuilder text = new StringBuilder();
            text.append(String.format("\nCreate Response=%d Created\n", result.status));
            
            checkHeader(result, text, "Location", true);
            checkHeader(result, text, "Content-Location", true);
            checkHeader(result, text, "Last-Modified", true);
            checkHeader(result, text, "Expires", true);
            checkHeader(result, text, "Content-Type", true);
            checkHeader(result, text, "ETag", true);
            //checkHeader(result, text, "Cache-Control", true);
            
            log.debug("{}\n{}\n", text, JAXBHelper.toString(result.entity));
        }
        else {
            //fail("unexpected HTTP POST status code:" + result.status);
        }
        return result;
    }

    @Override
    protected <T> HttpResult<T> doCheckGetResult(HttpResult<T> result) {
        if (result.status == 200) {
            StringBuilder text = new StringBuilder();
            text.append(String.format("\nGET Response=%d OK\n", result.status));
            
            checkHeader(result, text, "Location", false);
            checkHeader(result, text, "Content-Location", true);
            checkHeader(result, text, "Expires", true);
            checkHeader(result, text, "Last-Modified", false);
            checkHeader(result, text, "ETag", true);
            //checkHeader(result, text, "Cache-Control", true);
            String contentType=checkHeader(result, text, "Content-Type", true);
            
            if ("application/xml".equals(contentType)) {
                log.debug("{}\n{}\n", text, JAXBHelper.toString(result.entity));
            } else {
                log.debug("{}\n{}\n", text, result.entity);
            }
        }
        else {
            //fail("unexpected HTTP GET status code:" + result.status);
        }
        return result;
    }
    
    @Override
    protected <T> HttpResult<T> doCheckPutResult(HttpResult<T> result) {
        if (result.status == 201) {
            StringBuilder text = new StringBuilder();
            text.append(String.format("\nPUT Response=%d Created\n", result.status));
            
            checkHeader(result, text, "Location", false);
            checkHeader(result, text, "Content-Location", true);
            checkHeader(result, text, "Last-Modified", false);
            checkHeader(result, text, "Content-Type", true);
            
            log.debug("{}\n{}\n", text, JAXBHelper.toString(result.entity));
        }
        if (result.status == 204) {
            StringBuilder text = new StringBuilder();
            text.append(String.format("\nPUT Response=%d No Content\n", result.status));
            
            checkHeader(result, text, "Location", false);
            checkHeader(result, text, "Content-Location", false);
            checkHeader(result, text, "Last-Modified", false);
            checkHeader(result, text, "Content-Type", false);
            
            log.debug("{}\n", text);
        }
        else {
            //fail("unexpected HTTP PUT status code:" + result.status);
        }
        return result;
    }

    @Override
    protected <T> HttpResult<T> doCheckDeleteResult(HttpResult<T> result) {
        if (result.status == 200) {
            String entityValue = null;
            StringBuilder text = new StringBuilder();
            text.append(String.format("\nDelete Response=%d OK\n", result.status));

            String contentType=result.getFirstHeader("Content-Type");
            if ("text/plain".equals(contentType)) {
                checkHeader(result, text, "Location", false);
                checkHeader(result, text, "Content-Location", false);
                checkHeader(result, text, "Last-Modified", false);
                checkHeader(result, text, "Content-Type", true);
                entityValue = result.entity.toString();
            }
            else {
                //fail("add tests for this media type");
            }
            
            log.debug("{}\n{}\n", text, entityValue);
        }
        else {
            //fail("unexpected HTTP DELETE status code:" + result.status);
        }
        return result;
    }

}
