package practical.sets;

import Models.Product;
import Models.products.Laptop;

import javax.annotation.processing.SupportedSourceVersion;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EqualsHashCodeLab {
    public  static void main(String args[]){
        System.out.println("1--------Paso");
        Product laptopA = new Laptop(new BigInteger("1"),"Laptop",new BigDecimal("1000"),new BigDecimal("2"));
        Product laptopB = new Laptop(new BigInteger("1"),"Laptop",new BigDecimal("1000"), new BigDecimal("2"));
        Product laptopC= new Laptop(new BigInteger("2"),"Laptop Pro",new BigDecimal("1500"), new BigDecimal("2"));

        System.out.println("2--------Paso");
        System.out.println(laptopA==laptopB);
        // espero: false ✅
        System.out.println("3--------Paso");
        System.out.println(laptopA.equals(laptopB));
        // espero: true
        // real: false
        // por que: por que son objetos diferentes, si bien tiene mismos valores, ya que internamente termina usando this==o debido a que se esta herando de object y object solo hace this==o , por lo tanto siempre es mejor sobre escribir el metodo.

        System.out.println("4--------Paso");
        System.out.println(laptopA.hashCode() == laptopB.hashCode());
        // espero false ✅

        System.out.println("5--------Paso");
        List<Product> list = new ArrayList<>();
        list.add(laptopA);
        System.out.println(list.contains(laptopB));
        // espero false.✅

        System.out.println("6--------Paso");
        Set<Product> set = new HashSet<>();
        System.out.println(set.add(laptopA));
        //espero: true ✅
        System.out.println(set.add(laptopB));
        // espero: true ✅
        System.out.println(set.add(laptopC));
        // espero: true ✅

        System.out.println("7--------Paso");
        System.out.println(set);
        // espero: obvioante en orden diferente: [Product{id="1", name= laptop, price= 1000},Product{id="1", name= laptop, price= 1000},Product{id="2", name= laptop Pro, price= 1500}]✅

        System.out.println("8--------Paso");
        Product laptopD = new Laptop(new BigInteger("1"),"Laptop",new BigDecimal("1000"),new BigDecimal("2"));
        System.out.println(set.size());
        // espero: 3 ✅
        System.out.println(set.contains(laptopD));
        // espero : true
        // real: false,
        // por que: parece ser que tambien usa intenamente un this==o que esa heradoado desde object,  asi que en objetos personalizados por asi decirlo deberiamos de sobre escribirlo.

    }
}
/*
| Línea                          | Ronda 1 (nada) | Ronda 2 (solo equals) | Ronda 3 (equals + hashCode) |
|--------------------------------|----------------|-----------------------|-----------------------------|
| A == B                         |      false     |                       |                             |
| A.equals(B)                    |      false     |                       |                             |
| A.hashCode() == B.hashCode()   |      false     |                       |                             |
| list.contains(B)               |      false     |                       |                             |
| set.add(A)                     |      true      |                       |                             |
| set.add(B)                     |      true      |                       |                             |
| set.add(C)                     |      true      |                       |                             |
| set.size()                     |      3         |                       |                             |
| set.contains(D)                |      false     |                       |                             |
 */

/**
 * 1: por que en realiadad el laptopA==laptopB hace una comparacion de la ubicacion de ese objeto en nuestra memoria RAM... creo!!
 * 2: por que en realidad equals la final hace  un this==o
 * 3: En realidad no es el mismo producto, son instancias diferentes, el equals, contains aun no tiene una regla, metodo, etc de saber cuando una objeto tipo producto es igual que otro objeto tipo producto, por eso tenemos que re-escribir esos metodos para tener ese parametro de identificacion de igualdad entre productos.
 */