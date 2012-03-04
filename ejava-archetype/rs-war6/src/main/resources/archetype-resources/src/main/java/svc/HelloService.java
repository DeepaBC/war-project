#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.svc;

/**
 * This interface defines the business interface for the Hello Service.
 */
public interface HelloService {
	String sayHello(String name);
}
