package net.nihilanth.demo.guicedemo.named;

import com.google.inject.Inject;

import java.io.PrintWriter;

/**
 * A concrete implementation of type or interface.  One of several implementations.
 */
class DatabaseTransactionLog implements TransactionLog {
    private PrintWriter printWriter;

    @Inject
    public DatabaseTransactionLog() {
        this.printWriter = new PrintWriter(System.out, true);
        System.out.println("DatabaseTransactionLog.DatabaseTransactionLog");
    }

    @Override
    public void log(String transaction) {
        this.printWriter.println("DatabaseTransactionLog: " + transaction);
    }
}
