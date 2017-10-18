package net.nihilanth.demo.guicedemo.justintime;

import com.google.inject.ImplementedBy;

/**
 * An interface that defines the requirements of the client class
 *
 * @ImplementedBy creates a JIT binding to
 * a default implementation.
 */
@ImplementedBy(DatabaseTransactionLog.class)
interface TransactionLog {
    void log(String transaction);
}
