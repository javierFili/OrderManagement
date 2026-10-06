package practical.sets;

import Models.Product;
import Models.products.Laptop;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class SetOrderLab {
    public static void main(String args[]) {
        List<String> names = List.of("Carla", "beto", "Andrés", "Carla", "Diego", "beto", "Ana");
        Set<String> hashNames = new HashSet<>(names);
        Set<String> treeNames = new TreeSet<>(names);
        Set<String> linkedNames = new LinkedHashSet<>(names);

        System.out.println(hashNames);
        // espero: orden impredecible: [Carla,beto,Andres, Diego,Ana] ✅
        System.out.println(hashNames.size());
        // espero:5 ✅

        System.out.println(treeNames);
        // espero: [Ana, Andres,beto,beto,Carla,Carla,Diego]
        // real: [Ana, Andrés, Carla, Diego, beto], esto es por que treeNames no permite duplicados, yo lo habia olvidado, ademas que tambien me equivoque en el orden las mayusculas son antes..
        System.out.println(treeNames.size());
        //espero: 7
        //real: 5 , esto es por que treeNames no permite duplicados, yo lo habia olvidado.
        System.out.println(linkedNames);
        //espero: [Carla,beto,Andres,Carla,Diego,beto,Ana]
        //real:[Carla, beto, Andrés, Diego, Ana], por que el el linkedHash no permite duplicados, yo lo habia olvidado..
        System.out.println(linkedNames.size());
        // espero: 7
        // real: 5, por que no deja duplicados.

        List<Integer> codes = List.of(40, 10, 40, 30, 10, 20);
        Set<Integer> deDuplicates = new LinkedHashSet<>(codes);
        System.out.println(deDuplicates);
        // espero: [40,10,30,20]✅

        try {
            Set<Product> tree = new TreeSet<>();
            Product laptop = new Laptop(new BigInteger("5"),"Monitor",new BigDecimal("300"),new BigDecimal("4"));
            tree.add(laptop);
        } catch (ClassCastException e) {
            System.out.println(e.getClass().getSimpleName());
        }
        // deberia crear con normalidad, tambien agregar el producto, aunque el creo que tambien se deberia sobreesecribir los metodos que usar el treeSet para hace el add
        // real: dio classCastException: creo que obligatoriamente se debe sobre escribir esos metodos..

    }
}

/***
 * 1: por pro que beto esta en minuscula y en orden las mayusculas tiene prioridad.
 * 2: creo que necesita un comparador para saber en que orden deben de ir.. segun la teoria usan comparable y comparator.
 * 3: pues solo un hashSet por que es el mas basico ademas que no cuesta mucho agregar elementos como en otros.
 *
 */