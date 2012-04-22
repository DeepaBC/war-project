package ejava.exercises.simple.bank.svc;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.exercises.simple.bank.dto.Bank;

/**
 * This class implements a simple implementation for the overall
 * banking system.
 */
@Singleton
public class BankServiceStub implements BankService {
    private Logger log = LoggerFactory.getLogger(BankServiceStub.class);
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
        return bank;
    }

    @Override
    public int updateBank(Bank bank) {
        this.bank.setName(bank.getName());
        return 0;
    }
}
