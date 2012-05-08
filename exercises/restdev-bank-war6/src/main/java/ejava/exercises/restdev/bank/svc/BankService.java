package ejava.exercises.restdev.bank.svc;

import ejava.exercises.restdev.bank.dto.Bank;

/**
 * This interface represents what services are available for the overall
 * banking operation.
 */
public interface BankService {
    Bank getBank();
    int updateBank(Bank bank);
    void resetBank();
}
