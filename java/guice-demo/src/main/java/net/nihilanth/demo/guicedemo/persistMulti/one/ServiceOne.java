package net.nihilanth.demo.guicedemo.persistMulti.one;

import com.google.inject.persist.PersistService;
import com.google.inject.persist.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * A service that does some database transaction.
 */
public class ServiceOne {
    private EntityManager em;

    @Inject
    public ServiceOne(PersistService persistService) {
        persistService.start();
        System.out.println("ServiceOne.ServiceOne");
    }

    @Inject
    public void setEm(EntityManager em) {
        System.out.println("ServiceOne.setEm");
        this.em = em;
    }

    @Transactional
    public EntityOne createEntityOne(final String name) {
        EntityOne o = new EntityOne(name);
        em.persist(o);
        return o;
    }
}
