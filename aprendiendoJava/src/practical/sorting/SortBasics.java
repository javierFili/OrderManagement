package practical.sorting;

import java.math.BigDecimal;
import java.util.*;

public class SortBasics {

    public static void main(String args[]) {
        print("-----Parte A ----");
        List<String> ciudades = new ArrayList<>(List.of("lima", "Quito", "Bogotá", "asunción", "Cusco", "Ébano"));
        Collections.sort(ciudades);
        print(ciudades);
        // espero: Bogotá, Cusco, Quito, asunción, lima, Ébano ✅
        ciudades.sort(String.CASE_INSENSITIVE_ORDER);
        print(ciudades);
        // espero: asuncion,bogota,cusco,lima,quito,ebano ✅
        ciudades.sort(Comparator.reverseOrder());
        print(ciudades);
        //espero: Ébano,lima,asunción,Cusco,Quito,,Bogotá✅

        print("-----Parte B ----");

        List<Integer> stock = new ArrayList<>(List.of(15, 4, 120, 4, 0, 33));
        stock.sort(null);
        print(stock);
        //espero: 0,4,4,15,33,120

        List<BigDecimal> precios = new ArrayList<>(List.of(
                new BigDecimal("19.90"), new BigDecimal("5"), new BigDecimal("100"),
                new BigDecimal("0.50"), new BigDecimal("5.00")));
        precios.sort(null);
        print(precios);
        //espero: 0.50,5,5.00,19.90,100✅

        print(new HashSet<>(precios).size());
        //espero: 5✅
        print(new TreeSet<>(precios).size());
        //espero:4✅

        print("-----Parte C ----");
        // List<String> r = ciudades.sort(null);
        // espero: no compila, no se puede compilar por que .sort devuelve void.

        List<String> tallas = List.of("M", "S", "XL", "L");
        try {
            tallas.sort(null);

        } catch (UnsupportedOperationException e) {
            print("no se puede ordenar" + e.getMessage());
            // espero:✅
        }
        List<String> tallasOrdenadas = new ArrayList<>(tallas);
        tallasOrdenadas.sort(null);
        print(tallasOrdenadas);
        //espero: M,L,S,XL✅

    }

    private static void print(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }
}
