package ejava.exercises.jaxrsrep.rs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

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

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("data")
public class DataAccessRS {
    private static Logger log = LoggerFactory.getLogger(DataAccessRS.class);
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
        
        return null;
    }

    @Path("calcStreamed")
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response calcResultsStream(
            //all inputs can be streamed into the method from this source
            final InputStream is, 
            //all outputs can be streamed to the response object
            @Context HttpServletResponse httpResponse) 
            throws IOException {

        return null;
    }

    @Path("calcStreamed2")
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response calcResultsStream2(
            //all input data will be read from this stream
            final InputStream is) throws IOException {
        log.info("called: {} {}", request.getMethod(), uriInfo.getRequestUri());

        @SuppressWarnings("unused")
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException,
                    WebApplicationException {
                //process the data and write result to os
            }
        };
        
        return null;
    }

    /**
     * This helper method will read formulas from the input stream, calculate
     * the results, and write the answer to the output stream. The input stream
     * is read/closed. The output stream is left open for the caller to close.
     * @param is
     * @param os
     * @throws IOException
     */
    OutputStream process(final InputStream is, final OutputStream os) throws IOException {
        int count=0;
        try {
            for (LineIterator itr=IOUtils.lineIterator(is, "UTF-8"); itr.hasNext(); ) {
                String line=itr.nextLine();
                StringTokenizer tok = new StringTokenizer(line," ");
                if (tok.countTokens()==3) {
                    int loperand = Integer.parseInt(tok.nextToken());
                    String operator = tok.nextToken();
                    int roperand = Integer.parseInt(tok.nextToken());
                    StringBuilder result = new StringBuilder(
                            String.format("%d %s %d = ", loperand, operator, roperand));
                    if (operator.equals("+")) {
                        result.append(new Integer(loperand + roperand).toString());
                    }
                    else if (operator.equals("-")) {
                        result.append(new Integer(loperand - roperand).toString());
                    }
                    else if (operator.equals("*")) {
                        result.append(new Integer(loperand * roperand).toString());
                    }
                    else if (operator.equals("/")) {
                        result.append(new Integer(loperand / roperand).toString());
                    }
                    else {
                        result.append("???");
                    }
                    result.append("\n");
                    os.write(result.toString().getBytes("UTF-8"));
                    count += 1;
                    //log.info(line);
                    System.out.print(".");
                }
            }
        } finally {
            is.close();
            os.flush();
        }
        log.info("processed {} requests", count);
        return os;
    }
}
