package net.nihilanth.demo.guicedemo.justintime;


import com.google.inject.Provider;

/**
 * A Guice Provider class
 */
public class PaypalCreditCardProcessorProvider implements Provider<CreditCardProcessor> {
    public PaypalCreditCardProcessorProvider() {
        System.out.println("PaypalCreditCardProcessorProvider.PaypalCreditCardProcessorProvider");
    }

    @Override
    public CreditCardProcessor get() {
        System.out.println("PaypalCreditCardProcessorProvider.get");
        return new PaypalCreditCardProcessor();
    }
}
