import Models.Customer;
import Models.Order;
import Models.OrderItem;
import Models.Product;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        Customer customer = new Customer(new BigInteger("1"), "John Doe", "javierfiligrana@gmail.com");

        Product laptp = new Product(new BigInteger("1"), "Laptop", new BigInteger("1000"));
        Product mouse = new Product(new BigInteger("2"), "Mouse", new BigInteger("50"));
        Product keyboard = new Product(new BigInteger("3"), "Keyboard", new BigInteger("80"));

        Order order = new Order(new BigInteger("1"),customer);

        order.addItem(laptp,3);
        order.addItem(mouse,4);
        order.addItem(keyboard,29);

        System.out.println(order.getTotalPrice());
    }
}