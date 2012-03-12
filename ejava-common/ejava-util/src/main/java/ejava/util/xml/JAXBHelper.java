package ejava.util.xml;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

/**
 * This class contains helper methods to assist in the marshalling and
 * demarshalling fo JAXB objects.
 */
public class JAXBHelper {
    /**
     * This helper function calculates an md5 checksum of the provided
     * object and returns as a base64 encoded string. This can be used
     * to track state changes to the object. If the object is a string, the
     * md5 is calculated on the string's physical byte value. Otherwise it
     * is assumed to be a jaxb object and the serialized byte stream is 
     * used for the md5.
     * @param object
     * @return
     */
    public static String getTag(Object object) {
        try {
            byte[] bytes=object instanceof String ? 
                   ((String)object).getBytes("UTF-8") :
                   JAXBHelper.marshall(object, null);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5 = md.digest(bytes);
            return DatatypeConverter.printBase64Binary(md5);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    
    /**
     * This helper method provides a convenient wrapper around the marshalling
     * function for the sole purpose of producing an XML string for printing.
     * @param object
     * @param classes
     * @return
     */
    public static String toString(Object object, Class<?>...classes) {
        try {
            return new String(marshall(object, null, classes));
        } catch (JAXBException ex) {
            throw new RuntimeException("error marshalling object:" + object, ex);
        }
    }

    /**
     * This helper method provides a convenient wrapper around the marshalling
     * function for the sole purpose of producing an XML string for printing.
     * @param object
     * @param max number of characters to limit the returned string to (plus
     *    extra ... characters at the end.
     * @param classes
     * @return
     */
    public static Object toString(Object object, int max, Class<?>...classes) {
        String text = toString(object, classes);
        if (max > 0 && text.length() > max) {
            text = new StringBuilder(text.substring(0, max)).append("...").toString();
        }
        return text;
    }
    
    /**
     * This helper method will marshal a JAXB object to a returned byte[].
     * @param object
     * @param schema
     * @param classes
     * @return
     * @throws JAXBException 
     */
    public static byte[] marshall(Object object, Schema schema, Class<?>...classes) 
            throws JAXBException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshall(bos, object, schema, classes);
        return bos.toByteArray();
    }
    
    /**
     * This helper method will marshal a JAXB object to an output stream.
     * @param os
     * @param object
     * @param schema
     * @param classes
     * @throws JAXBException
     */
    public static void marshall(
            OutputStream os, Object object, Schema schema, Class<?>...classes) 
            throws JAXBException {
        if (object == null) { return; }
        Class<?>[] clazzes= new Class[classes.length+1];
        clazzes[0] = object.getClass();
        System.arraycopy(classes, 0, clazzes, 1, classes.length);
        JAXBContext ctx = JAXBContext.newInstance(clazzes);
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        if (schema != null) {
            marshaller.setSchema(schema);
        }
        marshaller.marshal(object, os);            
    }
    
    /**
     * This helper method unmarshalls a JAXB object from a byte[]
     * @param data
     * @param type
     * @param schema
     * @param classes
     * @return
     * @throws JAXBException
     * @throws IOException
     */
    public static <T> T unmarshall(
            byte[] data, Class<T> type, Schema schema, Class<?>...classes) 
            throws JAXBException, IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        return unmarshall(bis, type, schema, classes);
    }
    
    /**
     * This method will unmarshall an input stream into a JAXB jaba object.
     * @param is input stream with full object -- will be closed during call
     * @param type class type of object being returned
     * @param schema optional schema for validation
     * @param classes an array of one or more classes being umarshalled
     * @return
     * @throws JAXBException
     * @throws IOException
     */
    public static <T> T unmarshall(
            InputStream is, Class<T> type, Schema schema, Class<?>...classes) 
            throws JAXBException, IOException {
        try {
            JAXBContext ctx = JAXBContext.newInstance(classes);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            if (schema != null) {
                unmarshaller.setSchema(schema);
            }
            @SuppressWarnings("unchecked")
            T object = (T) unmarshaller.unmarshal(is);
            return object;
        }
        finally {
            is.close();
        }
    }

}
