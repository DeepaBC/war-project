package ejava.ws.other.jersey.svc;

public class ServiceResult<T> {
    public enum Status {
        OK, CLIENT_ERROR, SERVER_ERROR
    };
    private final Status status;
    private final String message;
    private final T result;
    
    private ServiceResult(Status status, String message, T result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }
    public static <T> ServiceResult<T> ok() {
        return new ServiceResult<T>(Status.OK, null, null);
    }
    public static <T> ServiceResult<T> ok(T result) {
        return new ServiceResult<T>(Status.OK, null, result);
    }
    public static <T> ServiceResult<T> clientError(String message) {
        return new ServiceResult<T>(Status.CLIENT_ERROR, message, null);
    }
    public static <T> ServiceResult<T> serverError(String message) {
        return new ServiceResult<T>(Status.SERVER_ERROR, message, null);
    }
    public boolean isOK() { return Status.OK.equals(status); }
    public boolean isClientError() { return Status.CLIENT_ERROR.equals(status); }
    public boolean isServerError() { return Status.SERVER_ERROR.equals(status); }
    public Status getStatus() { return status; }
    public String getMessage() { return message; }
    public T getResult() { return result; }    
}
