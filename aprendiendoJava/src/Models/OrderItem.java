package Models;

import java.math.BigDecimal;
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

    public BigDecimal getSubTotalPrice() {
        return product.getprice().multiply(BigDecimal.valueOf(quantity));
    }


}
