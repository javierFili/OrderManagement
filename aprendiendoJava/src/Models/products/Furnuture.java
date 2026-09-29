package Models.products;

import Interfaces.Shippable;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Furnuture  extends PhysicalProduct implements Shippable {
    public Furnuture(BigInteger id, String name, BigDecimal price, BigDecimal weight){
        super(id,name,price,weight);
    }
    public BigDecimal calculateShippingCost(){
        return getPrice().add(getWeight());
    }
}
