package ejava.examples.jaxrscs.httpmethod.rs;

import java.lang.annotation.ElementType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.HttpMethod;

/**
 * An example HTTP Method Annotation that can be used to extend the 
 * defined HTTP 1.1 methods to support extensions like WebDAV. 
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("FOO")
public @interface FOO {
}
