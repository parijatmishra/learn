package net.nihilanth.demo.guicedemo.providesMethods;

import java.io.PrintWriter;

/**
 * A concrete implementation of type or interface.
 */
class DatabaseTransactionLog implements TransactionLog {
    private PrintWriter printWriter;
    private String jdbcUrl;

    public DatabaseTransactionLog(String jdbcUrl) {
        this.printWriter = new PrintWriter(System.out, true);
        this.jdbcUrl = jdbcUrl;
        System.out.println("DatabaseTransactionLog.DatabaseTransactionLog: [" + jdbcUrl + "]");
    }

    @Override
    public void log(String transaction) {
        this.printWriter.println("DatabaseTransactionLog: " + transaction);
    }
}
