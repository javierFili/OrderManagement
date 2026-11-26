package Models;

import Models.comparators.ProductNameComparator;
import Models.comparators.ProductPriceComparator;
import Models.comparators.ProductTypeComparator;

import java.math.BigInteger;
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

    public Optional<Product> getCheapest() {
        if (!products.isEmpty()) {
            Product productMin = getSortedByPrice().getLast();
            return Optional.of(productMin);
        }
        return Optional.empty();
    }

    private List<Product> sortedCopy(Comparator<Product> comparator) {
        List<Product> newproducts = new ArrayList<>(products);
        newproducts.sort(comparator);
        return Collections.unmodifiableList(newproducts);
    }

    public Optional<Product> findByName(String name) {

        for (Product item : products) {
            if (item.getName().equalsIgnoreCase(name)) {
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

    public Optional<Product> findById(BigInteger id) {
        for (Product item : products) {
            if (item.getId().equals(id)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

}
