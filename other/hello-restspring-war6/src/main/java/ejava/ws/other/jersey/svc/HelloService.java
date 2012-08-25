package ejava.ws.other.jersey.svc;

import ejava.ws.other.jersey.model.Organization;
import ejava.ws.other.jersey.model.Person;

public interface HelloService {
	ServiceResult<String> sayHello();
    ServiceResult<Person> addName(Person person);
    ServiceResult<Person> getName(int id);
    ServiceResult<Organization> createOrganization(Organization organization);
    ServiceResult<Organization> getOrganization(int id);
}
