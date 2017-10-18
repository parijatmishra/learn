package net.nihilanth.demo.guicedemo.justintime;

/**
 * A value or data transfer object.
 */
public class Receipt {
    private int amount;

    public Receipt(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "amount=" + amount +
                '}';
    }
}
