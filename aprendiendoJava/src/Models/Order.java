package Models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class Order {
    private BigInteger id;
    private Customer customer;
    private final List<OrderItem> orderItems;

    public Order(BigInteger id, Customer customer) {
        this.id = id;
        this.customer = customer;
        orderItems = new ArrayList<>();
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
        return List.copyOf(orderItems);// es que a este es al que entiendo mejor ademas que no se como usar el otro.
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

    public boolean isEmpty(){
        return orderItems.isEmpty();
    }

    public Set<Product> getDistinctProduct(){
        Set<Product> products = new LinkedHashSet<>();
        for (OrderItem item:orderItems){
            products.add(item.getProduct());
        }
        //return Collections.unmodifiableSet(products);
        return Set.copyOf(products);
    }
    /**
     * 1: bueno estoy sacando todos los productos en un for-each, almacenando dentro de un linkedHashSet ya que almacena en el orden de insercion ademas que solo esta insertando solo productos distintos.
     * 2: La forma de devolverlo prefiero devolver el copyof, para que el que lo recive pueda modificarlo sin modificar lo que esta en mi Order, solo lo que le di.
     */

}
