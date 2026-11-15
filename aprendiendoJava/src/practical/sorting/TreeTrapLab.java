package practical.sorting;

import Models.Product;
import Models.products.DigitalProduct;
import Models.products.Laptop;
import Models.products.PhysicalProduct;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TreeTrapLab {
    public static void main(String args[]) {
        Product laptop = new Laptop(new BigInteger("40"), "ThinkPad", new BigDecimal("1200.00"), new BigDecimal("2.1"));
        Product silla = new PhysicalProduct(new BigInteger("12"), "Silla", new BigDecimal("89.90"), new BigDecimal("7.5"));
        Product curso = new DigitalProduct(new BigInteger("7"), "Curso java", new BigDecimal("49.00"), new BigDecimal("2.5"));
        Product mac = new Laptop(new BigInteger("25"), "MacBook", new BigDecimal("1999.99"), new BigDecimal("1.4"));
        Product mouse = new PhysicalProduct(new BigInteger("31"), "Mouse", new BigDecimal("49.00"), new BigDecimal("0.2"));
        Product audifonos = new PhysicalProduct(new BigInteger("18"), "Audifonos", new BigDecimal("89.90"), new BigDecimal("0.3"));
    }

}
