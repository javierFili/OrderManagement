package Models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private BigInteger id;
    private Customer customer;
    private final List<OrderItem> orderItems;

    public Order(BigInteger id, Customer customer) {
        this.id = id;
        this.customer = customer;
        orderItems = new ArrayList<OrderItem>();
    }

    public BigDecimal getTotalPrice() {
        BigDecimal res = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            res = res.add(orderItem.getSubTotalPrice());
        }
        return res;
    }

    public boolean addItem(Product product, int quantity) {
        OrderItem orderItem = new OrderItem(product, quantity);

        return this.orderItems.add(orderItem);
    }

    public List<OrderItem> getItems() {
        return List.copyOf(orderItems);
    }

    public int getItemCount() {
        return orderItems.size();
    }

    public int getTotalUnits() {
        int totalQuantity = 0;
        for (OrderItem item : orderItems) {
            totalQuantity += item.getQuantity();
        }
        return totalQuantity;
    }

    public boolean removeItem(int index) {
        if (index > orderItems.size() - 1 || index < 0) {
            return false;
        }
        orderItems.remove(index);
        return true;
    }

}
