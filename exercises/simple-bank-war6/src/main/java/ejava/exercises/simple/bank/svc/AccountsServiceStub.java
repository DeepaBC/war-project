package ejava.exercises.simple.bank.svc;

import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.exercises.simple.bank.dto.Account;
import ejava.exercises.simple.bank.dto.Accounts;
import ejava.exercises.simple.bank.dto.BankRepresentation;

/**
 * This class provides a functional, in-memory implementation of the 
 * application service interface.
 */
@Singleton
public class AccountsServiceStub implements AccountsService {
    private static final Logger log = LoggerFactory.getLogger(AccountsServiceStub.class);
    private AtomicInteger accountId=new AtomicInteger(new Random().nextInt(100));
    private Map<Integer, Account> accounts = new HashMap<Integer, Account>();
    
    @Override
    public float getAssets() {
        float total = 0;
        for (Account account : accounts.values()) {
            total += account.getBalance();
        }
        return total;
    }

    @Override
    public Account createAccount(Account account) {
        account.setId(accountId.addAndGet(1));
        account.setUpdated(new Date());
        account.resetLinks();
        accounts.put(account.getId(), account);
        log.debug("created account:{}", account.toXML());
        return account;
    }

    @Override
    public Account getAccount(int id) {
        return accounts.get(id);
    }

    @Override
    public int updateAccount(Account account) {
        if (account == null) { return -1; }
        Account dbAccount = accounts.get(account.getId());
        if (dbAccount != null) {
            dbAccount.setOwnerName(account.getOwnerName());
            account.setUpdated(new Date());
            account.resetLinks();
            log.debug("updated account:{}", account.toXML());
            return 0;
        }
        else {
            log.debug("account not found:{}", account.getId());
            return 1;
        }
    }

    @Override
    public int deleteAccount(int id) {
        log.debug("removing account:{}", id);
        return accounts.remove(id)==null ? 1 : 0;
    }

    @Override
    public int deposit(int id, float amount) {
        Account account = accounts.get(id);
        if (account != null) {
            log.debug("deposited to account:{}", account.toXML());
            account.deposit(amount);
            account.setUpdated(new Date());
            account.resetLinks();
            return 0;
        }
        else {
            return 1;
        }
    }

    @Override
    public int withdraw(int id, float amount) {
        Account account = accounts.get(id);
        if (account != null) {
            log.debug("withdraw to account:{}", account.toXML());
            account.setUpdated(new Date());
            account.withdraw(amount);
            account.resetLinks();
            return 0;
        }
        else {
            return 1;
        }
    }

    @Override
    public int transfer(int fromId, int toId, float amount) {
        Account fromAccount = accounts.get(fromId);
        Account toAccount = accounts.get(toId);
        if (fromAccount != null && toAccount != null) {
            log.debug("transfer from {} to {}", fromAccount.toXML(), toAccount.toXML());
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
            fromAccount.setUpdated(new Date());
            toAccount.setUpdated(new Date());
            fromAccount.resetLinks();
            toAccount.resetLinks();
            return 0;
        }
        else {
            return 1;
        }
    }

    @Override
    public Accounts getAccounts(int start, int count) {
        Accounts result = new Accounts(start, count);
        List<Integer> ids = new ArrayList<Integer>(accounts.keySet());
        start = start<0 ? 0 : start;
        for (int i=start; i<ids.size() && (count > 0 && result.size()<count); i++) {
            Account account = accounts.get(ids.get(i));
            if (account != null) {
                result.add(account);
            }
        }
        return result;
    }
}
