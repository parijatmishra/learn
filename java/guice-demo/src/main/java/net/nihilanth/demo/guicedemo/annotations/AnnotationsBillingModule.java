package net.nihilanth.demo.guicedemo.annotations;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * A Google Guice "module" that contains type to implementation bindings for a particular bounded context
 */
public class AnnotationsBillingModule extends AbstractModule {

    /*
     * All the bindings are defined in the configure() method.
     *
     */
    @Override
    protected void configure() {
        // multiple implementations of a type are disambiguated with a binding annotation
        // the client can specify which implementation it prefers using one or the other annotation at the injection point
        bind(TransactionLog.class).annotatedWith(Local.class).to(DatabaseTransactionLog.class);
        bind(TransactionLog.class).annotatedWith(Remote.class).to(RemoteTransactionLog.class);
    }


    public static void main(String[] args) {
        /*
         * Illustrates how to create a Guice injector from a module, and then get top-level object instances from it.
         */
        Injector injector = Guice.createInjector(new AnnotationsBillingModule());
        BillingService billingService = injector.getInstance(BillingService.class);

        /*
         * Now that we have our instances, we can kick-off the processing chain
         */
        Order order = new Order(100);
        Receipt receipt = billingService.chargeOrder(order);
        System.out.println(receipt);

    }
}
