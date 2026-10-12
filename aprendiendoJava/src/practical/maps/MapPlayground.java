package practical.maps;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapPlayground {
    public static void main(String[] args) {
        Map<String, Integer> m = new HashMap<>();
        // espero: no compila..
        // cuando se le cambia por Integar si compila.
        //lo entendiste?
        // 1: La clave es la casilla donde se guardara y posteriormente se vbuscara mientras que el valor es el

        // pruebas
        Map<String, Integer> edades = new LinkedHashMap<>();
        System.out.println(edades.put("Rosa",30));
        //espero: null
        System.out.println(edades.put("Rosa",31));
        // espero: 30
        System.out.println(edades.get("Rosa"));
        // espero: 31
        System.out.println(edades.size());
        // espero 1
        System.out.println(edades.get("Tito"));
        // espero: null
        System.out.println(edades.getOrDefault("Tito",-1));
        // -1
        try {
            int e = edades.get("Tito");

        }catch (NullPointerException ex){
            System.out.println("NPE: Tito no existe");
            // si entra en el cash obvio
        }
        // 5  recorrer un Map
        Map<String, Integer> s = new LinkedHashMap<>();
        s.put("camisa", 7);
        s.put("pantalon", 4);
        s.put("gorra", 2);

        System.out.println(s.keySet());     // → [camisa, pantalon, gorra]
        System.out.println(s.values());     // → [7, 4, 2]
        System.out.println(s.entrySet());   // → [camisa=7, pantalon=4, gorra=2]

        for (Map.Entry<String, Integer> e : s.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
        // → camisa -> 7
        // → pantalon -> 4
        // → gorra -> 2
        System.out.println("pruebas 5--------------------------");
        edades.put("Daniel",26);
        edades.put("Jonas",44);
        System.out.println(edades.keySet());
        // espero: rosa, danil, jonas
        System.out.println(edades.values());
        // espero: 31,26,44
        System.out.println(edades.entrySet());
        // espero: rosa=31,daniel=26,jonas=44
        int totalSuma = 0;
        for (Map.Entry<String,Integer> e : edades.entrySet()){
            System.out.println(e.getKey()+" tiene "+e.getValue()+" anios");
            // espero rosa tiene 31 anios....
            totalSuma += e.getValue();
        }
        System.out.println(totalSuma);
        // espero: 31+26+44=101

    }
}
/**
 * Prueba s4
 * 1: vale 2, y el get("a") es de 1.
 * 2: vale null
 * 3: por que m.get("zzz") no existe, entonces y estamos por asignar a un int un valor null.
 */

/**
 * prueba s5
 * 1: por que son las key del map, las cuales no deberian de respetirse y values solo son los valores, es decir pueden repetir asi que no tenemos problemas que sean repetidos.
 * 2: la de entrySet().
 */