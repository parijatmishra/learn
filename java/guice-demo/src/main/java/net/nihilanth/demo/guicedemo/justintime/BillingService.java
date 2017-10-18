package net.nihilanth.demo.guicedemo.justintime;


import javax.inject.Inject;

/**
 * A hypothetical top-level class that depends on other helpers, using interfaces to describe its requirements
 */
class BillingService {
    private final TransactionLog transactionLog;
    private final CreditCardProcessor processor;

    /*
     * Expresses the dependency between this class and the TransactionLog type using constructor injection.
     */
    @Inject
    public BillingService(TransactionLog transactionLog, CreditCardProcessor processor) {
        this.processor = processor;
        this.transactionLog = transactionLog;
        System.out.println("BillingService.BillingService");
    }

    public Receipt chargeOrder(Order order) {
        /*
         * Use the injected dependency
         */
        processor.process(order.getAmount());
        transactionLog.log("Transaction: " + order.getAmount());
        return new Receipt(order.getAmount());
    }
}
