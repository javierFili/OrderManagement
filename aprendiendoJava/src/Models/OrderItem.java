package Models;

import java.math.BigInteger;

public class OrderItem {
    private Product product;
    private int quantity;

    public OrderItem(Product product,int quantity){
        this.product = product;
        this.quantity = quantity;
    }

    public BigInteger getSubTotalPrice(){
        return product.getprice().multiply(BigInteger.valueOf(quantity));
    }

}
