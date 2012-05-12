package ejava.examples.jaxrsrep.dmv.client;

import ejava.examples.jaxrsrep.dmv.lic.dto.Application;

/**
 * This class implements the approval of a DMV application.
 */
public class ApproveApplicationAction extends PutApplicationAction {
    public Application approve() {
        return super.put(null);
    }
}
