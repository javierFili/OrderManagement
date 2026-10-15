package practical.maps;

import Models.Product;
import Models.products.Laptop;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

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


        /**
         * pruebas 7:
         */
        System.out.println("pruebas 77777");
        Map<Product, Integer> reservas = new HashMap<>();
        Product laptop = new Laptop(new BigInteger("9"),"laptopss",new BigDecimal("33.4"),new BigDecimal("43.5"));
        Product laptop1 = new Laptop(new BigInteger("9"),"laptopss11",new BigDecimal("333.4"),new BigDecimal("53.5"));
        System.out.println(reservas.put(laptop,1));
        // espero null
        System.out.println(reservas.put(laptop,5));
        // espero: la laptop que agrege primero.
        System.out.println(reservas.size());
        // espero: 1
        System.out.println(reservas.get(laptop1));
        // espero: 5
        // 1:  si product tuviera equals no lo encontraria, ya que no sabe en que cajon esta
        // 2:no necesariamente, temos la validacion del equals aun.

        // respuesta para la 8
        // 1:vamos a usar linkedHashMap
        // 2:ordena por la clave

        // respuesta para la 10:
        // 1: Collections.unmodifiableXxx
        // 2: pues se mezclan ademas para mapas fijos con menos de 10

        

    }

    private static void imprimir(Objects o){
        System.out.println(o);
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

/**
 * autoevalucion:
 * 1: el put devuelve el valor anterior y solo devuelve booelan
 * 2:la primera vez devuelve null, la segunda devuelve lo que se puso en la primera
 * 3:es posible que no exista ese get("x")
 * 4: se usa el entrySet
 * 5:
 * 6: suma 1 a k
 * 7:necesita que se sobre escriba el metodo equals y el hashcode
 * 8:
 * 9: el por defecto es el hash map, el hahmap se usa cuando no te importa el orde, linkendhashmap cuando te importa el orden TreeMap para tenerlos en orden alfabetico
 * 10: por que los mezcla.
 *
 */

/**
 * votos = rojo, azul, rojo, verde, azul, rojo, rojo
 *
 * | Vuelta | p     | ¿Estaba? (sí/no) | Qué hace (put ...) | Mapa después |
 * |--------|-------|------------------|--------------------|--------------|
 * | 1      | rojo  |                  |                    |              |
 * | 2      | azul  |                  |                    |              |
 * | 3      | rojo  |                  |                    |              |
 * | 4      | verde |                  |                    |              |
 * | 5      | azul  |                  |                    |              |
 * | 6      | rojo  |                  |                    |              |
 * | 7      | rojo  |                  |                    |              |
 *
 *
 */