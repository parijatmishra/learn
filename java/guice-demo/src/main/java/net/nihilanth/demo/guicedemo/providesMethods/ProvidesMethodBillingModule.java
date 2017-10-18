package net.nihilanth.demo.guicedemo.providesMethods;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

import javax.inject.Named;


/**
 * A Google Guice "module" that contains type to implementation bindings for a particular bounded context
 */
public class ProvidesMethodBillingModule extends AbstractModule {

    /*
     * All the bindings are defined in the configure() method.
     */
    @Override
    protected void configure() {

    }

    @Provides
    @Named("local")
    TransactionLog provideTransactionLog() {
        DatabaseTransactionLog txlog = new DatabaseTransactionLog("jdbc:mysql://localhost/seomdb");
        return txlog;
    }

    public static void main(String[] args) {
        /*
         * Illustrates how to create a Guice injector from a module, and then get top-level object instances from it.
         */
        Injector injector = Guice.createInjector(new ProvidesMethodBillingModule());
        BillingService billingService = injector.getInstance(BillingService.class);

        /*
         * Now that we have our instances, we can kick-off the processing chain
         */
        Order order = new Order(100);
        Receipt receipt = billingService.chargeOrder(order);
        System.out.println(receipt);

    }
}
