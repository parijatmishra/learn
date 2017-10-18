package net.nihilanth.demo.guicedemo.persistMulti;

import net.nihilanth.demo.guicedemo.persistMulti.one.EntityOne;
import net.nihilanth.demo.guicedemo.persistMulti.one.ServiceOne;
import net.nihilanth.demo.guicedemo.persistMulti.two.EntityTwo;
import net.nihilanth.demo.guicedemo.persistMulti.two.ServiceTwo;

import javax.inject.Inject;

/**
 * A service that orchestrates two other services.  Each of the other services
 * does a transactional database operation on its own data source.
 */
public class OrchestrationService {
    private final ServiceOne serviceOne;
    private final ServiceTwo serviceTwo;

    @Inject
    public OrchestrationService(ServiceOne serviceOne, ServiceTwo serviceTwo) {
        this.serviceOne = serviceOne;
        this.serviceTwo = serviceTwo;
        System.out.println("OrchestrationService.OrchestrationService");
    }

    public void process(final String nameE1, final String nameE2) {
        EntityOne entityOne = serviceOne.createEntityOne(nameE1);
        System.out.println(entityOne);
        EntityTwo entityTwo = serviceTwo.createEntityTwo(nameE2);
        System.out.println(entityTwo);
    }
}
