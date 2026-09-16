package Models;

import java.math.BigInteger;
import java.util.List;

public class Order {
    private BigInteger id;
    private Customer customer;
    private List<OrderItem> orderItems;

    public Order(BigInteger id, Customer customer, List<OrderItem> orderItems) {
        this.id = id;
        this.customer = customer;
        this.orderItems = orderItems;
    }

    public BigInteger getTotalPrice() {
        BigInteger res = new BigInteger("0");
        for (OrderItem orderItem : orderItems) {
            res  = res.add(orderItem.getSubTotalPrice());
        }
        return res;
    }

    public boolean addItem(OrderItem orderItem) {
        return orderItems.add(orderItem);
    }

}
