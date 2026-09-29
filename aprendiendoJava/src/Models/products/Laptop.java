package Models.products;

import Interfaces.Shippable;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Laptop extends PhysicalProduct implements Shippable {
    public Laptop(BigInteger id, String name, BigDecimal price, BigDecimal weight){
        super(id,name,price,weight);
    }
    public BigDecimal calculateShippingCost(){
        return getPrice().add(getWeight());
    }
}
