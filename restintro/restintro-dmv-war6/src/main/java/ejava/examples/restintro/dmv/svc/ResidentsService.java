package ejava.examples.restintro.dmv.svc;

import ejava.examples.restintro.dmv.dto.Person;
import ejava.examples.restintro.dmv.dto.Persons;

/**
 * This interface defines the methods our business tier can perform on
 * an resident.
 */
public interface ResidentsService {
    Person createResident(Person app);
    Person getResidentById(long id);
    int updateResident(Person app);
    int deleteResident(long id);
    Persons getResidents(int start, int count);
    Persons findResidentsByName(String firstName, String lastName, int start, int count);
}
