package net.nihilanth.demo.guicedemo.named;

/**
 * A value or data transfer object.
 */
public class Order {
    private int amount;

    public Order(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
