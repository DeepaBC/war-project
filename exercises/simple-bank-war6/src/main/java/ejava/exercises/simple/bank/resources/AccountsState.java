package ejava.exercises.simple.bank.resources;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import ejava.exercises.simple.bank.dto.Account;
import ejava.exercises.simple.bank.dto.BankRepresentation;

/**
 * This class builds full URL hrefs for links within the accounts.
 */
public class AccountsState {
    protected UriInfo uriInfo;
    
    public AccountsState(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public URI setHRefs(Account account) {
        URI self = selfURI(account.getId());
        for (BankRepresentation.Link link : account.getLinks()) {
            if (link.getHref() == null) {
                if (BankRepresentation.SELF_REL.equals(link.rel)) {
                    link.setHref(selfURI(account.getId()));
                }
                else if (BankRepresentation.ACCOUNTS_REL.equals(link.rel)) {
                    link.setHref(accountsURI());
                }
                else if (BankRepresentation.DEPOSIT_REL.equals(link.rel)) {
                    link.setHref(depositURI(account.getId()));
                }
                else if (BankRepresentation.WITHDRAW_REL.equals(link.rel)) {
                    link.setHref(withdrawURI(account.getId()));
                }
                else if (BankRepresentation.TRANSFER_REL.equals(link.rel)) {
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
