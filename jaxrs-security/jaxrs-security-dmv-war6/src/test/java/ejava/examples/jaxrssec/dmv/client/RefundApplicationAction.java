package ejava.examples.jaxrssec.dmv.client;

import ejava.examples.jaxrssec.dmv.lic.dto.Application;

/**
 * This class implements the refund of a DMV application payment.
 */
public class RefundApplicationAction extends PutApplicationAction {
    public Application refund() {
        return super.put(null);
    }
}
