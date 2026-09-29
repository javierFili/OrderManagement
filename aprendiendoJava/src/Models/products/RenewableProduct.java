package Models.products;

import Interfaces.Renewable;
import Models.Product;
import objects.Renewal;
import objects.RenewalStatus;

import java.math.BigDecimal;
import java.math.BigInteger;

public class RenewableProduct extends Product implements Renewable {
    private int months;

    public RenewableProduct(BigInteger id, String name, BigDecimal price, int months) {
        super(id, name, price);
        this.months = months;
    }

    @Override
    public Renewal renew() {
        return new Renewal();
    }

    @Override
    public RenewalStatus getRenewalStatus() {
        return RenewalStatus.ACTIVE;
    }
}
