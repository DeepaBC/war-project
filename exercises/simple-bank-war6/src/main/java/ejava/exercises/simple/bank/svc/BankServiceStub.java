package ejava.exercises.simple.bank.svc;

import javax.inject.Inject;
import javax.inject.Singleton;

import ejava.exercises.simple.bank.dto.Bank;

/**
 * This class implements a simple implementation for the overall
 * banking system.
 */
@Singleton
public class BankServiceStub implements BankService {
    private Bank bank=new Bank();
    private @Inject AccountsService accounts;

    @Override
    public Bank getBank() {
        Bank b = new Bank();
        b.setName(bank.getName());
        b.setTotalAssets(accounts.getAssets());
        return bank;
    }

    @Override
    public int updateBank(Bank bank) {
        this.bank.setName(bank.getName());
        return 0;
    }
}
