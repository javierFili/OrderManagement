package practical.maps;

import java.util.*;

public class MapOrderLab {

    public static void main(String args[]) {
        Map<String, Integer> hasmap = new HashMap<>();
        Map<String, Integer> linke = new LinkedHashMap<>();
        Map<String, Integer> treemap = new TreeMap<>();
        hasmap.put("2026-03", 40);
        hasmap.put("2025-10", 75);
        hasmap.put("2026-10", 12);
        hasmap.put("2026-01", 33);

        linke.put("2026-03", 40);
        linke.put("2025-10", 75);
        linke.put("2026-10", 12);
        linke.put("2026-01", 33);

        treemap.put("2026-03", 40);
        treemap.put("2025-10", 75);
        treemap.put("2026-10", 12);
        treemap.put("2026-01", 33);

        printl(hasmap);
        // espero:{2026-03=40,2026-10=75,2025-10=12,2026-01=33} ✅

        printl(linke);
        // espero:{2026-03=40, 2025-10=75, 2026-10=12, 2026-01=33} ✅

        printl(treemap);
        // espero:{2025-10=75, 2026-01=33, 2026-03=40, 2026-10=12} ✅
        printl("------T4----");
        Map<String,Integer> treemap2 = new TreeMap<>();
        treemap2.put("2026-3", 40);
        treemap2.put("2025-12", 75);
        treemap2.put("2026-10", 12);
        treemap2.put("2026-1", 33);
        printl(treemap);
        // espero:{2025-10=75, 2026-01=33, 2026-03=40, 2026-10=12} ✅

        printl(linke.put("2026-03", 41));
        // espero: 40, se qieda en su lugar, porque tendria que moverse? es acaso nuevo?✅
        printl(linke);
        // espero: {2026-03=41, 2025-10=75, 2026-10=12, 2026-01=33}✅

        printl("----parte C----");

        Map<String, Integer> soloLectura = Collections.unmodifiableMap(linke);
        Map<String, Integer> copia = Map.copyOf(linke);
        printl(soloLectura);
        // espero: en orden, el unmodificableMap no mezcla la lista✅
        printl(copia);
        // espero: orden aleatorio, ese copyOf mezcla ✅
        // cambiar el copyOf..

        try {
            soloLectura.put("2026-11",5);
        } catch (UnsupportedOperationException e) {
            // espero: entra al catch por que no se puede modificar ese map
        }
        linke.put("2026-11",5);
        printl(soloLectura);
        //espero:  {2026-03=41, 2025-10=75, 2026-10=12, 2026-01=33, 2026-11=5}✅
    }

    public static void printl(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }
}
/**
 * Parte A — ventas por mes
 * <p>
 * Crea tres mapas Map<String, Integer>: HashMap, LinkedHashMap y TreeMap.
 * Mete en los tres, en este orden, las ventas de cada mes (clave "año-mes", valor = cantidad de ventas): "2026-03" → 40, "2025-12" → 75,
 * "2026-10" → 12, "2026-01" → 33.
 * Imprime los tres. // espero:
 * HashMap: "orden impredecible" y qué entradas.
 * LinkedHashMap y TreeMap: el orden exacto.
 * La trampa: crea un TreeMap nuevo con las mismas ventas, pero escribiendo los meses sin el cero: "2026-3", "2025-12", "2026-10", "2026-1". Imprime. // espero: el orden exacto.
 * Pista: TreeMap<String, ...> ordena texto, letra por letra, como un diccionario. No sabe que son fechas. Compara "2026-1" con "2026-10" y con "2026-3" carácter por carácter.
 */

/***
 * Parte B — reinsertar una clave en LinkedHashMap
 *
 * En tu LinkedHashMap del paso 2, haz put("2026-03", 41) (corregir una venta de marzo).
 * Imprime. // espero: → ¿marzo se mueve al final (como si fuera nuevo) o se queda en su lugar?
 *
 * Esto es clave para la Tarea 6: cuando agregues laptop por segunda vez, ¿la línea Laptop se va al final del pedido o se queda donde estaba?
 */

/**
 * Parte C — devolver un mapa sin perder el orden
 * <p>
 * Crea Map<String, Integer> soloLectura = Collections.unmodifiableMap(linked); y Map<String, Integer> copia = Map.copyOf(linked);.
 * Imprime los dos. // espero: → ¿cuál conserva el orden? Ejecuta 3 veces y anota lo que sale en cada una.
 * Dentro de un try/catch (UnsupportedOperationException e), intenta soloLectura.put("2026-11", 5). // espero:
 * Haz linked.put("2026-11", 5) (en el original) y después imprime soloLectura. // espero: → ¿la vista ve el cambio?
 */

/**
 * Respuestas a las preguntas
 * <p>
 * P5.1 "Con los meses sin cero, TreeMap los ordenó de otra forma  porque compara cadenas y no numeros. Para ordenar fechas de verdad, la clave debería ser una fecha (semana 6: java.time), no un String."
 * P5.2 "Al hacer put sobre una clave que ya existe en un LinkedHashMap, la entrada no se modifica  porque el orden de inserción es el de la primera vez que llegó la la clave."
 * P5.3 "Para devolver el mapa interno de una clase sin perder el orden y sin que lo modifiquen, uso unmodificableMap. No uso Map.copyOf porque mezcla los datos."
 */