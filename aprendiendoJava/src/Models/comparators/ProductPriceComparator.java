package Models.comparators;

import Models.Product;

import java.util.Comparator;

public class ProductPriceComparator implements Comparator<Product> {
    @Override
    public int compare(Product product1, Product product2) {
        return product1.getPrice().compareTo(product2.getPrice());
    }
}
