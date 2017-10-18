package net.nihilanth.demo.guicedemo.named;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * A Google Guice "module" that contains type to implementation bindings for a particular bounded context
 */
public class NamedBillingModule extends AbstractModule {

    /*
     * All the bindings are defined in the configure() method.
     *
     */
    @Override
    protected void configure() {
        // multiple implementations of a type are disambiguated with a binding annotation
        // the client can specify which implementation it prefers using one or the other annotation at the injection point
        bind(TransactionLog.class).annotatedWith(Names.named("local")).to(DatabaseTransactionLog.class);
        bind(TransactionLog.class).annotatedWith(Names.named("remote")).to(RemoteTransactionLog.class);
    }


    public static void main(String[] args) {
        /*
         * Illustrates how to create a Guice injector from a module, and then get top-level object instances from it.
         */
        Injector injector = Guice.createInjector(new NamedBillingModule());
        BillingService billingService = injector.getInstance(BillingService.class);

        /*
         * Now that we have our instances, we can kick-off the processing chain
         */
        Order order = new Order(100);
        Receipt receipt = billingService.chargeOrder(order);
        System.out.println(receipt);

    }
}
