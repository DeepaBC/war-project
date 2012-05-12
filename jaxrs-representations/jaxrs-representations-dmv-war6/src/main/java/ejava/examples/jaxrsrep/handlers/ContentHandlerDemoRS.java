package ejava.examples.jaxrsrep.handlers;

import java.io.EOFException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.jaxrsrep.dmv.lic.dto.ContactInfo;
import ejava.examples.jaxrsrep.dmv.lic.dto.Person;

/**
 * This class is used to demonstrate contant handling capabilities of JAX-RS.
 */
@Path("content")
public class ContentHandlerDemoRS {
    private static final Logger log = LoggerFactory.getLogger(ContentHandlerDemoRS.class);
    private @Context UriInfo uriInfo;
    private @Context Request request; 
    private @Context HttpHeaders headers;
    private List<Person> people = new ArrayList<Person>();
    public ContentHandlerDemoRS() {
        Person p=null;
        people.add(p=new Person("peyton","manning"));
        ContactInfo info = new ContactInfo();
        p.getContactInfo().add(info.setCity("denver").setState("CO"));
        
        people.add(p=new Person("greg","williams"));
        info = new ContactInfo();
        p.getContactInfo().add(info.setCity("st. louis").setState("MO"));
        
        people.add(p=new Person("rg","3"));
        info = new ContactInfo();
        p.getContactInfo().add(info.setCity("washington").setState("DC"));

        people.add(p=new Person("andrew","luck"));
        info = new ContactInfo();
        p.getContactInfo().add(info.setCity("indianapolis").setState("IN"));

        people.add(p=new Person("cat","inhat"));
        people.add(p=new Person("thing","one"));
        people.add(p=new Person("thing","two"));
    }


    /**
     * This method provides a demonstration how the service can stream 
     * data directly back to the caller.
     * @param os
     * @throws IOException
     */
    @GET @Path("outputStream")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public void getOutputStream(@Context HttpServletResponse httpResponse) throws IOException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        ObjectOutputStream oos = new ObjectOutputStream(httpResponse.getOutputStream());
        try {
            for (Person p : people) {
                oos.writeObject(p);
            }
        } finally {
            oos.close();
        }
    }

    
    /**
     * This method provides a demonstration of the StreamingOutput construct. 
     * This allows the provide more flexibility in how the response is to be
     * provided to the caller by the provider.
     * @return
     */
    @GET @Path("streamingOutput")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getStreamingOutput() {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        StreamingOutput responseStream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException,
                    WebApplicationException {
                ObjectOutputStream oos = new ObjectOutputStream(os);
                try {
                    for (Person p : people) {
                        oos.writeObject(p);
                    }
                } finally {
                    oos.close();
                }
            }
        };
        return responseStream;
    }
    
    /**
     * This method demonstrates how to return a streaming output with 
     * additional response properties.
     * @return
     */
    @GET @Path("streamingOutput2")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getStreamingOutput2() {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        StreamingOutput responseStream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException,
                    WebApplicationException {
                ObjectOutputStream oos = new ObjectOutputStream(os);
                try {
                    for (Person p : people) {
                        oos.writeObject(p);
                    }
                } finally {
                    oos.close();
                }
            }
        };
        
        return Response.ok(responseStream)
                .lastModified(new Date())
                .build();
    }

    /**
     * This method demonstrates reading the entity body using an InputStream.
     * Any single un-annotated parameter on the command line is assumed to 
     * present the entity. 
     * @param is
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @PUT @Path("inputStream")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public int putInputStream(
            InputStream is,
            @HeaderParam("Content-Length") long size) throws IOException, ClassNotFoundException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("reading stream, size={} bytes", size);
        for (String key : headers.getRequestHeaders().keySet()) {
            log.debug("{}={}", key, headers.getRequestHeader(key));
        }
        
        int count=0;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(is);
            Person p=null;
            while ((p=(Person) ois.readObject())!=null) {
                if (count < 10 || count%1000==0) {
                    log.debug("{} read {}", count, p.getFirstName());
                }
                count += 1;
            }
        } catch (EOFException done) {
        } finally {
            if (ois != null) { ois.close(); }
        }
        return count;
    }

    /**
     * This method demonstrates reading the entity body using a temporary file.
     * @param file
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @PUT @Path("fromFile")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public int putFromFile(File file) throws IOException, ClassNotFoundException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("file={}", file.getAbsolutePath());
        int count=putInputStream(new FileInputStream(file), file.length());
        file.delete(); //didn't locate any documentation on who manages this
        return count;
    }
    
    /**
     * This method demonstrates return an entity to the provider using a file.
     * @return
     * @throws IOException
     */
    @GET @Path("fromFile")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public File getFromFile() throws IOException {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        
        File file = new File(new File("/tmp"), "temp.dat");
        //the above file is meant to be managed by the application and will
        //be leaked here -- sorry
        FileOutputStream fos = new FileOutputStream(file,false);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        for (Person p: people) {
            oos.writeObject(p);
        }
        oos.close();
        log.debug("wrote file={}", file.getAbsolutePath());
        
        return file;
    }
}
