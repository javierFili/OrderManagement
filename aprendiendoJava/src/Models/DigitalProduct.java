package Models;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DigitalProduct extends Product {
    private BigDecimal fileSize;
    public DigitalProduct(BigInteger id, String name, BigDecimal price, BigDecimal fileSize,ShippingStragy shippingStragy) {
        super(id,name,price,shippingStragy);
        this.fileSize = fileSize;
    }
    public BigDecimal getFileSize() {
        return fileSize;
    }
    public void setFileSize(BigDecimal fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public BigDecimal calculateShippingCost() {
        return super.shippingStragy.calculateShippingCost(new BigDecimal("0"));
    }

}
