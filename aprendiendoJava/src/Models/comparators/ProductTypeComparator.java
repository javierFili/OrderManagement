package Models.comparators;

import Models.Product;
import Models.products.DigitalProduct;
import Models.products.Laptop;
import Models.products.PhysicalProduct;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ProductTypeComparator {
    //✅
    public static void main(String[] args) {
        Product laptop = new Laptop(new BigInteger("40"), "ThinkPad", new BigDecimal("1200.00"), new BigDecimal("2.1"));
        Product silla = new PhysicalProduct(new BigInteger("12"), "Silla", new BigDecimal("89.90"), new BigDecimal("7.5"));
        Product curso = new DigitalProduct(new BigInteger("7"), "Curso java", new BigDecimal("49.00"), new BigDecimal("2.5"));
        Product mac = new Laptop(new BigInteger("25"), "MacBook", new BigDecimal("1999.99"), new BigDecimal("1.4"));


    }

    private int rank(Product p) {
        return 0;
    }

    private static void print(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }
}
