import Models.*;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        Customer customer = new Customer(new BigInteger("1"), "John Doe", "javierfiligrana@gmail.com");

        Product laptp = new PhysicalProduct(new BigInteger("1"), "Laptop", new BigDecimal("1000"),new BigDecimal("43"));
        Product mouse = new PhysicalProduct(new BigInteger("2"), "Mouse", new BigDecimal("50"),new BigDecimal("54"));
        Product keyboard = new PhysicalProduct(new BigInteger("3"), "Keyboard", new BigDecimal("80"),new BigDecimal("62"));

        Product courseJava = new DigitalProduct(new BigInteger("4"), "Java Course", new BigDecimal("100"), new BigDecimal("100"));
        Product coursePython = new DigitalProduct(new BigInteger("5"), "Python Course", new BigDecimal("100"), new BigDecimal("100"));

        Order order = new Order(new BigInteger("1"),customer);

        order.addItem(laptp,3);
        order.addItem(mouse,4);
        order.addItem(keyboard,29);

        order.addItem(courseJava,4);
        order.addItem(coursePython,4);

        System.out.println(order.getTotalPrice());
    }
}

/**
 * 1: La herencia se encuentra en los dos nuevos productos creados, los fisicos y digitales.
 * 2: EL polimorfismo es en el metodo getTotalPrice
 * 3: El encapsulamiento se encuentra en los atributos de los productos y en el metodo getTotalPrice
 * 4: El abstraccion se encuentra en la clase Product y en la clase Order
 */