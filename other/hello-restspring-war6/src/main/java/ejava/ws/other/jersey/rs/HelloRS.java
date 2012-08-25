package ejava.ws.other.jersey.rs;

import info.ejava.organization.Org;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.ws.other.jersey.model.Organization;
import ejava.ws.other.jersey.model.Person;
import ejava.ws.other.jersey.svc.HelloService;
import ejava.ws.other.jersey.svc.ServiceResult;

@Path("")
public class HelloRS {
    private Logger log = LoggerFactory.getLogger(HelloRS.class);
	
	@Inject
	private HelloService service;
	@Context
	private UriInfo uriInfo;
	@Context
	private Request request;

	@Path("sayHello")
	@GET
	public Response sayHello() {
	    log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
		return Response.ok(service.sayHello().getResult()).build();
	}
	
	@Path("names")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	public Response addName(Person person) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
	    try {
	        ServiceResult<Person> result = service.addName(person);
	        if (result.isOK()) {
	            person = result.getResult();
                URI uri=uriInfo.getBaseUriBuilder()
                        .path(HelloRS.class)
                        .path(HelloRS.class, "getName")
                        .build(person.getId());
    	        return Response.created(uri)
    	                .lastModified(person.getLastModified())
                        .tag(""+person.getLastModified().getTime())
    	                .build();
	        }
	        else if (result.isClientError()) {
	            return Response.status(Response.Status.BAD_REQUEST)
	                    .type(MediaType.TEXT_PLAIN_TYPE)
	                    .entity(result.getMessage())
	                    .build();
	        }
	        else {
	            return Response.serverError()
	                    .type(MediaType.TEXT_PLAIN_TYPE)
	                    .entity(result.getMessage())
	                    .build();
	        }
	    } catch (Exception ex) {
	        return Response.serverError()
	                .type(MediaType.TEXT_PLAIN_TYPE)
	                .entity(ex.getLocalizedMessage())
	                .build();
	    }
	}
	
	@Path("names/{id}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@PrettyPrint
	public Response getName(@PathParam("id") int id) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        try {
            ServiceResult<Person> result = service.getName(id);
            if (result.isOK() && result.getResult()!=null) {
                Person person = result.getResult();
                return Response.ok(person, MediaType.APPLICATION_XML_TYPE)
                        .lastModified(person.getLastModified())
                        .tag(""+person.getLastModified().getTime())
                        .build();
            }
            else if (result.isOK() && result.getResult()==null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(String.format("unknown id: %d", id))
                        .build();
            }
            else if (result.isClientError()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .type(MediaType.TEXT_PLAIN_TYPE)
                        .entity(result.getMessage())
                        .build();
            }
            else {
                return Response.serverError()
                        .type(MediaType.TEXT_PLAIN_TYPE)
                        .entity(result.getMessage())
                        .build();
            }
        } catch (Exception ex) {
            return Response.serverError()
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(ex.getLocalizedMessage())
                    .build();
        }
	}
	
	@Path("orgs")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	@PrettyPrint
	public Response createOrg(Org org) {
        log.debug("{} {}", request.getMethod(), uriInfo.getRequestUri());
        log.debug("org.type={}", org.getClass().getName());
        for (Object person : org.getMembers()) {
            log.debug("person.type={}", person.getClass().getName());
        }
        if (!Organization.class.equals(org.getClass())) {
            return Response.serverError()
                    .entity("unexpected org type:" + org.getClass())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        
        
        try {
            Organization organization = (Organization)org;
            ServiceResult<Organization> result = service.createOrganization(organization);
            if (result.isOK()) {
                return Response.ok(result.getResult())
                        .build();
            }
            else if (result.isClientError()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .type(MediaType.TEXT_PLAIN_TYPE)
                        .entity(result.getMessage())
                        .build();
            }
            else {
                return Response.serverError()
                        .type(MediaType.TEXT_PLAIN_TYPE)
                        .entity(result.getMessage())
                        .build();
            }
        } catch (Exception ex) {
            return Response.serverError()
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(ex.getLocalizedMessage())
                    .build();
        }
	}
	
}
