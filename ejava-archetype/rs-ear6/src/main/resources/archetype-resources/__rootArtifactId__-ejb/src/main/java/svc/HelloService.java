#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.svc;

import javax.ejb.Local;

/**
 * This interface defines the service interface for the HelloService EJB.
 */
@Local
public interface HelloService {
    String sayHello(String name);
}
