package net.nihilanth.demo.guicedemo.providerBindings;


import javax.inject.Inject;
import javax.inject.Named;

/**
 * A hypothetical top-level class that depends on other helpers, using interfaces to describe its requirements
 */
class BillingService {
    private TransactionLog transactionLog;

    /*
     * Expresses the dependency between this class and the TransactionLog type using constructor injection.
     */
    @Inject
    public BillingService(@Named("local") TransactionLog transactionLog) {
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
