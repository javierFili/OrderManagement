package Models.comparators;

import Models.Product;

import java.util.Comparator;

public class ProductNameComparator implements Comparator<Product> {

    @Override
    public int compare(Product product1,Product product){
        return product1.getName().compareToIgnoreCase(product.getName());
    }
}
