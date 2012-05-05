package ejava.examples.jaxrscs.dmv.client;

import ejava.examples.jaxrscs.dmv.lic.dto.Application;

/**
 * This class implements the approval of a DMV application.
 */
public class ApproveApplicationAction extends PutApplicationAction {
    public Application approve() {
        return super.put(null);
    }
}
