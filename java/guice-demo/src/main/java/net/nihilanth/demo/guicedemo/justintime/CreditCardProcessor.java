package net.nihilanth.demo.guicedemo.justintime;

import com.google.inject.ProvidedBy;

/**
 * An interface that defines the requirements of the client class.
 *
 * @ProvidedBy creates an JIT binding to a default Provider.
 */
@ProvidedBy(PaypalCreditCardProcessorProvider.class)
public interface CreditCardProcessor {
    public void process(int amount);
}
