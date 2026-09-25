package Models;

import java.math.BigDecimal;

public class NoShipping implements ShippingStragy {
    @Override
    public BigDecimal calculateShippingCost(BigDecimal weight) {
        return BigDecimal.ZERO;
    }
}
