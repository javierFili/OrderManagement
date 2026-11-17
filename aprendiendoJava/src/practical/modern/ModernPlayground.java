package practical.modern;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

record Libro(String isbn, String titulo, int paginas) {
}   // ← un record dentro de la clase


public class ModernPlayground {
    record Ciudad(String nombre, int habitantes) {
        Ciudad {
            if (habitantes < 0) {
                throw new IllegalArgumentException("Habitantes negativos");
            }
        }
    }

    public static void main(String[] args) {
        BigDecimal precio = new BigDecimal("20.00");
        print(precio.multiply(new BigDecimal("2")));
        //espero: 40 pero en otro objeto BigDecimal que s el resultado de 20 x 2


        final List<Integer> numeros = new ArrayList<>();
        numeros.add(1);
        numeros.add(2);
        print(numeros);
        // espero: [1,2], si compila.
        // numeros = new ArrayList<>();
        // Cannot assign a value to final variable 'numeros'
        print("-----prueba seccion 3---");
        Ciudad c1 = new Ciudad("Arquipa", 100000);
        Ciudad c2 = new Ciudad("Arquipa", 100000);
        print(c1 == c2);
        // espero: false ✅

        print(c1.equals(c2));
        // espero: true ✅
        print(c1.nombre);
        // espero: Arquipa ✅

        // c1.getNombre();
        //Cannot resolve method 'getNombre' in 'Ciudad'
        try {
            Ciudad c3 = new Ciudad("Error", -1);
        } catch (IllegalArgumentException e) {
            print(e);
            // error: IllegalArgumentException: Habitantes negativos. ✅
        }
        record Precio(BigDecimal valor) {

        }

        print(new Precio(new BigDecimal("3.0")).equals(new Precio(new BigDecimal("3.00"))));
        // espero: false ✅

        print("-----seccion 4----");
        enum Dia {
            LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO;

            public boolean esFinDeSemana(Dia dia) {
                return switch (this) {
                    case SABADO -> SABADO == dia || dia == DOMINGO;
                    case DOMINGO -> dia == SABADO || dia == DOMINGO;
                    case LUNES, MARTES, MIERCOLES, JUEVES, VIERNES -> false;
                };
            }
        }
        print(Dia.values());
        // espero: LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO
        print(Dia.VIERNES.ordinal());
        // espero: 4
        print(Dia.valueOf("SABADO"));
        // espero: SABADO

        try {
            print(Dia.valueOf("Sabado"));
        } catch (IllegalArgumentException e) {
            print(e);
            // espero: IllegalArgumentException
        }
        // toca: switch modermo

    }

    private static void print(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }

}
