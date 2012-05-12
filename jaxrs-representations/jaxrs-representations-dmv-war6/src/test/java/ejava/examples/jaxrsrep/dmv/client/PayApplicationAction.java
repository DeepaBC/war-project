package ejava.examples.jaxrsrep.dmv.client;

import ejava.examples.jaxrsrep.dmv.lic.dto.Application;

/**
 * This class implements the payment of a DMV application.
 */
public class PayApplicationAction extends PutApplicationAction {
    public Application payment() {
        return super.put(null);
    }
}
