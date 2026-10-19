package practical.maps;

import Models.Customer;
import Models.Order;
import Models.Product;
import Models.products.Laptop;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerSpendingLab {

    public static void main(String args[]) {
        Customer ana1 = new Customer(new BigInteger("100"), "Ana", "ana@mail.com");
        Customer ana2 = new Customer(new BigInteger("100"), "Ana", "ana@mail.com");
        Customer beto = new Customer(new BigInteger("200"), "Beto", "beto@gmail.com");
        Order o1 = new Order(new BigInteger("1"), ana1);
        Order o2 = new Order(new BigInteger("2"), beto);
        Order o3 = new Order(new BigInteger("3"), ana2);

        Product laptop = new Laptop(new BigInteger("1"), "Laptop", new BigDecimal("23.2"), new BigDecimal("32.4"));
        o1.addItem(laptop, 1);

        Product mouse = new Laptop(new BigInteger("2"), "Mouse", new BigDecimal("43.2"), new BigDecimal("35.4"));
        o2.addItem(mouse, 1);

        Product webcam = new Laptop(new BigInteger("3"), "Webcam", new BigDecimal("32.00"), new BigDecimal("5.00"));
        o3.addItem(webcam, 1);

        List<Order> pedidos = new ArrayList<>();
        pedidos.add(o1);
        pedidos.add(o2);
        pedidos.add(o3);

        Map<Customer, BigDecimal> gastando = new HashMap<>();
        for (Order item : pedidos) {
            gastando.merge(item.getCustomer(), item.getTotalPrice(), BigDecimal::add);
        }
        printl(gastando.size());
        //espero: 3 ✅
        //2do espero: 2✅
        printl(gastando.get(ana1));
        //espero:23.2 ✅
        // 2do espero: 23.2
        // real: olvide sumar pero ya esta sale: 55.20
        /**
         * que equals usa?   ==> usa la que es por defecto del object, en pocas palabras algo asi this==o
         * que hashcode usa? ==> usa la que es por defecto del object, no se como sacar ese numero, pero usa el por defecto.
         */
        printl("----Ronda2----");
        // no deberia hacer ningun setId ya que ahora es final, una vez instanciado no se puede cambiar.

        printl("-----Ronda3----");
        Product prod31 = new Laptop(new BigInteger("31"), "Laptop", new BigDecimal("32.52"), new BigDecimal("32.4"));
        Product prod429 = new Laptop(new BigInteger("4294967296"), "mouse", new BigDecimal("332.52"), new BigDecimal("342.4"));
        printl(prod31.hashCode());
        // espero:31
        // real: 62, por que yo no se como calcular el hashcode a mano
        printl(prod429.hashCode());
        //espero:0
        // real: 62, por que yo no se como calcular el hashcode a mano
        printl(prod31.equals(prod429));
        //espero: false ✅

        Map<Product,String> etiquetas = new HashMap<>();
        etiquetas.put(prod31,"A");
        etiquetas.put(prod429,"B");
        printl(etiquetas.size());
        // espero:2 ✅
        printl(etiquetas.get(prod31));
        // espero:A ✅
        printl(etiquetas.get(prod429));
        // espero:B ✅



    }

    public static void printl(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }
}

/**
 * Ronda 2 — arreglar Customer
 * <p>
 * En Customer, haz el campo id final (¿hay algún setId que se rompa? ¿por qué no?).
 * Escribe equals y hashCode comparando solo el id, con la misma receta que usaste en Product (lección 02, §1.2): this == o,
 * instanceof, comparar id con equals y hashCode con Objects.hash(id).
 * <p>
 * No la copies de Product: escríbela mirando la receta paso a paso. Es la tercera vez que la escribes; la idea es que te salga sola.
 * <p>
 * Ejecuta de nuevo, sin cambiar el lab. Anota size() y get(ana1) en una tabla:
 * | Línea            | Ronda 1 (sin equals) | Ronda 2 (con equals) |
 * |------------------|----------------------|----------------------|
 * | gastado.size()   |        3             |        2              |
 * | gastado.get(ana1)|       23.2           |       55.20             |
 *
 */

/**
 * Ronda 3 — la colisión (deuda de la lección 02)
 *
 * Crea dos productos con ids "31" y "4294967296" (cualquier nombre y precio).
 * Imprime el hashCode() de cada uno y p31.equals(p429). // espero: en cada uno.
 * (Ya sabes la respuesta del hashCode: está en la revisión v3 de la Tarea 3 de la lección 02.)
 * Crea Map<Product, String> etiquetas = new HashMap<>();, haz put(p31, "A") y put(p429, "B").
 * Imprime size(), get(p31) y get(p429). // espero: en cada uno.
 */

/**
 *
 * P4.1 "En la Ronda 1, size dio 3 porque ana1 y ana2 tenían hashCode distinto (el de Object), así que fueron a cajones distintos y equals era this == o."
 * P4.2 "En la Ronda 2, merge encontró la clave de Ana con ana2 aunque yo la metí con ana1, porque se sobreescribio el equals y hashcode."
 * P4.3 (la colisión, P3.4 de la lección 02) "p31 y p429 tienen el mismo hashCode (62), o sea, caen en el mismo 62. Pero no son la misma clave porque equals da false. Por eso el mapa tiene 2 entradas. Esto se llama doble verificacion."
 * P4.4 "Customer tiene setName y setEmail. No los usé en equals/hashCode porque, si un campo de la clave cambia después del put, entonces perdemos el cajon donde se guardo el objeto (§7.4)."
 *
 */