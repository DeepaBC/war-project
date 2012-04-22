package ejava.exercises.simple.bank.svc;

import ejava.exercises.simple.bank.dto.Account;
import ejava.exercises.simple.bank.dto.Accounts;

public interface AccountsService {
    float getAssets();
    Account createAccount(Account account);
    Account getAccount(int id);
    int updateAccount(Account account);
    int deleteAccount(int id);
    int deposit(int id, float amount);
    int withdraw(int id, float amount);
    int transfer(int fromId, int toId, float amount);
    Accounts getAccounts(int start, int count);
}
