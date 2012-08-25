package ejava.ws.other.jersey.xml;

import javax.xml.bind.annotation.XmlRegistry;

import ejava.ws.other.jersey.model.Organization;
import info.ejava.organization.Org;

@XmlRegistry
public class MyObjectFactory extends info.ejava.organization.ObjectFactory {

    @Override
    public Org createOrg() {
        return new Organization();
    }

}
