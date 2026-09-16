package Models;

import java.math.BigInteger;

public class OrderItem {
    private Product product;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    public BigInteger getSubTotalPrice() {
        return product.getprice().multiply(BigInteger.valueOf(quantity));
    }


}
