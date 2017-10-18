package net.nihilanth.demo.guicedemo.named;

/**
 *  A concrete implementation of type or interface.  One of several implementations.
 */
public class RemoteTransactionLog implements TransactionLog {
    public RemoteTransactionLog() {
        System.out.println("RemoteTransactionLog.RemoteTransactionLog");
    }

    @Override
    public void log(String transaction) {
        System.out.println("RemoteTransactionLog: " + transaction);
    }
}
