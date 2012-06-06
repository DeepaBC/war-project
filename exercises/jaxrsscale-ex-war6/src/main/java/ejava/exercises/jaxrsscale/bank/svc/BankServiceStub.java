package ejava.exercises.jaxrsscale.bank.svc;

import java.util.Date;



import javax.inject.Inject;
import javax.inject.Singleton;

import ejava.exercises.jaxrsscale.bank.dto.Bank;

/**
 * This class implements a simple implementation for the overall
 * banking system.
 */
@Singleton
public class BankServiceStub implements BankService {
    private Bank bank=new Bank();
    private @Inject AccountsService accounts;
    
    public BankServiceStub() {
        bank.setUpdated(new Date());
    }

    @Override
    public Bank getBank() {
        Bank b = new Bank();
        b.setName(bank.getName());
        b.setTotalAssets(accounts.getAssets());
        b.setUpdated(bank.getUpdated());
        b.resetLinks();
        return b;
    }

    @Override
    public int updateBank(Bank bank) {
        if (bank != null) {
            this.bank.setName(bank.getName());
            return 0;
        }
        else {
            return -1;
        }
    }

    @Override
    public void resetBank() {
        accounts.resetAccounts();
        this.bank=new Bank();
        bank.setUpdated(new Date());
    }
    
    
}
