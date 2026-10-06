package practical.sets;

import Models.Product;
import Models.products.Laptop;
import Models.products.PhysicalProduct;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HashSpyLab {
    public static void main (String args[]){
        Product m1 = new Laptop(new BigInteger("7"),"Mouse",new BigDecimal("20"),new BigDecimal("1"));
        Product m2 = new Laptop(new BigInteger("7"),"Mouse",new BigDecimal("20"),new BigDecimal("1"));
        System.out.println(m1.hashCode());
        System.out.println(m2.hashCode());
        // espero: su hashcode diferente
        List<Product> products = new ArrayList<>();
        products.add(m1);
        System.out.println(products.contains(m2));
        // espero: si aparece el  >> equals llamado con Product{id=7, name='Mouse' , price= 20}

        HashSet<Product> hashProducts = new HashSet<>();
        hashProducts.add(m1);
        System.out.println(hashProducts.add(m2));
        // espero: true
        /**
         * las mini tareas 1
         */
        int x = 500;
        int y = 500;
        System.out.println(x==y);
        // espero: true ✅
        BigInteger bx = new BigInteger("500");
        BigInteger by = new BigInteger("500");
        System.out.println(bx==by);
        // espero: false ✅

        /**
         * mini tarea 3
         */
        Product p1 = new Laptop(new BigInteger("31"), "Cable",new BigDecimal("5"), new BigDecimal("1"));
        Product p2 = new Laptop(new BigInteger("4294967296"), "Cable XL",new BigDecimal("0"), new BigDecimal("2"));
        System.out.println(p1.hashCode());
        // espero: 31
        System.out.println(p2.hashCode());
        // espero: 4294967296 // real: 1? por que? realmente no se por que!!!
        System.out.println(p1.equals(p2));
        //eespero: false

        HashSet<Product> listHash = new HashSet<>();
        System.out.println(listHash.add(p1));
        // espero: true
        System.out.println(listHash.size());
        // espero: 1

        /**
         * mini tarea 4
         */
        Product p3 = new Laptop(null, "Cable XL",new BigDecimal("0"), new BigDecimal("2"));
        listHash.add(p3);
        System.out.println(p3.hashCode());
        // espero: exception
        // real: 1, por que el null sigue siendo un tipo valido aunque sea null.

        System.out.println(new BigDecimal("2.0").equals(new BigDecimal("2.00")));
        // espero: true
        // real: false,  java hace diferencia entre 2.0 y 2.00...
    }
}

/**
 * P3.2: bueno la ser decimal, ademas que un valor que puede ser modificado ahce que no sea seguro poder tomar como referencia de comparacion.
 * P3.1: si a.equals(b) es true entonces a.hashCode() ==b.hashCode() es true, por lo tanto el bug que vimos fue por que el cometodo hashCode() no fue sobreescrito en el objeto product
 * 3 : Al poner id final no se rompe por que como que bloquea el id para que no sea modificable, solo se puede ver no editar.
 */


