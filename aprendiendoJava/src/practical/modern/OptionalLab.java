package practical.modern;

import Models.Catalog;
import Models.Product;
import Models.products.DigitalProduct;
import Models.products.Laptop;
import Models.products.PhysicalProduct;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.Optional;

public class OptionalLab {

    public static void main(String[] args) {
        Catalog catalog = new Catalog();

        Product laptop = new Laptop(new BigInteger("40"), "ThinkPad", new BigDecimal("1200.00"), new BigDecimal("2.1"));
        Product silla = new PhysicalProduct(new BigInteger("12"), "Silla", new BigDecimal("89.90"), new BigDecimal("7.5"));
        Product curso = new DigitalProduct(new BigInteger("7"), "Curso java", new BigDecimal("49.00"), new BigDecimal("2.5"));
        Product mac = new Laptop(new BigInteger("25"), "MacBook", new BigDecimal("1999.99"), new BigDecimal("1.4"));
        Product mouse = new PhysicalProduct(new BigInteger("31"), "Mouse", new BigDecimal("49.00"), new BigDecimal("0.2"));
        Product audifonos = new PhysicalProduct(new BigInteger("18"), "Audifonos", new BigDecimal("89.90"), new BigDecimal("0.3"));
        catalog.addProduct(laptop);
        catalog.addProduct(silla);
        catalog.addProduct(curso);
        catalog.addProduct(mac);
        catalog.addProduct(mouse);
        catalog.addProduct(audifonos);

        print("----Paso 3---");
        print(catalog.findByName("mouse").isPresent());
        print(catalog.findByName("tablet").isPresent());
        print(catalog.findByName("tablet").isEmpty());
        print(catalog.findByName("mouse").get().getPrice());
        print(catalog.findByName("AUDÍFONOS").isPresent());
        print(catalog.findByName("audifonos").isPresent());
        print(catalog.findById(BigInteger.valueOf(25)).get().getName());
        print(catalog.findById(BigInteger.valueOf(99)));
        print(catalog.getCheapest().get().getName());
        print(new Catalog().getCheapest());

        /**
         * #	Expresión	                                                        espero	            real
         * a	catalog.findByName("mouse").isPresent()                             true                true
         * b	catalog.findByName("tablet").isPresent()                            false               false
         * c	catalog.findByName("tablet").isEmpty()                              true                true
         * d	catalog.findByName("mouse").get().getPrice()                        error               49.00
         * e	catalog.findByName("AUDÍFONOS").isPresent()                         false               false
         * f	catalog.findByName("audifonos").isPresent() ← sin tilde             true                true
         * g	catalog.findById(BigInteger.valueOf(25)).get().getName()            error               Macbook         pense que saldria error debido a que estamos haciendo get sin preguntar si existe algo
         * h	catalog.findById(BigInteger.valueOf(99))                            empty               Optional.empty
         * i	catalog.getCheapest().get().getName()                               error               Curso Java      pense que saldria error debido a que estamos haciendo get sin preguntar si existe algo
         * j	new Catalog().getCheapest()                                         empty               Optional.empty
         */

        print("-----Paso 4----");
        mostrarPrecio(catalog, "Silla");
        // espero: Silla: 89.90
        mostrarPrecio(catalog, "silla");
        // espero: Silla: 89.90
        mostrarPrecio(catalog, "Teclado");
        // espero: No existe: Teclado.
        print("----Paso 5----");
        print(catalog.findByName("tablet").orElse(porDefecto()).getName());
        // espero: va por defecto
        print(catalog.findByName("mouse").orElse(porDefecto()).getName());
        // espero: ingresa normal pero tambien entra a por defecto.

        print("----Paso 6----");

        try {
            catalog.findByName("tablet").get();
        } catch (NoSuchElementException e) {
            print(e);
            // espero: No value present
        }
        try {
            Optional.of(null);
        } catch (NullPointerException e) {
            print(e);
            // espero: NullPointerException
        }

        print("----Paso 7-------");
        /**
         * findBySku(String sku) (el SKU es único)
         * R:Optional<Product>
         * findByPriceLowerThan(BigDecimal max) (pueden ser varios)
         * R:List<>
         * getProducts()
         * R: List<>
         * countProducts()
         * R: int, BigInteger
         */


    }

    static Product porDefecto() {
        System.out.println("  (porDefecto se ejecutó)");
        return new Product(BigInteger.ZERO, "Sin producto", BigDecimal.ZERO);
    }

    static void mostrarPrecio(Catalog catalog, String name) {
        Optional<Product> product = catalog.findByName(name);
        if (product.isEmpty()) {
            print("No existe:" + name);
        } else {
            print("Existe" + product.get().getName() + " Precio:" + product.get().getPrice());
        }
    }

    private static void print(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }

}
/**
 *
 * P5.1 "findByName devuelve Optional<Product> en vez de Product porque así gestionamos el Optional. Quien lo llama tiene que gestionar que es un Optional."
 * P5.2 "findByName("audifonos") dio el producto audifinos porque equalsIgnoreCase ignora mayuscylas pero no tildes."
 * P5.3 "En el paso 5, porDefecto() se ejecutó 2 veces, porque orElse siempre ejecuta aunque no muestra."
 * P5.4 "getCheapest() con Optional es mejor que la versión de la lección 04 porque no devuelve null, si no que avisa si no tiene productos."
 * P5.5 "findByPriceLowerThan no devuelve Optional<List<Product>> porque puede dar una lista vacia directamente."
 */