package ejava.examples.restintro.svc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.restintro.rest.dto.Resident;
import ejava.util.xml.JAXBHelper;

@Singleton
public class DMVServiceStub implements DMVService {
    private static final Logger log = LoggerFactory.getLogger(DMVServiceStub.class);
    private long residentId=1;
    private Map<Long, Resident> residents=new HashMap<Long, Resident>();

    @Override
    public Resident createResident(Resident resident) {
        if (resident != null && 
                resident.getFirstName() != null &&
                resident.getLastName() != null) {
            resident.setId(residentId++);
            residents.put(resident.getId(), resident);
            log.debug("creating resident {}:{}", resident.getId(), resident);
            return resident;
        }
        return null;
    }

    @Override
    public Resident getResidentById(long id) {
        Resident resident = residents.get(id);
        log.debug("getting resident {}:{}", id, resident);
        return resident;
    }

    @Override
    public boolean updateResident(Resident resident) {
        Resident dbResident = residents.get(resident.getId());
        if (dbResident != null) {
            log.debug("replacing:{}", JAXBHelper.toString(dbResident));
            log.debug("with:{}", JAXBHelper.toString(resident));
            residents.put(resident.getId(), resident);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public int deleteResident(long id) {
        Resident dbResident = residents.remove(id);
        log.debug("deleted resident {}:{}", id, dbResident);
        return dbResident != null ? 1 : 0;
    }

    @Override
    public List<Resident> getResidents() {
        List<Resident> residentList = new ArrayList<Resident>();
        residentList.addAll(residents.values());
        return residentList;
    }

    @Override
    public String getResidentNames() {
        StringBuilder text = new StringBuilder();
        for (Resident resident : getResidents()) {
            text.append(String.format("%s, %s\n", 
                    resident.getLastName(), 
                    resident.getFirstName()));
        }
        return text.toString();
    }

    @Override
    public boolean isSamePerson(long p1, long p2) {
        Resident r1 = residents.get(p1);
        Resident r2 = residents.get(p2);
        return r1!=null && r2 != null &&
                r1.getFirstName().equals(r2.getFirstName()) &&
                r1.getLastName().equals(r2.getLastName()) &&
                r1.getContactInfo().size() >= 1 &&
                r2.getContactInfo().size() >= 1 &&
                r1.getContactInfo().get(0).getZip().equals(
                        r2.getContactInfo().get(0).getZip());
                
    }

    @Override
    public List<Resident> getResidents(int start, int count) {
        List<Resident> residents = getResidents();
        List<Resident> page = new ArrayList<Resident>();
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
}
