package practical.sorting;

import Models.Product;
import Models.comparators.ProductTypeComparator;
import Models.products.DigitalProduct;
import Models.products.Laptop;
import Models.products.PhysicalProduct;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class RankingLab {
    public static void main(String[] args) {
        Product sillaGamer = new PhysicalProduct(new BigInteger("12"), "Silla gamer", new BigDecimal("149.90"), new BigDecimal("9"));
        List<Product> lista = productT5();
        lista.add(sillaGamer);
        System.out.println("---- T5 · Paso 4 ----");
        List<Product> l4 = new ArrayList<>(lista);
        l4.sort(new ProductTypeComparator());
        imprimir(l4);
        // espero:
        /**
         * Producto	    Clase real	    ¿en qué if entra primero?	rango que recibe
         * ThinkPad 	Laptop          |   p instansof Laptop      | 1
         * Silla	    PhysicalProduct	|   p instansof Physi..     | 2
         * curso java	DigitalProduct	|   p instansof Digit..     | 3
         * MacBook	    Laptop		    |   p instansof Laptop      | 1
         * Mouse	    PhysicalProduct	|	                        | 2
         * audífonos	PhysicalProduct	|	                        | 2
         * Silla gamer	PhysicalProduct	|	                        | 2
         */

    }

    private static List<Product> productT5() {
        Product laptop = new Laptop(new BigInteger("40"), "ThinkPad", new BigDecimal("1200.00"), new BigDecimal("2.1"));
        Product silla = new PhysicalProduct(new BigInteger("12"), "Silla", new BigDecimal("89.90"), new BigDecimal("7.5"));
        Product curso = new DigitalProduct(new BigInteger("7"), "Curso java", new BigDecimal("49.00"), new BigDecimal("2.5"));
        Product mac = new Laptop(new BigInteger("25"), "MacBook", new BigDecimal("1999.99"), new BigDecimal("1.4"));
        Product mouse = new PhysicalProduct(new BigInteger("31"), "Mouse", new BigDecimal("49.00"), new BigDecimal("0.2"));
        Product audifonos = new PhysicalProduct(new BigInteger("18"), "Audifonos", new BigDecimal("89.90"), new BigDecimal("0.3"));
        List<Product> productList = new ArrayList<>();
        productList.add(laptop);
        productList.add(silla);
        productList.add(curso);
        productList.add(mac);
        productList.add(mouse);
        productList.add(audifonos);
        return productList;
    }

    private static void print(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }

    private static void imprimir(List<Product> l) {
        StringBuilder res = new StringBuilder();
        for (Product item : l) {
            res.append("nombre: " + item.getClass().getSimpleName() + " | ");
        }
        print(res);
    }
}
