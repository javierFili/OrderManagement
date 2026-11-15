package practical.sorting;

import Models.Product;
import Models.products.DigitalProduct;
import Models.products.Laptop;
import Models.products.PhysicalProduct;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class ComparableLab {

    public static void main(String args[]) {
        Set<Product> treeProduct = new TreeSet<>();
        Product laptop1 = new Laptop(new BigInteger("2"), "Dell 32d", new BigDecimal("34.2"), new BigDecimal("32"));
        try {
            treeProduct.add(laptop1);
        } catch (ClassCastException e) {
            print("no se peude agregar aun" + e);
            // espero: no se peude agregar aun classcastException
        }
        print(treeProduct);
        //espero: product{2,Dell 32d, 34.2, 32} ✅
        treeProduct.clear();
        Product laptop = new Laptop(new BigInteger("40"), "ThinkPad", new BigDecimal("1200.00"), new BigDecimal("2.1"));
        Product silla = new PhysicalProduct(new BigInteger("12"),"Silla",new BigDecimal("89.90"),new BigDecimal("7.5"));
        Product curso = new DigitalProduct(new BigInteger("7"),"Curso java",new BigDecimal("49.00"),new BigDecimal("2.5"));
        Product mac = new Laptop(new BigInteger("25"),"MacBook",new BigDecimal("1999.99"),new BigDecimal("1.4"));
        Product sillaGamer = new PhysicalProduct(new BigInteger("12"),"Silla gamer",new BigDecimal("149.00"),new BigDecimal("9.00"));
        List<Product> catalogo = new ArrayList<>();
        catalogo.add(laptop);catalogo.add(silla);catalogo.add(curso);catalogo.add(mac);catalogo.add(sillaGamer);
        Collections.sort(catalogo);
        for (Product item:catalogo){
            print(item);
        }
        // espero: 7 curso java,12 silla,12 silla gamer,25 macbook,40 thinkpad ✅ primer va silla ya que por estabilidad.
        print(laptop.compareTo(silla));
        // espero: + ✅
        print(curso.compareTo(mac));
        // espero: - ✅
        print(silla.compareTo(sillaGamer));
        // espero: 0 ✅
        print(silla.equals(sillaGamer));
        // espero: true ✅
        treeProduct.clear();
        print(treeProduct.add(laptop));
        // espero: true ✅
        print(treeProduct.add(silla));
        // espero: true ✅
        print(treeProduct.add(curso));
        // espero: true ✅
        print(treeProduct.add(mac));
        // espero: true ✅
        print(treeProduct.add(sillaGamer));
        // espero: false ✅

        List<Laptop> laptops = new ArrayList<>();
        laptops.add((Laptop) laptop);
        laptops.add((Laptop) mac);
        Collections.sort(laptops);
        print(laptops);
        // espero: 25,40: compila por que si no tiene compareTo propio el compilado busca en su padre y asi suceciva y sucecivaamente.



    }

    private static void print(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }
}

/***
 * Expresión	espero          (signo)	real
 * thinkpad.compareTo(silla)	    +	+1
 * curso.compareTo(mac)		        -   -1
 * silla.compareTo(sillaGamer)		0
 * silla.equals(sillaGamer)	(boolean) true true
 */

/**
 *
 * P2.1 "Elegí el id como orden natural porque en nuestro negocio deberia ser unico desde la bd. Si hubiera elegido el precio, en el paso 6 el TreeSet se rompia."
 * P2.2 "En el paso 6, el add de sillaGamer devolvió false porque compareTo dio 0. Quedó guardada la silla que fue agregada de primero."
 * P2.3 "Si un producto tuviera id null, compareTo lanzaría excepcion  NullPointerException"
 */