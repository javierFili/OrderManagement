package Models;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PhysicalProduct extends Product {
    private BigDecimal weight;
    public PhysicalProduct(BigInteger id, String name, BigDecimal price,BigDecimal weight) {
        super(id,name,price);
        this.weight = weight;
    }
    public BigDecimal getWeight() {
        return weight;
    }
    public void setWeight(BigDecimal weight){
        this.weight = weight;
    }
}
