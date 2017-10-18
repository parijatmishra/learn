package net.nihilanth.demo.guicedemo.linked;

import javax.inject.Inject;
import java.io.PrintWriter;

/**
 * A concrete implementation of type or interface.
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
