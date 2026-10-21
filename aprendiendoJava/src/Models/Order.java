package Models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class Order {
    private final BigInteger id;
    private Customer customer;
    private final Map<Product, OrderItem> orderItems;

    public Order(BigInteger id, Customer customer) {
        this.id = id;
        this.customer = customer;
        orderItems = new HashMap<>();
    }

    public BigDecimal getTotalPrice() {
        BigDecimal res = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems.values()) {
            res = res.add(orderItem.getSubTotalPrice());
        }
        return res;
    }

    public boolean addItem(Product product, int quantity) {
        OrderItem orderItem = new OrderItem(product, quantity);
        try {
            this.orderItems.put(product, orderItem);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public Map<Product, OrderItem> getItems() {
        return Collections.unmodifiableMap(orderItems);
    }

    public int getItemCount() {
        return orderItems.size();
    }

    public int getTotalUnits() {
        int totalQuantity = 0;
        for (OrderItem item : orderItems.values()) {
            totalQuantity += item.getQuantity();
        }
        return totalQuantity;
    }

    public boolean removeItem(Product product) {
        try {
            OrderItem removed = orderItems.remove(product);
            return removed != null ? true : false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEmpty() {
        return orderItems.isEmpty();
    }

    public Set<Product> getDistinctProduct() {
        Set<Product> products = new LinkedHashSet<>();
        for (OrderItem item : orderItems.values()) {
            products.add(item.getProduct());
        }
        return Collections.unmodifiableSet(products);
    }

    public Customer getCustomer() {
        return customer;
    }

    @Override
    public String toString(){
        return id+" = "+customer.toString()+" "+orderItems.toString();
    }
    /**
     * 1: bueno estoy sacando todos los productos en un for-each, almacenando dentro de un linkedHashSet ya que almacena en el orden de insercion ademas que solo esta insertando solo productos distintos.
     * 2: La forma de devolverlo prefiero devolver el copyof, para que el que lo recive pueda modificarlo sin modificar lo que esta en mi Order, solo lo que le di.
     */

}
