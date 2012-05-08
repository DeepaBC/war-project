package ejava.exercises.restdev.bank.rs;

import java.net.URI;


import javax.ws.rs.core.UriInfo;

import ejava.exercises.restdev.bank.dto.Bank;
import ejava.exercises.restdev.bank.dto.BankRepresentation;

/**
 * This class builds full URL hrefs for links within the bank
 */
public class BankRefs {
    protected UriInfo uriInfo;
    protected AccountRefs accounts;
    
    public BankRefs(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        accounts = new AccountRefs(uriInfo);
    }

    public URI setHRefs(Bank bank) {
        URI self = selfURI();
        for (BankRepresentation.Link link : bank.getLinks()) {
            if (link.getHref() == null) {
                if (BankRepresentation.SELF_REL.equals(link.getRel())) {
                    link.setHref(self);
                }
                else if (BankRepresentation.ACCOUNTS_REL.equals(link.getRel())) {
                    link.setHref(accounts.accountsURI());
                }
                else if (BankRepresentation.TRANSFER_REL.equals(link.getRel())) {
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
