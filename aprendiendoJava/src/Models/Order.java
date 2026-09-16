package Models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private BigInteger id;
    private Customer customer;
    private List<OrderItem> orderItems;

    public Order(BigInteger id, Customer customer) {
        this.id = id;
        this.customer = customer;
        orderItems = new ArrayList<OrderItem>();
    }

    public BigDecimal getTotalPrice() {
        BigDecimal res = new BigDecimal("0");
        for (OrderItem orderItem : orderItems) {
            res= res.add(orderItem.getSubTotalPrice());
        }
        return res;
    }

    public boolean addItem(Product product,int quantity) {
        OrderItem orderItem = new OrderItem(product,quantity);

        return this.orderItems.add(orderItem);
    }

}
