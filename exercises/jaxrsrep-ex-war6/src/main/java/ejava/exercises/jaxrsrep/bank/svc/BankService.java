package ejava.exercises.jaxrsrep.bank.svc;

import ejava.exercises.jaxrsrep.bank.dto.Bank;

/**
 * This interface represents what services are available for the overall
 * banking operation.
 */
public interface BankService {
    Bank getBank();
    int updateBank(Bank bank);
    void resetBank();
}
