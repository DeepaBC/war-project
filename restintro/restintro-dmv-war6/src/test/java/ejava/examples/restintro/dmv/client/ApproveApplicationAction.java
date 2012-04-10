package ejava.examples.restintro.dmv.client;

import ejava.examples.restintro.dmv.dto.Application;

/**
 * This class implements the approval of a DMV application.
 */
public class ApproveApplicationAction extends PutApplicationAction {
    public Application approve() {
        return super.put(null);
    }
}
