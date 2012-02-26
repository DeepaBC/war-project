package ejava.ws.util.test;

import java.security.Principal;

/**
 * This class is used to simulate the java.security.Principal class
 * for the EJB while being unit tested.
 */
public class PrincipalStub implements Principal {
    protected String name;

    public PrincipalStub(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }

}
