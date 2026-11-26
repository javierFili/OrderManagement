package practical.sorting;

import Models.Catalog;
import Models.Product;
import Models.products.DigitalProduct;
import Models.products.Laptop;
import Models.products.PhysicalProduct;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class CatalogMain {
    //✅
    public static void main(String[] args) throws IllegalAccessException {
        Catalog catalog = new Catalog();
        Product laptop = new Laptop(new BigInteger("40"), "ThinkPad", new BigDecimal("1200.00"), new BigDecimal("2.1"));
        Product silla = new PhysicalProduct(new BigInteger("12"), "Silla", new BigDecimal("89.90"), new BigDecimal("7.5"));
        Product curso = new DigitalProduct(new BigInteger("7"), "Curso java", new BigDecimal("49.00"), new BigDecimal("2.5"));
        Product mac = new Laptop(new BigInteger("25"), "MacBook", new BigDecimal("1999.99"), new BigDecimal("1.4"));
        Product mouse = new PhysicalProduct(new BigInteger("31"), "Mouse", new BigDecimal("49.00"), new BigDecimal("0.2"));
        Product audifonos = new PhysicalProduct(new BigInteger("18"), "Audifonos", new BigDecimal("89.90"), new BigDecimal("0.3"));
        Product sillaGamer = new PhysicalProduct(new BigInteger("12"), "sillaGamer", new BigDecimal("289.90"), new BigDecimal("73.5"));

        print(catalog.addProduct(laptop));
        print(catalog.addProduct(silla));
        print(catalog.addProduct(curso));
        print(catalog.addProduct(mac));
        print(catalog.addProduct(mouse));
        print(catalog.addProduct(audifonos));
        print(catalog.addProduct(sillaGamer));
        // print(catalog.addProduct(null));
        /**
         * addProduct(…)	¿es null?	¿ya hay alguien con ese id?	espero	        real
         * thinkpad             no          no                      true            true
         * silla                no          no                      true            true
         * curso                no          no                      true            true
         * mac                  no          no                      true            true
         * mouse                no          no                      true            true
         * audifonos            no          no                      true            true
         * sillaGamer           no          si                      true            true
         * null                 si          no                      no puede ser null       no puede ser null
         */
        print("----Paso 3-----");
        imprimirNombres(catalog.getProducts());
        // espero: ThinkPad Silla Curso java MacBook Mouse Audifonos

        print("---Paso 4-----");
        //imprimirNombres(catalog.getProducts());
        print("----price---");
        imprimirNombres(catalog.getSortedByPrice());
        // espero:Curso java price:49.00 type:class Models.products.DigitalProduct -- Mouse price:49.00 type:class Models.products.PhysicalProduct -- Silla price:89.90 type:class Models.products.PhysicalProduct -- Audifonos price:89.90 type:class Models.products.PhysicalProduct -- ThinkPad price:1200.00 type:class Models.products.Laptop -- MacBook price:1999.99 type:class Models.products.Laptop --

        //imprimirNombres(catalog.getProducts());
        print("---name---");
        imprimirNombres(catalog.getSortedByName());
        //espero: Audifonos price:89.90 type:class Models.products.PhysicalProduct -- Curso java price:49.00 type:class Models.products.DigitalProduct -- MacBook price:1999.99 type:class Models.products.Laptop -- Mouse price:49.00 type:class Models.products.PhysicalProduct -- Silla price:89.90 type:class Models.products.PhysicalProduct -- ThinkPad price:1200.00 type:class Models.products.Laptop --

        //imprimirNombres(catalog.getProducts());
        print("----type---");
        imprimirNombres(catalog.getSortedByType());
        // espero:ThinkPad price:1200.00 type:class Models.products.Laptop -- Silla price:89.90 type:class Models.products.PhysicalProduct -- MacBook price:1999.99 type:class Models.products.Laptop -- Mouse price:49.00 type:class Models.products.PhysicalProduct -- Audifonos price:89.90 type:class Models.products.PhysicalProduct -- Curso java price:49.00 type:class Models.products.DigitalProduct --

        print("-----Paso 5---");
        print("---orden natural");
        imprimirNombres(catalog.getProducts());
        // espero: ThinkPad price:1200.00 type:class Models.products.Laptop -- Silla price:89.90 type:class Models.products.PhysicalProduct -- Curso java price:49.00 type:class Models.products.DigitalProduct -- MacBook price:1999.99 type:class Models.products.Laptop -- Mouse price:49.00 type:class Models.products.PhysicalProduct -- Audifonos price:89.90 type:class Models.products.PhysicalProduct --

        print("---Paso 6----");
        try {
            catalog.getSortedByPrice().add(mouse);
        } catch (UnsupportedOperationException e) {
            print(e + "1");
            // espero: cae aqui
        }

        try {
            catalog.getProducts().sort(null);
        } catch (UnsupportedOperationException e) {
            print(e + "2");
            // espero: cae aqui
        }

        try {
            catalog.getProducts().clear();
        } catch (UnsupportedOperationException e) {
            print(e + "3");
            // espero: cae aqui
        }
        print("----Paso 7----");
        print(catalog.getCheapest().getName());
        // espero: Curso java
        print("----Paso 8----");
        try {
            print(new Catalog().getCheapest());
        } catch (RuntimeException e) {
            print(e);
            // espero: exception: el catalogo esta vacio
        }

    }

    static void imprimirNombres(List<Product> l) {
        for (Product item : l) {
            System.out.print(item.getName() + " price:" + item.getPrice() + " type:" + item.getClass() + " -- ");
        }
        System.out.println("");
    }

    private static void print(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }
}

/**
 * P6.1 "getSortedByPrice copia antes de ordenar porque, si ordenara products directamente, podemos modificar los productos del catalogo. Lo comprobé comparando el paso getproduct con el paso sortedByPrice."
 * P6.2 "Envuelvo la copia ordenada con Collections.unmodifiableList(...) para que no se puede modificar. En el paso 6 eso se vio porque no modifico el catalogo real.."
 * P6.3 "Si el catálogo está vacío, getCheapest() nos devuelve una exception, porque elegí la opción de devolver una exceptino. Esa opción es mejor que devolver null porque si no podriamos tratar de operar con se null." (si elegiste null, explica por qué igual lo prefieres)
 * P6.4 "addProduct detecta que sillaGamer ya está usando el método addproduct de la lista, que por dentro llama al método contains de Product, que compara los ids."
 * P6.5 "Los tres getSortedBy... no repiten código porque tenemos un metodo que necesita como parametro que tipo de orden queremos.."
 */