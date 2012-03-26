package ejava.examples.restintro.dmv.svc;

/**
 * This exception is thrown when an argument is invalid and may have been
 * provided by the client.
 */
public class BadArgument extends Exception {
    private static final long serialVersionUID = 1L;

    public BadArgument(String message) {
        super(message);
    }
}
