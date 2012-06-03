package ejava.examples.jaxrssec.dmv.client;

import ejava.examples.jaxrssec.dmv.lic.dto.Application;

/**
 * This class implements the approval of a DMV application.
 */
public class ApproveApplicationAction extends PutApplicationAction {
    public Application approve() {
        return super.put(null);
    }
}
