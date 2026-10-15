package practical.maps;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapBasics {

    public static void main(String args[]) {
        Map<String, BigDecimal> precios = new LinkedHashMap<>();
        printl(precios.put("teclado",new BigDecimal("45.90")));
        // espero: null;✅
        precios.put("monitor",new BigDecimal("189.00"));
        precios.put("cable", new BigDecimal("39.90"));
        // espero: null,null✅
        printl(precios);;
        // espero: {teclado=49.90,monitor=189.00,cable=39.90}✅
        printl(precios.size());
        // espero: 3✅

        printl("----T4---");
        printl(precios.put("teclado",new BigDecimal("39.90")));
        // espero: 45.90 ✅

        printl("----T5---");
        printl(precios.get("mouse"));
        // espero: null ✅

        printl("-----T6---");
        printl(precios.getOrDefault("mouse",BigDecimal.ZERO));
        // espero: 0 ✅

        printl("-----T7-----");
        try {
            BigDecimal conIva = precios.get("mouse").add(new BigDecimal("1"));
        }catch (NullPointerException e){
            printl("NPE: mouse no existe");

        }
        // espero: NPE: mouse no existe, si entra al catch por que no encontro valor valido pra sumar entonces se rompio.
        printl("-----T8---");
        printl(precios.containsKey("cable"));
        // espero: true
        printl("------T9---");
        printl(precios.containsValue(new BigDecimal("7.5")));
        // espero: false

        printl("------T10-----");
        printl(precios.remove("cable"));
        //espero: true
        // real: 39.90 por que: el remove me devuelve el objeto que vamos a remover
        printl(precios.remove("cable"));
        // espero: false
        // real: null por que: el remove busco para  remover y no lo encontro.



    }
    public static void printl(Object ... o){
        for (Object item:o){
            System.out.println(item);
        }
    }
}

/**
 * 1: put devolvio el objeto anterior por que ya existia una llave igual por lo tanto se sustituto. Despues el put, size es el mismo por que no se agrego se sustituyo
 * 2: Exploto por que get("mouse") devolvio null ,y en la mismoa se pude sumar, No es unboxing por que BigDecimal es un  objeto, pero igaul nose puede llmar a un metodo sobre null.
 * 3: containsValue con 7.5 dio null por que BigDecinal.equals es sencible y comprar los decimales, PAra comparar deinero sim importar los ceros usa Compareble, creo que tenemos sobreescribit ese metodo.
 */