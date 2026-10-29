package practical.sorting;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String args[]) {
        List<String> colores = new ArrayList<>(List.of("verde", "Azul", "rojo", "Índigo", "amarillo"));
        print(colores);
        // espero: [verde,Azul]
    }

    private static void print(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }
}
