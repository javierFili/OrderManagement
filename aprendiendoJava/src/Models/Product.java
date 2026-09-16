package Models;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Product {
    private BigInteger id;
    private String name;
    private BigDecimal price;

    public Product(BigInteger id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        if (!this.isValidValue(price)) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getprice() {
        return this.price;
    }

    public boolean setPrice(BigDecimal price) {
        if (this.isValidValue(price)) {
            this.price = price;
            return true;
        }
        return false;
    }

    private boolean isValidValue(BigDecimal price) {
        return price.longValueExact() >= 0;
    }
}
