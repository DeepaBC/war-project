package ejava.examples.jaxrssec.dmv.client;

import ejava.examples.jaxrssec.dmv.lic.dto.Application;

/**
 * This class implements the payment of a DMV application.
 */
public class PayApplicationAction extends PutApplicationAction {
    public Application payment() {
        return super.put(null);
    }
}
