package practical.sorting;

import Models.Customer;
import com.sun.source.tree.Tree;

import javax.swing.text.html.CSS;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class SortPlayground {
    public static void main(String args[]) {
        List<String> colores = new ArrayList<>(List.of("verde", "Azul", "rojo", "Índigo", "amarillo"));
        Collections.sort(colores);
        print("-----");
        print(colores);
        // espero:  [Azul, amarillo, rojo, verde, Índigo]✅

        colores.sort(String.CASE_INSENSITIVE_ORDER);
        print("-----");
        print(colores);
        // espero: [amarillo, Azul, rojo, verde, Índigo] ✅


        // List<String> r = colores.sort(null);
        //Required type:List<java.lang.String>Provided:void

        List<String> fijos = List.of("c", "a", "b");
        try {
            fijos.sort(null);
        } catch (UnsupportedOperationException unsupportedOperationException) {
            print("no se puede");
            //espero: no se puede✅
        }
        List<String> fijos1 = List.copyOf(fijos);
        // fijos1.sort(null);
        // espero: [amarillo,rojo,verde,Azul,Índigo]
        // real:  no se puede.. ✅

        print("-----seccion4----");
        print("mesa".compareTo("silla"));
        // espero: positibo
        // real: negativo
        // porque: mesa es menor que siila
        print("Silla".compareTo("silla"));
        // espero: positibvo
        // real: negativo
        // porque: Silla es menor que silla
        print(Integer.compare(120, 15));
        // espero:positivo✅
        print(new BigDecimal("5.00").compareTo(new BigDecimal("5")));
        // espero:igual✅
        print(new BigDecimal("5.00").equals(new BigDecimal("5")));
        // espero:false✅
        //leccion 5
        try {
            Customer customer1 = new Customer(new BigInteger("1"), "Javier", "javierfiligranaagreda@gmail.com");
            Customer customer2 = new Customer(new BigInteger("2"), "Daniel", "danielsalamanca@gmail.com");
            Set<Customer> setCustomer = new TreeSet<>();
            print("no");
            setCustomer.add(customer1);
            print("no1");
            setCustomer.add(customer2);
            print("no2");
            // Collections.sort(setCustomer);
            // Required type:
            //List
            //<T>
            //Provided:
            //Set
            //<Customer>
            //reason: no instance(s) of type variable(s) T exist so that Set<Customer> conforms to List<T>

        } catch (ClassCastException classCastException) {
            print(classCastException.getMessage());
            // espero: entra pero por en el primer add.✅
        }
        //  6:Comparator:
        Customer mario = new Customer(new BigInteger("1"), "mario", "lkasjdf@mgial.com");
        Customer andrea = new Customer(new BigInteger("2"), "Andrea", "aklsdjf@gmail.com");
        Customer luz = new Customer(new BigInteger("3"), "Luz", "kalsjdf@gmail.com");
        List<Customer> listCustoms = new ArrayList<>();
        listCustoms.add(mario);
        listCustoms.add(andrea);
        listCustoms.add(luz);
        listCustoms.sort(new CustomerNameComparator());
        for (Customer cus:listCustoms){
            print(cus);
        }
        // espero: Andrea,Luz, mario ✅
        print(Collections.max(listCustoms,new CustomerNameComparator()).getName());
        // espero: Andrea ✅
        // real: mario
        // por que: bueno en realidad mario es mayor que Andrea, fue una confusion mia.

        // tema 7: reversed,thenComparing
        Customer andrea1 = new Customer(new BigInteger("4"), "andrea", "aklsdjfed@gmail.com");
        listCustoms.add(andrea1);
        listCustoms.sort(new CustomerNameComparator());
        for (Customer cus:listCustoms){
            print(cus);
        }
        // espero: Andrea,andrea,Luz,mario✅
        listCustoms.sort(new CustomerNameComparator().reversed());
        print("a--------");
        for (Customer cus:listCustoms){
            print(cus);
        }
        // espero: mario,Luz,andrea,Andrea
        // real: mario,Luz,Andrea,andrea

        // tema 8: La trampa: TreeSet, TreeMap.

        // tema 9: Ordenar un map



    }

    private static void print(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }
}
/**
 * ¿Qué imprime System.out.println(lista.sort(null));?
 * R: no compila.
 * ¿Por qué List.of(1, 2).sort(null) falla si ya está ordenada?
 * R: por uqe es una lista no modificable.
 * Ordenas ["b", "B", "a"] con Collections.sort. ¿Qué sale?
 * R: sale: [B,a,b]
 */

/**
 * ¿Cuántos órdenes naturales puede tener una clase?
 * R: Solo deberia tener un solo orden natural, el cual sea unico para no intervenir o chocar con otros.
 * ¿Por qué el orden natural de Product debería ser el id y no el precio?
 * R: Por que el id si viene de la bd seria unico, mientras el precio puede repetirse en otros productos lo cual haria que se pierdan productos una vez choquen
 * Escribiste compareTo en tu clase pero TreeSet sigue lanzando ClassCastException. ¿Qué falta?
 * R: implements Comparable<MiClase>
 */

/***
 * ¿Qué diferencia hay entre compareTo(otro) y compare(a, b)?
 * R: el compareTo se lo pone dentro de una clase y es unico, mientras que compare(a,b) es para comparar dos elementos entre si, ademas que se puede poner en las clases que se desea, crearla en si
 * ¿Qué le pasas a lista.sort(...) para ordenar por salario: la clase PorSalario o un objeto?
 * R: se le pasa la clase Porsalario que implementa compare<...
 * ¿Collections.max(lista, comp) deja la lista ordenada?
 * R: no,no la ordena, solo la recorre.
 */


/**
 * ¿Qué hace thenComparing si el primer comparador no da 0?
 * R: no hace nada, solo interviene cuando hay empate.
 * Dos productos con el mismo precio, sin desempate. ¿En qué orden quedan?
 * R: en el orden natural, es decir el primero que llego, si hay empate no se re-ordenan.
 * ¿Hace falta escribir una clase PorSalarioDescendente?
 * R: No,no hace falta ya que existe el reversed.
 */

/**
 * Un TreeSet<Product> con un comparador por precio. Agregas dos productos distintos que cuestan 49.00. ¿size?
 * R: solo 1
 * ¿Por qué con List.sort el mismo comparador no "pierde" nada?
 * R: por que usa solo el compareTo
 * ¿Qué le agregas al comparador para que el TreeSet no pierda elementos?
 * R: avisarle que tambien use el orden natural thenComparing(Comparator.naturalOrder())
 */

/***
 * ¿Qué devuelve lista.sort(null)? ¿Qué le pasa a lista?
 * R: no devuelve nada
 * ¿Por qué falla List.of(2, 1).sort(null) y cómo ordenas esos datos sin que falle?
 * R: por que es una lista que nose puede modificar, tendria que ir agregand ouno a uno cada dato
 * a.compareTo(b) da -24. ¿Quién va primero? ¿Por qué no se compara con == -1?
 * R: va el a primero, nose comprara con ==-1 por que estariamos diciendo que todos los a.compareTo(b) da -1,0,1 pero no es cierto.
 * ¿Por qué no se escribe return a - b; en un comparador de int?
 * R: por que se puede salir el rango, a podria ser mayor a lo que contiene int y pues daria valores inciertos.
 * Diferencia entre Comparable y Comparator: dónde se escribe cada uno y cuántos puede haber.
 * R: El comparable es para una sola clase especifica, se usa para compararla, mientras que el comparator no, se puede tener varias, podriamos ordernar de una o otra forma dependiendo como esta la clase que implemneta ese comparator.
 * ¿Qué elegirías como orden natural de Product y por qué?
 * R: el orden natural deberia ser como viene nomas, es decir por el id y al primero que llego por que de esa forma tambien vendra de la bd, aunque personalmente me traeria product ya ordenado
 * ¿Qué hace new PorPrecio().reversed().thenComparing(new PorNombre()), en palabras?
 * R: obtiene el reverso de PorPrecio si hubiera un empate toma en cuenta el nombre.
 * Dos elementos empatan en un List.sort. ¿En qué orden quedan?
 * R: en el orden natural.
 * Un TreeSet con un comparador por precio, y dos productos distintos con el mismo precio: ¿qué pasa? ¿Cómo lo arreglas?
 * R: no se puede ordenar, se arregla sacando a una lista ordenarla y ponerla de nuevo.
 * ¿Cómo muestras un Map<String, Integer> ordenado por valor, de mayor a menor?
 * R: saco a una lista y la ordeno ahi.
 */