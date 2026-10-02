package Models;

import java.math.BigDecimal;
import java.math.BigInteger;

public class OrderItem {
    private Product product;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        if(product==null){
            throw  new IllegalArgumentException("No puede ser un producto nullo");
        }
        this.product = product;
        if (quantity <= 0) {
            throw new IllegalArgumentException("Tiene que existe un cantidad de productos mayor a 0");
        }
        this.quantity = quantity;
    }

    public BigDecimal getSubTotalPrice() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public Product getProduct(){
        return product;
    }

    public int getQuantity(){
        return quantity;
    }
}
