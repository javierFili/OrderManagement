package Models;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PhysicalProduct extends Product {
    private BigDecimal weight;
    public PhysicalProduct(BigInteger id, String name, BigDecimal price,BigDecimal weight,ShippingStragy shippingStragy) {
        super(id,name,price,shippingStragy);
        this.weight = weight;
    }
    public BigDecimal getWeight() {
        return weight;
    }
    public void setWeight(BigDecimal weight){
        this.weight = weight;
    }
    @Override
    public BigDecimal calculateShippingCost() {
        return super.shippingStragy.calculateShippingCost(this.weight);
    }

}
