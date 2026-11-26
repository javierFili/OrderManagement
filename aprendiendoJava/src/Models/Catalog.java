package Models;

import Models.comparators.ProductNameComparator;
import Models.comparators.ProductPriceComparator;
import Models.comparators.ProductTypeComparator;
import org.w3c.dom.ls.LSInput;

import java.util.*;

public class Catalog {
    private final List<Product> products = new ArrayList<>();

    public boolean addProduct(Product product) {
        if (product == null) {
            return false;
        }
        if (products.contains(product)) {
            return true;
        }
        return products.add(product);
    }

    public List<Product> getProducts() {
        return List.copyOf(products);
    }

    public List<Product> getSortedByPrice() {
        return sortedCopy(new ProductPriceComparator());
    }

    public List<Product> getSortedByName() {
        return sortedCopy(new ProductNameComparator());
    }

    public List<Product> getSortedByType() {
        return sortedCopy(new ProductTypeComparator());
    }

    public Product getCheapest() throws IllegalAccessException {
        if (!products.isEmpty()) {
            return Collections.min(products);
        }
        throw new IllegalAccessException("el catalogo esta vacio");
    }

    private List<Product> sortedCopy(Comparator<Product> comparator) {
        List<Product> newproducts = new ArrayList<>(products);
        newproducts.sort(comparator);
        return Collections.unmodifiableList(newproducts);
    }
}
