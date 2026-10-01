package practical.lists;

import Interfaces.Shippable;
import Models.Product;
import Models.products.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ProductListLab {
    public static void main(String args[]) {
        System.out.println("1---------------");
        List<Product> products = new ArrayList<>();
        products.add(new Laptop(new BigInteger("1"), "Laptop", new BigDecimal("3452.4"), new BigDecimal("32")));
        products.add(new PhysicalBook(new BigInteger("2"), "book2", new BigDecimal("4005.3"), new BigDecimal("34")));
        products.add(new Furnuture(new BigInteger("3"), "char", new BigDecimal("123.43"), new BigDecimal("33")));
        products.add(new DigitalProduct(new BigInteger("4"), "course java", new BigDecimal("213.43"), new BigDecimal("32")));
        products.add(new RenewableProduct(new BigInteger("5"), "netflix", new BigDecimal("342.2"), 3));
        products.add(new ConsultingService(new BigInteger("6"), "casas", new BigDecimal("824")));
        System.out.println("2---------------");
        System.out.println(products);

        System.out.println("3----------------");
        for (Product p : products) {
            System.out.println("N." + p.getName() + " - " + p.getPrice());
        }

        System.out.println("4----------");
        BigDecimal sumtotal = BigDecimal.ZERO;
        for (Product p : products) {
            sumtotal = sumtotal.add(p.getPrice());
        }
        System.out.println(sumtotal);

        System.out.println("5----------------------");

        Product mostCost = products.get(0);
        for (Product p : products) {
            int compare = p.getPrice().compareTo(mostCost.getPrice());
            if (compare >= 1) {
                mostCost = p;
            }
        }
        System.out.println(mostCost);

        System.out.println("6-------------");

        List<Product> expensive = new ArrayList<>();
        for (Product product : products) {
            if (product.getPrice().compareTo(new BigDecimal("500")) >= 1) {
                expensive.add(product);
            }
        }
        System.out.println(expensive);
        System.out.println("7-------------");
        Product find = findByName(products, "netflix");
        System.out.println(find);

        Product dontFind = findByName(products, "nadaaa");

        System.out.println(dontFind);
        System.out.println("8-------------");
        List<Shippable> listOnlyShippable = onlyShippable(products);
        System.out.println(listOnlyShippable);

    }

    private static Product findByName(List<Product> list, String name) {
        if (list.size() <= 0) {
            throw new IllegalArgumentException("necesitmaos que sea una lista con elmentos.");
        }

        for (Product product : list) {
            if (product.getName().equals(name)) {
                return product;
            }
        }

        return null;
    }

    private static List<Shippable> onlyShippable(List<Product> list) {
        List<Shippable> listShippable = new ArrayList<>();
        for (Product product : list) {
            if (product instanceof Shippable) {
                listShippable.add((Shippable) product);
            }
        }
        return listShippable;
    }
}
