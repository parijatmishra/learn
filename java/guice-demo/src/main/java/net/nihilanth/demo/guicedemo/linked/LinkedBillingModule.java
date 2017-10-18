package net.nihilanth.demo.guicedemo.linked;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * A Google Guice "module" that contains type to implementation bindings for a particular bounded context
 */
public class LinkedBillingModule extends AbstractModule {

    /*
     * All the bindings are defined in the configure() method.
     */
    @Override
    protected void configure() {
        bind(TransactionLog.class).to(DatabaseTransactionLog.class);
    }


    public static void main(String[] args) {
        /*
         * Illustrates how to create a Guice injector from a module, and then get top-level object instances from it.
         */
        Injector injector = Guice.createInjector(new LinkedBillingModule());
        BillingService billingService = injector.getInstance(BillingService.class);

        /*
         * Now that we have our instances, we can kick-off the processing chain
         */
        Order order = new Order(100);
        Receipt receipt = billingService.chargeOrder(order);
        System.out.println(receipt);

    }
}
