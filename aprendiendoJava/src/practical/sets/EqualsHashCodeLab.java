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

        System.out.println("Pruebas----");
        System.out.println(laptopA.equals(laptopB));
        // espero: true✅
        System.out.println(laptopA.equals(null));
        // espero: false✅
        System.out.println(laptopA.equals(laptopC));
        // espero: false✅
        System.out.println(laptopA.equals("Laptop"));
        // espero: false✅
        System.out.println(laptopA.equals(laptopA));
        // espero: true✅
    }
}
/*
| Línea                          | Ronda 1 (nada) | Ronda 2 (solo equals) | Ronda 3 (equals + hashCode) |
|--------------------------------|----------------|-----------------------|-----------------------------|
| A == B                         |      false     |        false          |                             |
| A.equals(B)                    |      false     |        true           |                             |
| A.hashCode() == B.hashCode()   |      false     |        false          |                             |
| list.contains(B)               |      false     |        true           |                             |
| set.add(A)                     |      true      |        true           |                             |
| set.add(B)                     |      true      |        true           |                             |
| set.add(C)                     |      true      |        true           |                             |
| set.size()                     |      3         |         3             |                             |
| set.contains(D)                |      false     |        false           |                             |
 */

/**
 * 1: por que en realiadad el laptopA==laptopB hace una comparacion de la ubicacion de ese objeto en nuestra memoria RAM... creo!!
 * 2: por que en realidad equals la final hace  un this==o
 * 3: En realidad no es el mismo producto, son instancias diferentes, el equals, contains aun no tiene una regla, metodo, etc de saber cuando una objeto tipo producto es igual que otro objeto tipo producto, por eso tenemos que re-escribir esos metodos para tener ese parametro de identificacion de igualdad entre productos.
 */

/**
 * 1: solo cambio la fila Ronda2 solo el equals y contains, esto se debe a hizimos una sobreescritura del metodo equals, ahora esta comparando el ID para saber si es igual o no el objeto a comparar.
 * 2: el list.contains(B) nos da true debido  a que esta usando el equals para comparar, pero el set lo esta aceptando por que no usa el equals, usa el hashcode y pues no esta siendo encontrado ya que no esta sobre escribiendo se metodo lo por lo tanto esta usando su metodo por defecto...
 * ej: A tiene el hascode: 123433 y B tiene el hascodee: 123532 que son completamente diferentes entonces nunca se encontran o evaluan.
 * 3: En una respuesta rapida: simplemente por que se puede comparar con cualquier objeto, pero internamente ya se puede decir si validar o no, ej: yo estoy validando con instanceof para que se asegure que efectivamente es un Product
 */