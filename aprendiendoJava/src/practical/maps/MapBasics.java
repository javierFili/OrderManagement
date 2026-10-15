package practical.maps;

import java.math.BigDecimal;
import java.util.*;

public class MapBasics {

    public static void main(String args[]) {
        Map<String, BigDecimal> precios = new LinkedHashMap<>();
        printl(precios.put("teclado", new BigDecimal("45.90")));
        // espero: null;✅
        precios.put("monitor", new BigDecimal("189.00"));
        precios.put("cable", new BigDecimal("7.50"));
        // espero: null,null✅
        printl(precios);
        ;
        // espero: {teclado=49.90,monitor=189.00,cable=39.90}✅
        printl(precios.size());
        // espero: 3✅

        printl("----T4---");
        printl(precios.put("teclado", new BigDecimal("39.90")));
        // espero: 45.90 ✅
        printl(precios);
        // espero:{teclado=39.90,monitor=189.00,cable=7.50}✅
        printl(precios.size());
        // espero: 3 ✅
        printl("----T5---");
        printl(precios.get("mouse"));
        // espero: null ✅

        printl("-----T6---");
        printl(precios.getOrDefault("mouse", BigDecimal.ZERO));
        // espero: 0 ✅

        printl("-----T7-----");
        try {
            BigDecimal conIva = precios.get("mouse").add(new BigDecimal("1"));
        } catch (NullPointerException e) {
            printl("NPE: mouse no existe");

        }
        // espero: NPE: mouse no existe, si entra al catch por que no encontro valor valido pra sumar entonces se rompio.
        printl("-----T8---");
        printl(precios.containsKey("cable"));
        // espero: true✅
        printl(precios.containsValue(new BigDecimal("7.50")));
        // espero: true ✅
        printl("------T9---");
        printl(precios.containsValue(new BigDecimal("7.5")));
        // espero: false✅

        printl("------T10-----");
        printl(precios.remove("cable"));
        //espero: true
        // real: 39.90 por que: el remove me devuelve el objeto que vamos a remover
        printl(precios.remove("cable"));
        // espero: false
        // real: null por que: el remove busco para  remover y no lo encontro.

        printl("---TAREA2------");
        Map<String, BigDecimal> catalogo = new LinkedHashMap<>();
        catalogo.put("notebook", new BigDecimal("899.99"));
        catalogo.put("mouse", new BigDecimal("19.90"));
        catalogo.put("webcam", new BigDecimal("54.00"));
        catalogo.put("pendrive", new BigDecimal("8.75"));
        catalogo.put("audifonos", new BigDecimal("35.00"));
        printl(catalogo.keySet());
        //espero: [notebook, mouse, webcam, pendrive, audifonos]✅
        printl("---T2---");
        for (Map.Entry<String, BigDecimal> item : catalogo.entrySet()) {
            System.out.println(item.getKey() + "= ($" + item.getValue() + ")");
            // espero: notebook=($899.99)✅
        }

        printl("-----T3-----");
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal sum : catalogo.values()) {
            total = total.add(sum);

        }
        printl(total);
        // espero: 1017.64✅

        printl("-----T4-----");
        String masCaroKey = null;
        BigDecimal mascaroValue = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> item : catalogo.entrySet()) {
            if (item.getValue().compareTo(mascaroValue) == 1) {
                masCaroKey = item.getKey();
                mascaroValue = item.getValue();
            }
        }
        printl("Mas caro:" + masCaroKey + "($" + mascaroValue + ")");
        //espero: Mas caro:notebook($899.99)✅

        printl("-----T5-----");
        try {
            for (Map.Entry<String, BigDecimal> item : catalogo.entrySet()) {
                if (item.getValue().compareTo(new BigDecimal("10.00")) == -1) {
                    catalogo.remove(item.getKey());
                }
            }
        } catch (ConcurrentModificationException e) {
            printl("No se puede modificar");
        }
        // espero: no se puede modificar

        printl("------T6----");
        // como es que me das lambda si no se eso, osea me estas dando tareas que no se como hacer, la are de otroa forma vale?, que no se repita porfa!!
        List<String> aEliminar = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> item : catalogo.entrySet()) {
            if (item.getValue().compareTo(new BigDecimal("10.00")) == -1) {
                aEliminar.add(item.getKey());
            }
        }
        for(String key: aEliminar){
            catalogo.remove(key);
        }
        printl(catalogo);
        // espero: {notebook=899.99, mouse=19.90, webcam=54.00, audifonos=35.00}



    }

    public static void printl(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }
}

/**
 * 1: put devolvio el objeto anterior por que ya existia una llave igual por lo tanto se sustituto. Despues el put, size es el mismo por que no se agrego se sustituyo
 * 2: Exploto por que get("mouse") devolvio null ,y en la mismoa se pude sumar, No es unboxing por que BigDecimal es un  objeto, pero igaul nose puede llmar a un metodo sobre null.
 * 3: containsValue con 7.5 dio null por que BigDecinal.equals es sencible y comprar los decimales, PAra comparar dinero sim importar los ceros usa Compareble.
 */

/**
 * Respuestas de la tarea2
 * 1: Para el paso2 usee entrySey() por que necesitaba tanto el key como el value de mi mapa, para el caso 3 use solo el values por que solo necesitaba hacer una simple suma.
 * 2: en el paso 5, el for-each exploto por que usa un iterador, validodor interno que detecto que el mpa se modifico y yo cambie el mapa con remove en vez de con ese removeif
 */