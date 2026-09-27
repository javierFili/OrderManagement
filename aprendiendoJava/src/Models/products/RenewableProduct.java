package Models.products;

import Interfaces.Renewable;
import Models.Product;

import java.math.BigDecimal;
import java.math.BigInteger;

public class RenewableProduct extends Product implements Renewable {
    private int months;

    public RenewableProduct(BigInteger id, String name, BigDecimal price, int months) {
        super(id, name, price);
        this.months = months;
    }
    @Override
    public boolean renewableProduct(BigDecimal months){
        return  true;
    }
}
