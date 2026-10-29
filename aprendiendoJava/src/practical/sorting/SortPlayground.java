package practical.sorting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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