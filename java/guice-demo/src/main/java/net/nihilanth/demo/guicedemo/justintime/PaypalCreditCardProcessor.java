package net.nihilanth.demo.guicedemo.justintime;

/**
 * A concrete implementation of CreditCardProcessor interface
 */
public class PaypalCreditCardProcessor implements CreditCardProcessor {

    public PaypalCreditCardProcessor() {
        System.out.println("PaypalCreditCardProcessor.PaypalCreditCardProcessor");
    }

    @Override
    public void process(int amount) {
        System.out.println("PaypalCreditCardProcessor: processing [" + amount + "]");
    }
}
