package ejava.examples.restintro.dmv.svc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.restintro.dmv.dto.Person;
import ejava.examples.restintro.dmv.dto.Persons;
import ejava.util.xml.JAXBHelper;

@Singleton
public class ResidentsServiceStub implements ResidentsService {
    private static final Logger log = LoggerFactory.getLogger(ResidentsServiceStub.class);
    private long residentId=1;
    private Map<Long, Person> residents=new HashMap<Long, Person>();

    @Override
    public Person createResident(Person resident) {
        if (resident != null && 
                resident.getFirstName() != null &&
                resident.getLastName() != null) {
            resident.setId(residentId++);
            resident.setLastModified(new Date());
            residents.put(resident.getId(), resident);
            log.debug("creating resident {}:{}", resident.getId(), resident);
            return resident;
        }
        return null;
    }

    @Override
    public Person getResidentById(long id) {
        Person resident = residents.get(id);
        log.debug("getting resident {}:{}", id, resident);
        return resident;
    }

    @Override
    public int updateResident(Person resident) {
        Person dbResident = residents.get(resident.getId());
        if (dbResident != null) {
            log.debug("replacing:{}", JAXBHelper.toString(dbResident));
            log.debug("with:{}", JAXBHelper.toString(resident));
            resident.setLastModified(new Date());
            residents.put(resident.getId(), resident);
            return 0;
        }
        else {
            return -1;
        }
    }

    @Override
    public int deleteResident(long id) {
        Person dbResident = residents.remove(id);
        if (dbResident != null) {
            dbResident.setLastModified(new Date());
        }
        log.debug("deleted resident {}:{}", id, dbResident);
        return dbResident != null ? 1 : 0;
    }

    protected List<Person> getResidents() {
        List<Person> residentList = new ArrayList<Person>();
        residentList.addAll(residents.values());
        return residentList;
    }

    /*
    @Override
    public String getResidentNames() {
        StringBuilder text = new StringBuilder();
        for (Person resident : getResidents()) {
            text.append(String.format("%s, %s\n", 
                    resident.getLastName(), 
                    resident.getFirstName()));
        }
        return text.toString();
    }

    @Override
    public boolean isSamePerson(long p1, long p2) {
        Person r1 = residents.get(p1);
        Person r2 = residents.get(p2);
        return r1!=null && r2 != null &&
                r1.getFirstName().equals(r2.getFirstName()) &&
                r1.getLastName().equals(r2.getLastName()) &&
                r1.getContactInfo().size() >= 1 &&
                r2.getContactInfo().size() >= 1 &&
                r1.getContactInfo().get(0).getZip().equals(
                        r2.getContactInfo().get(0).getZip());
                
    }
    */
    
    @Override
    public Persons getResidents(int start, int count) {
        List<Person> residents = getResidents();
        Persons page = new Persons();
        page.setStart(0);
        page.setCount(count);
            //conditionally pay attention to start and count if >0
        for (int i=0; i<residents.size()&&(count<=0||page.size()<count); i++) {
            if (start <= 0 || i >= start) {
                page.add(residents.get(i));
            }
        }
        log.debug(String.format("returning residents (start=%d, count=%d)=%d",
                start, count, page.size()));
        return page;
    }

    @Override
    public Persons findResidentsByName(String firstName, String lastName, int start, int count) {
        Persons page = new Persons();
        page.setStart(0);
        page.setCount(count);
        List<Person> residents = getResidents();
        for (int i=0; i<residents.size(); i++) {
            if (count > 0 && page.size() >= count ) { break; }
            else if (start > 0 && i < start) { continue; }
            
            Person p = residents.get(i);
            if (lastName==null && firstName != null && firstName.equals(p.getFirstName())) {
                page.add(p);
            }
            else if (firstName==null && lastName != null && lastName.equals(p.getLastName())) {
                page.add(p);
            }
            else if (lastName != null && firstName != null &&
                    firstName.equals(p.getFirstName()) &&
                    lastName.equals(p.getLastName())) {
                page.add(p);
            }
        }
        return page;
    }
}
