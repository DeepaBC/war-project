package ejava.exercises.simple.bank.rs;

import java.lang.reflect.Method;
import java.net.URI;

import javax.ws.rs.core.UriInfo;

import ejava.exercises.simple.bank.dto.Account;
import ejava.exercises.simple.bank.dto.Accounts;
import ejava.exercises.simple.bank.dto.BankRepresentation;

/**
 * This class builds full URL hrefs for links within the accounts.
 */
public class AccountRefs {
    protected UriInfo uriInfo;
    
    public AccountRefs(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public URI setHRefs(Accounts accounts) {
        URI self = accountsURI(accounts.getStart(), accounts.getCount());
        for (BankRepresentation.Link link : accounts.getLinks()) {
            if (BankRepresentation.SELF_REL.equals(link.getRel())) {
                link.setHref(self);
            }
            else if (BankRepresentation.FIRST_REL.equals(link.getRel())) {
                link.setHref(accountsURI(0, accounts.getCount()));
            }
            else if (BankRepresentation.NEXT_REL.equals(link.getRel())) {
                link.setHref(accountsURI(accounts.getStart()+accounts.getCount(), accounts.getCount()));
            }
        }
        for (Account account: accounts) {
            setHRefs(account);
        }
        return self;
    }
    
    public URI setHRefs(Account account) {
        URI self = selfURI(account.getId());
        for (BankRepresentation.Link link : account.getLinks()) {
            if (link.getHref() == null) {
                if (BankRepresentation.SELF_REL.equals(link.getRel())) {
                    link.setHref(selfURI(account.getId()));
                }
                else if (BankRepresentation.ACCOUNTS_REL.equals(link.getRel())) {
                    link.setHref(accountsURI());
                }
                else if (BankRepresentation.DEPOSIT_REL.equals(link.getRel())) {
                    link.setHref(depositURI(account.getId()));
                }
                else if (BankRepresentation.WITHDRAW_REL.equals(link.getRel())) {
                    link.setHref(withdrawURI(account.getId()));
                }
                else if (BankRepresentation.TRANSFER_REL.equals(link.getRel())) {
                    link.setHref(transferURI());
                }
            }
        }
        return self;
    }

    public URI selfURI(long id) {
        return uriInfo.getBaseUriBuilder()
                .path(AccountsRS.class)
                .path(AccountsRS.class, "getAccountById")
                .build(id);
    }

    public URI accountsURI() {
        return uriInfo.getBaseUriBuilder()
                .path(AccountsRS.class)
                .build();
    }
    public URI accountsURI(int start, int count) {
        Method getAccounts = null;
        for (Method m: AccountsRS.class.getMethods()) {
            if (m.getName().equals("getAccounts")) { 
                getAccounts = m;
                break;
            }
        }
        return uriInfo.getBaseUriBuilder()
                .path(AccountsRS.class)
                //.path(AccountsRS.class, "getAccounts")
                .path(getAccounts)
                .queryParam("start", start)
                .queryParam("count", count)
                .build();
    }
    public URI depositURI(int id) {
        return uriInfo.getBaseUriBuilder()
                .path(AccountsRS.class)
                .path(AccountsRS.class, "deposit")
                .build(id);
    }
    public URI withdrawURI(int id) {
        return uriInfo.getBaseUriBuilder()
                .path(AccountsRS.class)
                .path(AccountsRS.class, "withdraw")
                .build(id);
    }
    public URI transferURI() {
        return uriInfo.getBaseUriBuilder()
                .path(AccountsRS.class)
                .path(AccountsRS.class, "transfer")
                .build();
    }
}
