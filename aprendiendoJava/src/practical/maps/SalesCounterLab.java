package practical.maps;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SalesCounterLab {
    public static void main(String args[]) {
        List<String> vendidos = List.of("pendrive", "mouse", "pendrive", "webcam", "mouse", "pendrive", "notebook", "mouse");
        Map<String, Integer> cuenta1 = new LinkedHashMap<>();
        for (String item : vendidos) {
            if (cuenta1.containsKey(item)) {
                cuenta1.put(item, cuenta1.get(item) + 1);
            } else {
                cuenta1.put(item, 1);
            }
        }
        printl(cuenta1);
    }

    public static void printl(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }
}
/**
 * | Vuelta | p          | ¿Estaba? (sí/no) | Qué hace (put ...) | Mapa después |
 * |--------|-------     |------------------|-------------------- |--------------|
 * | 1      | pendrive   |      no          | put(pendrive,1)     | {pendrive=1}|
 * | 2      | mouse      |     no           | put(mouse,1)        | {pendrive=1,mouse=1}|
 * | 3      | pendrive   |     si           | put(pendrive,1+1    |{pendrive=2,mouse=1}|
 * | 4      | webcam     |     no           | put(pendrive,1)     |{pendrive=2,mouse=1, webcam=1}|
 * | 5      | mouse      |     si           | put(mouse,1+1)      |{pendrive=2,mouse=2, webcam=1}|
 * | 6      | pendrive   |     si           | put(pendrive,2+1)   |{pendrive=3,mouse=2, webcam=1}|
 * | 7      | notebook   |     no           | put(notebook,1)     |{pendrive=3,mouse=2, webcam=1,notebook=1}|
 * | 8      | mouse      |     no           | put(mouse,2+1)     |  {pendrive=3,mouse=3, webcam=1,notebook=1}|
 */