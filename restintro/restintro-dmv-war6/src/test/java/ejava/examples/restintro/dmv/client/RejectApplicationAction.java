package ejava.examples.restintro.dmv.client;

import ejava.examples.restintro.dmv.lic.dto.Application;

/**
 * This class implements the rejection of a DMV application.
 */
public class RejectApplicationAction extends PutApplicationAction {
    public Application approve() {
        return super.put(null);
    }
}
