package ejava.ws.other.jersey.xml;

import info.ejava.organization.Org;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;

import ejava.ws.other.jersey.model.Organization;
import ejava.ws.other.jersey.model.Person;

public class MyJAXBContext extends JAXBContext {
    private JAXBContext ctx;

    public MyJAXBContext() throws JAXBException {
        ctx = JAXBContext.newInstance(
                Organization.class,
                Org.class,
                Person.class);
    }
    
    @Override
    public Marshaller createMarshaller() throws JAXBException {
        return ctx.createMarshaller();
    }

    @Override
    public Unmarshaller createUnmarshaller() throws JAXBException {
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        unmarshaller.setProperty("com.sun.xml.bind.ObjectFactory", new MyObjectFactory());
        return unmarshaller;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Validator createValidator() throws JAXBException {
        return ctx.createValidator();
    }

}
