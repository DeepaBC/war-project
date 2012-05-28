package ejava.examples.ejbear6.dmv.client;

import ejava.examples.ejbear6.dmv.lic.dto.Application;

/**
 * This class implements the payment of a DMV application.
 */
public class PayApplicationAction extends PutApplicationAction {
    public Application payment() {
        return super.put(null);
    }
}
