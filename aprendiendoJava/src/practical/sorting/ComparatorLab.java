package practical.sorting;

import Models.Product;
import Models.comparators.ProductNameComparator;
import Models.comparators.ProductPriceComparator;
import Models.products.DigitalProduct;
import Models.products.Laptop;
import Models.products.PhysicalProduct;
import org.w3c.dom.ls.LSInput;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComparatorLab {
    public static void main(String args[]) {
        Product laptop = new Laptop(new BigInteger("40"), "ThinkPad", new BigDecimal("1200.00"), new BigDecimal("2.1"));
        Product silla = new PhysicalProduct(new BigInteger("12"), "Silla", new BigDecimal("89.90"), new BigDecimal("7.5"));
        Product curso = new DigitalProduct(new BigInteger("7"), "Curso java", new BigDecimal("49.00"), new BigDecimal("2.5"));
        Product mac = new Laptop(new BigInteger("25"), "MacBook", new BigDecimal("1999.99"), new BigDecimal("1.4"));
        Product mouse = new PhysicalProduct(new BigInteger("31"), "Mouse", new BigDecimal("49.00"), new BigDecimal("0.2"));
        Product audifonos = new PhysicalProduct(new BigInteger("18"), "Audifonos", new BigDecimal("89.90"), new BigDecimal("0.3"));
        List<Product> base = new ArrayList<>();
        base.add(laptop);
        base.add(silla);
        base.add(curso);
        base.add(mac);
        base.add(mouse);
        base.add(audifonos);
        ProductPriceComparator priceComparator = new ProductPriceComparator();
        List<Product> l = new ArrayList<>(base);
        l.sort(priceComparator);
        imprimir(l);
        // espero: curso java 49.00 | Mouse 49.00 | silla 89.90 | audifinos 89.90 | Thinkpad 1200.00 | MacBook 1999.99 | ✅
        // explanation: cuando son iguales se queda el primero y el que igual va despues.
        List<Product> lreversed = new ArrayList<>(base);
        lreversed.sort(priceComparator.reversed());
        imprimir(lreversed);
        // espero: Macbook 1999.99 | ThinkPad 1200.00 | silla 89.90 | audifonos 89.90 | curso java 49.00 | Mouse 49.00 | ✅
        // explanation: el empate entre curso java y mouse va el curso java por que ese ingreso primero.
        ProductNameComparator nameComparator = new ProductNameComparator();
        List<Product> l1 = new ArrayList<>(base);
        l1.sort(nameComparator);
        imprimir(l1);
        // espero: audifonos | curso java | Macbook | Mouse | silla | Thinkpad ✅
        // explanation: audifonos va primero luego esta curso java por el orden alfabetico.
        print("---- paso 5.8---");
        List<Product> l1reversedthen = new ArrayList<>(base);
        l1reversedthen.sort(priceComparator.thenComparing(nameComparator));
        imprimir(l1reversedthen);
        // espero: Macbook 1999.99 | ThinkPad 1200.00 | audifonos 89.90 | silla 89.90 |  curso java 49.00 | Mouse 49.00 | ✅
        // explanation: si cambia el orden ya que ahora tambien comparamos por nombre, entonces cuando hay choque en el precio se toma el nombre y en este caso el primero va audifonos luego silla.
        print("------5.9-----");
        List<Product> l2 = new ArrayList<>(base);
        l2.sort(priceComparator.reversed().thenComparing(nameComparator));
        imprimir(l2);
        // espero: Macbook 1999.99 | ThinkPad 1200.00 | audifonos 89.90 | silla 89.90 | curso java 49.00 | Mouse 49.00 |✅
        // explanation: si, esta vez audifonos va primero por que si bien el reversed es por price el namecomparator sigue siendo orden normal.

        print("-----5.10----");
        List<Product> l3 = new ArrayList<>(base);
        l3.sort(nameComparator.reversed());
        imprimir(l3);
        // espero: thinkpad | silla | mouse | macbook | curso java | audifonos ✅
        print("---6---");
        List<Product> l5 = new ArrayList<>(base);
        Collections.reverse(l5);
        l5.sort(priceComparator);
        imprimir(l5);
        // espero: Mouse 49.00| Curso java 49.00| Audifonos 89.90| Silla 89.90| ThinkPad 1200.00| MacBook 1999.99| ✅
        print("---7---");
        Product max = Collections.max(base, new ProductPriceComparator());
        print(max);
        // espero: Macbook
        Product min = Collections.min(base, new ProductPriceComparator());
        print(min);
        // espero: Curso java
        Product min2 = Collections.min(base);
        print(min2);
        // espero: 7
        // explanation: usara el que nosotros pusimos en la clase product, osea el id.


    }

    private static void print(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }

    private static void imprimir(List<Product> lista) {
        StringBuilder res = new StringBuilder();
        for (Product item : lista) {
            res.append(item.getName()).append(" ").append(item.getPrice()).append("| ");
        }
        print(res.toString());
    }
}

/**
 * P3.1 "reversed() no modifica el comparador: crea uno nuevo. Por eso escribo new X().reversed() y no necesito una clase ProductPriceDescComparator."
 * P3.2 "thenComparing solo se usa cuando existe alguna igual o empate. En el paso 8 cambió el orden de audifonos y silla porque por que hubo un empate."
 * P3.3 "Entre el paso 5 y el paso del punto 6 cambió el orden de los empates porque sort es revesed: en un empate conserva el orden de insercion."
 */
