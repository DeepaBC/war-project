package ejava.examples.jaxrsscale.concurrency;

import ejava.examples.jaxrsscale.concurrency.dto.ConcurrencyCheck;

/**
 * This is the interface for an example service that will be accessed
 * concurrently by different clients.
 */
public interface ConcurrentService {
    ConcurrencyCheck get();
    void set(ConcurrencyCheck update);
}
