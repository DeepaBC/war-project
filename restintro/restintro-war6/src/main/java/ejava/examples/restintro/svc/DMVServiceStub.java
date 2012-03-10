package ejava.examples.restintro.svc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import ejava.examples.restintro.rest.dto.Resident;

@Singleton
public class DMVServiceStub implements DMVService {
    private long residentId=1;
    private Map<Long, Resident> residents=new HashMap<Long, Resident>();

    @Override
    public Resident createResident(Resident resident) {
        if (resident != null && 
                resident.getFirstName() != null &&
                resident.getLastName() != null) {
            resident.setId(residentId++);
            residents.put(resident.getId(), resident);
            return resident;
        }
        return null;
    }

    @Override
    public Resident getResidentById(long id) {
        return residents.get(id);
    }

    @Override
    public boolean updateResident(Resident resident) {
        Resident dbResident = residents.get(resident.getId());
        if (dbResident != null) {
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
        return dbResident != null ? 1 : 0;
    }

    @Override
    public List<Resident> getResidents() {
        List<Resident> residentList = new ArrayList<Resident>();
        residentList.addAll(residents.values());
        return residentList;
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
        return page;
    }
}
