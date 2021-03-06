package net.nihilanth.demo.guicedemo.named;


import javax.inject.Inject;
import javax.inject.Named;

/**
 * A hypothetical top-level class that depends on other helpers, using interfaces to describe its requirements
 */
class BillingService {
    private TransactionLog transactionLog;

    /*
     * Expresses the dependency between this class and the TransactionLog type using constructor injection.
     *
     * Chooses between alternative implementations using the @Named("local") or @Named("remote") annotation.  Change the annotation
     * and run NamedBillingModule to see the difference.
     *
     * If the annotation is not present, Guice will throw an error.
     */
    @Inject
    public BillingService(@Named("remote") TransactionLog transactionLog) {
        this.transactionLog = transactionLog;
        System.out.println("BillingService.BillingService");
    }

    public Receipt chargeOrder(Order order) {
        /*
         * Use the injected dependency
         */
        transactionLog.log("Transaction: " + order.getAmount());
        return new Receipt(order.getAmount());
    }
}
