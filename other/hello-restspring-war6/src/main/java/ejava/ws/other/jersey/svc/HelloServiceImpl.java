package ejava.ws.other.jersey.svc;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import ejava.ws.other.jersey.model.Organization;
import ejava.ws.other.jersey.model.Person;

/**
 * This class implements the service logic and uses the JPA entity manager
 * and annotations as a simple DAO.
 */
public class HelloServiceImpl implements HelloService {
    @PersistenceContext
    private EntityManager em;
    
	public ServiceResult<String> sayHello() {
		return ServiceResult.ok("hello");
	}

	@Transactional
    public ServiceResult<Person> addName(Person person) {
        em.persist(person);
        return ServiceResult.ok(person);
    }

    @Transactional(readOnly=true)
    public ServiceResult<Person> getName(int id) {
        Person person = em.find(Person.class, id);
        return ServiceResult.ok(person);
    }
    
    @Transactional
    public ServiceResult<Organization> createOrganization(
            Organization organization) {
        organization.setInsertDate(new Date());
        em.persist(organization);
        return ServiceResult.ok(organization);
    }

    @Transactional(readOnly=true)
    public ServiceResult<Organization> getOrganization(int id) {
        Organization organization = em.find(Organization.class, id);
        return ServiceResult.ok(organization);
    }
}
