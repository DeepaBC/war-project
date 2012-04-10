package ejava.examples.restintro.dmv.client;

import ejava.examples.restintro.dmv.dto.Application;

/**
 * This class implements the payment of a DMV application.
 */
public class PayApplicationAction extends PutApplicationAction {
    public Application payment() {
        return super.put(null);
    }
}
