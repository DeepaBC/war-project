package ejava.examples.restintro.svc;

import java.util.List;

import ejava.examples.restintro.rest.dto.Resident;

public interface DMVService {
    Resident createResident(Resident resident);
    Resident getResidentById(long id);
    boolean updateResident(Resident resident);
    int deleteResident(long id);
    List<Resident> getResidents();
    boolean isSamePerson(long p1, long p2);
    List<Resident> getResidents(int start, int count);
}
