package ejava.examples.jaxrsscale.concurrency;

import java.util.Date;

import javax.inject.Singleton;

import ejava.examples.jaxrsscale.concurrency.dto.ConcurrencyCheck;

/**
 * This class implements a concurrently accessed singleton.
 */
@Singleton
public class ConcurrentServiceImpl implements ConcurrentService {
    private ConcurrencyCheck value;
    
    public ConcurrentServiceImpl() {
        value = new ConcurrencyCheck();
        value.setToken(0);
        value.setModifiedDate(new Date());
        value.setModifier("original");
    }

    @Override
    public ConcurrencyCheck get() {
        synchronized (value) {
            ConcurrencyCheck val = new ConcurrencyCheck();
            val.setModifiedDate(value.getModifiedDate());
            val.setModifier(value.getModifier());
            val.setToken(value.getToken());
            return val;
        }
    }

    @Override
    public void set(ConcurrencyCheck update) {
        synchronized (value) {
            update.setModifiedDate(new Date());
            value.setModifiedDate(update.getModifiedDate());
            value.setModifier(update.getModifier());
            value.setToken(update.getToken());
        }
    }
}
