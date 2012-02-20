package ejava.ws.examples.hello.ejb31.rest;

import java.security.Identity;


import java.security.Principal;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.SessionContext;
import javax.ejb.TimerService;
import javax.transaction.UserTransaction;
import javax.xml.rpc.handler.MessageContext;

@SuppressWarnings("deprecation")
public class SessionContextStub implements SessionContext {
    protected Principal principal;
    
    public void setCallerPrincipal(String name) {
        this.principal = new PrincipalStub(name);        
    }

    public Identity getCallerIdentity() {
        return null;
    }

    public Principal getCallerPrincipal() {
        return principal;
    }

    public Map<String, Object> getContextData() {
        return null;
    }

    public EJBHome getEJBHome() {
        return null;
    }

    public EJBLocalHome getEJBLocalHome() {
        return null;
    }

    public Properties getEnvironment() {
        return null;
    }

    public boolean getRollbackOnly() throws IllegalStateException {
        return false;
    }

    public TimerService getTimerService() throws IllegalStateException {
        return null;
    }

    public boolean isCallerInRole(Identity arg0) {
        return false;
    }

    public boolean isCallerInRole(String arg0) {
        return false;
    }

    public Object lookup(String arg0) throws IllegalArgumentException {
        return null;
    }

    public void setRollbackOnly() throws IllegalStateException {
    }

    public <T> T getBusinessObject(Class<T> arg0) throws IllegalStateException {
        return null;
    }

    public EJBLocalObject getEJBLocalObject() throws IllegalStateException {
        return null;
    }

    public EJBObject getEJBObject() throws IllegalStateException {
        return null;
    }

    @SuppressWarnings("rawtypes")
    public Class getInvokedBusinessInterface() throws IllegalStateException {
        return null;
    }

    public boolean wasCancelCalled() throws IllegalStateException {
        return false;
    }

    public UserTransaction getUserTransaction() throws IllegalStateException {
        return null;
    }

    public MessageContext getMessageContext() throws IllegalStateException {
        return null;
    }
}
