package practical.sorting;

import Models.Product;
import Models.comparators.ProductPriceComparator;
import Models.products.DigitalProduct;
import Models.products.Laptop;
import Models.products.PhysicalProduct;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class TreeTrapLab {
    public static void main(String args[]) {
        TreeSet<Product> porPrecio = new TreeSet<>(new ProductPriceComparator());
        List<Product> productList = productT3();
        for (Product item : productList) {
            print(porPrecio.add(item));
        }
        /**
         * add de	¿ya había alguien con ese precio?	espero (true/false)	 real
         * thinkpad        no                      |       true        | true ✅
         * silla	       si audifonos            |       true        | true ✅
         * curso          si  Mouse                |       true        | true ✅
         * mac            no                       |       true        | true ✅
         * mouse          si curso                 |       false       | false ✅
         * audifonos      si silla                 |       false       | false ✅
         */
        print(porPrecio.size());
        // espero: 4 ✅
        Product fantasma = new DigitalProduct(new BigInteger("99"), "Fantasma", new BigDecimal("49"), new BigDecimal("1"));
        print(porPrecio.contains(fantasma));
        // espero: false
        // real: true
        // por que: como estamos ordenaod por precio entonces 49 es igual a 49.00

        print(productT3().contains(fantasma));
        // espero: false ✅

        print(new HashSet<>(productT3()).contains(fantasma));
        //espero: false ✅

        print("------5-----");
        TreeSet<Product> arreglado = new TreeSet<>(new ProductPriceComparator().thenComparing(Comparator.naturalOrder()));
        arreglado.addAll(productT3());
        print(arreglado.size());
        // espero: 6 ✅
        print("------6-----");
        Product curso = new DigitalProduct(new BigInteger("7"), "Curso java", new BigDecimal("49.00"), new BigDecimal("2.5"));
        Product mouse = new PhysicalProduct(new BigInteger("31"), "Mouse", new BigDecimal("49.00"), new BigDecimal("0.2"));

        Map<Product, Integer> stock = new TreeMap<>(new ProductPriceComparator());
        print(stock.put(curso, 10));
        // espero: null

        print(stock.put(mouse, 3));
        // espero: 10
        print(stock.size());
        //espero: 1

        for (Map.Entry<Product, Integer> item : stock.entrySet()) {
            StringBuilder res = new StringBuilder();
            res.append(item.getKey().getName()+" = "+item.getValue().toString());
            print(res);
        }
        // espero: Curso java = 3

    }

    private static List<Product> productT3() {
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
}

/**
 * P4.1 "Un TreeSet decide que un elemento 'ya está' cuando en el equals ya existe, no cuando es otra cosa."
 * P4.2 "contains(fantasma) dio true en el TreeSet y false en el HashSet, porque el TreeSet compara price y el HashSet compara hascode."
 * P4.3 "En el TreeMap del paso 6, el segundo put devolvió 10, la clave que quedó es Curso java y el valor es 3. Es decir: se quedó con la clave precio del curso y el valor 3."
 * P4.4 "Con List.sort y el mismo comparador no se pierde nada porque por que solo ordena no estamos hacienod una inserccion."
 *
 */

