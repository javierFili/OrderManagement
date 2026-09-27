package Models.products;

import Interfaces.Shippable;
import Models.Product;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PhysicalProduct extends Product implements Shippable {
    private BigDecimal weight;
    public PhysicalProduct(BigInteger id, String name, BigDecimal price, BigDecimal weight) {
        super(id,name,price);
        this.weight = weight;
    }
    public BigDecimal getWeight() {
        return weight;
    }
    public void setWeight(BigDecimal weight){
        this.weight = weight;
    }
    @Override
    public BigDecimal shippingCost(BigDecimal cost) {
        return cost.add(super.getprice());
    }

}
