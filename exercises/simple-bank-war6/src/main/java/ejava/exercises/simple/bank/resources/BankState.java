package ejava.exercises.simple.bank.resources;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import ejava.exercises.simple.bank.dto.Account;
import ejava.exercises.simple.bank.dto.Bank;
import ejava.exercises.simple.bank.dto.BankRepresentation;

/**
 * This class builds full URL hrefs for links within the bank
 */
public class BankState {
    protected UriInfo uriInfo;
    protected AccountsState accounts;
    
    public BankState(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public URI setHRefs(Bank bank) {
        URI self = selfURI();
        for (BankRepresentation.Link link : bank.getLinks()) {
            if (link.getHref() == null) {
                if (BankRepresentation.SELF_REL.equals(link.rel)) {
                    link.setHref(self);
                }
                else if (BankRepresentation.ACCOUNTS_REL.equals(link.rel)) {
                    link.setHref(accounts.accountsURI());
                }
                else if (BankRepresentation.TRANSFER_REL.equals(link.rel)) {
                    link.setHref(accounts.transferURI());
                }
            }
        }
        return self;
    }

    public URI selfURI() {
        return uriInfo.getBaseUriBuilder()
                .path(BankRS.class)
                .build();
    }
}
