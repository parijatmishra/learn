package net.nihilanth.demo.guicedemo.providerBindings;


import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

/**
 * a Guice Provider class
 */
public class DatabaseTransactionLogProvider implements Provider<TransactionLog> {
    private final String jdbcUrl;

    @Inject
    public DatabaseTransactionLogProvider(@Named("JDBC_URL") String jdbcUrl) {
        System.out.println("DatabaseTransactionLogProvider.DatabaseTransactionLogProvider: [" + jdbcUrl + "]");
        this.jdbcUrl = jdbcUrl;
    }

    @Override
    public TransactionLog get() {
        System.out.println("DatabaseTransactionLogProvider.get");
        return new DatabaseTransactionLog(this.jdbcUrl);
    }
}
