package Models;

import java.math.BigDecimal;

public class PhysicalShipping implements ShippingStragy{
    @Override
    public BigDecimal calculateShippingCost(BigDecimal weight){
        return weight.multiply(new BigDecimal("3"));
    }
}
