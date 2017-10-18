package net.nihilanth.demo.guicedemo.providerBindings;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

/**
 * A Google Guice "module" that contains type to implementation bindings for a particular bounded context
 */
public class ProviderBindingsBillingModule extends AbstractModule {

    /*
     * All the bindings are defined in the configure() method.
     */
    @Override
    protected void configure() {
        bind(TransactionLog.class)
                .annotatedWith(Names.named("local"))
                .toProvider(DatabaseTransactionLogProvider.class);
        bind(String.class)
                .annotatedWith(Names.named("JDBC_URL"))
                .toInstance("jdbc:mysql://localhost/db");
    }

    public static void main(String[] args) {
        /*
         * Illustrates how to create a Guice injector from a module, and then get top-level object instances from it.
         */
        Injector injector = Guice.createInjector(new ProviderBindingsBillingModule());
        BillingService billingService = injector.getInstance(BillingService.class);

        /*
         * Now that we have our instances, we can kick-off the processing chain
         */
        Order order = new Order(100);
        Receipt receipt = billingService.chargeOrder(order);
        System.out.println(receipt);

    }
}
