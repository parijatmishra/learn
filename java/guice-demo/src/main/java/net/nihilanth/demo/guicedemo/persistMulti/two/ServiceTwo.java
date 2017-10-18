package net.nihilanth.demo.guicedemo.persistMulti.two;

import com.google.inject.persist.PersistService;
import com.google.inject.persist.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * A service that does some database transaction.
 */
public class ServiceTwo {

    private EntityManager em;

    @Inject
    public ServiceTwo(PersistService persistService) {
        persistService.start();
        System.out.println("ServiceTwo.ServiceTwo");
    }

    @Inject
    public void setEm(EntityManager em) {
        this.em = em;
        System.out.println("ServiceTwo.setEm");
    }

    @Transactional
    public EntityTwo createEntityTwo(final String name) {
        EntityTwo o = new EntityTwo(name);
        em.persist(o);
        return o;
    }
}
