package Models.comparators;

import Models.Product;
import Models.products.DigitalProduct;
import Models.products.Laptop;
import Models.products.PhysicalProduct;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

public class ProductTypeComparator implements Comparator<Product> {

    @Override
    public int compare(Product product1, Product product2) {

        return Integer.compare(rank(product1), rank(product2));
    }

    private int rank(Product p) {
        int res = 4;
        if (p instanceof Laptop) {
            res = 1;
        }
        if (p instanceof PhysicalProduct) {
            res = 2;
        }
        if (p instanceof DigitalProduct) {
            res = 3;
        }
        return res;
    }

}
