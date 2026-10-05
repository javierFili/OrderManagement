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
    public static void main(String args[]) {
        System.out.println("1--------Paso");
        Product laptopA = new Laptop(new BigInteger("1"), "Laptop", new BigDecimal("1000"), new BigDecimal("2"));
        Product laptopB = new Laptop(new BigInteger("1"), "Laptop", new BigDecimal("1000"), new BigDecimal("2"));
        Product laptopC = new Laptop(new BigInteger("2"), "Laptop Pro", new BigDecimal("1500"), new BigDecimal("2"));

        System.out.println("2--------Paso laptopA == laptopB");
        System.out.println(laptopA == laptopB);
        // espero: false ✅
        System.out.println("3--------Paso: laptopA.equals(laptopB)");
        System.out.println(laptopA.equals(laptopB));
        // espero: true
        // real: false
        // por que: por que son objetos diferentes, si bien tiene mismos valores, ya que internamente termina usando this==o debido a que se esta herando de object y object solo hace this==o , por lo tanto siempre es mejor sobre escribir el metodo.

        System.out.println("4--------Paso: laptopA.hashCode() == laptopB.hashCode()");
        System.out.println(laptopA.hashCode() == laptopB.hashCode());
        System.out.println("hash: " + laptopA.hashCode() + " " + laptopB.hashCode());
        // espero false ✅

        System.out.println("5--------Paso: list.contains(laptopB)");
        List<Product> list = new ArrayList<>();
        list.add(laptopA);
        System.out.println(list.contains(laptopB));
        // espero false.✅

        System.out.println("6--------Paso: set.add(laptopA), set.add(laptopB), set.add(laptopC)");
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

        System.out.println("8--------Paso: set.size(), set.contains(laptopD)");
        Product laptopD = new Laptop(new BigInteger("1"), "Laptop", new BigDecimal("1000"), new BigDecimal("2"));
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

        try {
            Product laptopE = new Laptop(new BigInteger("1"), "Laptop",null, new BigDecimal("2"));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        Product laptopE = new Laptop(new BigInteger("1"), "Laptop",new BigDecimal("0"), new BigDecimal("2"));
        // espero: se rompe
        // real: deberia aceptar precio 0? actualmente deje que pase con precio 0.

    }
}
/*
| Línea                          | Ronda 1 (nada) | Ronda 2 (solo equals) | Ronda 3 (equals + hashCode) |
|--------------------------------|----------------|-----------------------|-----------------------------|
| A == B                         |      false     |        false          |     espero:false  ✅         |
| A.equals(B)                    |      false     |        true           |     espero: true  ✅         |
| A.hashCode() == B.hashCode()   |      false     |        false          |     espero: true ✅          |
| list.contains(B)               |      false     |        true           |     espero:true ✅           |
| set.add(A)                     |      true      |        true           |     espero:true ✅           |
| set.add(B)                     |      true      |        true           |     espero:false ✅          |
| set.add(C)                     |      true      |        true           |     espero:true ✅           |
| set.size()                     |      3         |         3             |     espero:2 ✅              |
| set.contains(D)                |      false     |        false          |     espero:false  // real: true, por que el hascode es 1, igual al de A,B entonces si lo contiene        |
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

/**
 * 1: si sobreescribimos el equals, tambien debemos sobre escribir el hashcode, esto por que para buscar un objeto usamos el equals, pero para agregarlo usamso el hashcode, si fueran diferentes entonces tendriamos bugs ocultas, inconsistencias..
 * 2: Bueno en realidad usa solo id, por que como se sabien bien el id, es un identificador unico, adeams si ej viene de la DB entonces el id no identifica si ya existe ese producto, bueno tambien depende del dominio, ademas se debe usar bien los comparadores equals del Biginteger.
 * 3: debe ser final y que despues de ser instanciado no debe cambiar, asegurando asi que en no ese id sea unico o inmutable  durante lo que dure la ejecucion,
 * explicacion del ejemplo: si existiria el setId en un objeto entonces el hashcode estaria cambiando ya que ahora lo estamos modificando mediante la sobreescritura y por lo tanto el hash set que perderia la informacion actualizado desencadenando a que se pierda ese objeto.
 * 4: por que en objetos hashSet para hacer un equals de un objeto se compara el hash, entonces si hacemos equals por id entonces  es por que su hash code tambien es igual.
 *
 */