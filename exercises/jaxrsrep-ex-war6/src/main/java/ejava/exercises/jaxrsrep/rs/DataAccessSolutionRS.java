package ejava.exercises.jaxrsrep.rs;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("data-solution")
public class DataAccessSolutionRS {
    private static Logger log = LoggerFactory.getLogger(DataAccessSolutionRS.class);
    private @Context UriInfo uriInfo;
    private @Context Request request;

    @Path("calcBuffered")
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response calcResultsBuffered(
            //all inputs have been buffered into this temporary file
            byte[] input) throws IOException {
        log.info("called: {} {}", request.getMethod(), uriInfo.getRequestUri());

            //process all inputs and build a buffer of results
        InputStream is = new ByteArrayInputStream(input);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        process(is, os).close();
        String result = os.toString();
        
            //return result
        return Response.ok(result, MediaType.APPLICATION_OCTET_STREAM)
                .build();
    }
    
    @Path("calcBuffered2")
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response calcResultsBuffered2(
            //all inputs have been buffered into this temporary file
            File input) throws IOException {
        log.info("called: {} {}", request.getMethod(), uriInfo.getRequestUri());

        try {
                //process all inputs and build a buffer of results
            InputStream is = new FileInputStream(input);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            process(is, os).close();
            String result = os.toString();
            
                //return result
            return Response.ok(result, MediaType.APPLICATION_OCTET_STREAM)
                    .build();
        } finally {
            input.delete();
        }
    }

    @Path("calcStreamed")
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response calcResultsStream(final InputStream is, 
            @Context HttpServletResponse httpResponse) 
            throws IOException {
        log.info("called: {} {}", request.getMethod(), uriInfo.getRequestUri());
        
        OutputStream os = httpResponse.getOutputStream();
        process(is, os); //don't close -- this is the raw HttpServletRequest
                         //and the provider will need to write the response
                         //info below

        return Response.ok()
                .build();
    }

    @Path("calcStreamed2")
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response calcResultsStream2(final InputStream is) throws IOException {
        log.info("called: {} {}", request.getMethod(), uriInfo.getRequestUri());
        
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException,
                    WebApplicationException {
                process(is, os).close();
            }
        };

        return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
                .build();
    }

    /**
     * This helper method will read formulas from the input stream, calculate
     * the results, and write the answer to the output stream. The input stream
     * is read/closed. The output stream is left open for the caller to close.
     * @param is
     * @param os
     * @throws IOException
     */
    private OutputStream process(final InputStream is, final OutputStream os) throws IOException {
        return new DataAccessRS().process(is, os);
    }
}
