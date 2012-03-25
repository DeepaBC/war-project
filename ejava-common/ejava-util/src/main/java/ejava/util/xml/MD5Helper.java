package ejava.util.xml;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

/**
 * This helper class provides setup and calculation functions for obtaining
 * MD5 values.
 */
public class MD5Helper {
    
    public static byte[] calcMD5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(data);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        } finally {}
    }

    public static byte[] calcMD5(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(value.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        } finally {}
    }
    
    public static String getTag(byte[] data) {
        byte[] md5 = calcMD5(data);
        return DatatypeConverter.printBase64Binary(md5);
    }
    
    public static String getTag(String value) {
        byte[] md5 = calcMD5(value);
        return DatatypeConverter.printBase64Binary(md5);
    }
}
