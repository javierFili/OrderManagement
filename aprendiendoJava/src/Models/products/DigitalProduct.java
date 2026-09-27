package Models.products;

import Interfaces.Downloadable;
import Models.Product;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DigitalProduct extends Product implements Downloadable {
    private BigDecimal fileSize;
    public DigitalProduct(BigInteger id, String name, BigDecimal price, BigDecimal fileSize) {
        super(id,name,price);
        this.fileSize = fileSize;
    }
    public BigDecimal getFileSize() {
        return fileSize;
    }
    public void setFileSize(BigDecimal fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String download() {
        return "asdf";
    }

}
