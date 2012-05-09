package ejava.examples.jaxrscs.httpmethod.rs;

import java.sql.SQLException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * This class will map between a thrown SQLException and an HTTP 500 error
 * response.
 */
@Provider
public class SQLExceptionMapper implements ExceptionMapper<SQLException>{
    @Override
    public Response toResponse(SQLException ex) {
        return Response.serverError()
                .entity(ex.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
    
}
