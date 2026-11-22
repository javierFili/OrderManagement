package practical.modern;

import java.math.BigDecimal;
import java.util.*;


public class ModernPlayground {
    record Libro(String isbn, String titulo, int paginas) {
    }   // ← un record dentro de la clase


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


        print("6. Optional: \"puede que no haya resultado\"\n");
        List<String> correos = List.of("ana@mail.com", "luis@mail.com", "bea@mail.com");
        Optional<String> l = buscarPorInicial(correos, "l");
        print(l);
        //espero: optional.empty
        Optional<String> z  = buscarPorInicial(correos, "z");
        print(z);
        //espero: optional.empty

        print(z.orElse("sin correo"));

        try {
            print(z.get());
        }catch (NoSuchElementException e){
            print(e);
            // espero:  java.ultil.NoSuchElementException
        }

        print("7. var: que Java adivine el tipo");
        var mitad = 7 / 2;
        print(mitad);
        // espero: 3

        var mitad2 = 7 / 2.0;
        print(mitad2);
        // espero: 3.5

        //m1.getAmount();


    }

    static Optional<String> buscarPorInicial(List<String> correos, String inicial) {

        for (String correo : correos) {
            if (correo.equals(inicial)) {
                return Optional.of(inicial);
            }
        }
        return Optional.empty();
    }

    private static void print(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }

}

/***
 * ¿Qué genera Java por ti cuando escribes record Punto(int x, int y) {}? Nombra cuatro cosas y di cómo se llama el accesor de x.
 * 	R: el accesor es record.x, ademas genera el toString, hashcode, getters, equals
 *
 * En un constructor compacto, ¿qué no puedes escribir y qué sí puedes escribir para cambiar un dato?
 * 	R: no puedo escribir el constructor completo, pero si puedo validar sus datos cuando el objeto se esta construyendo.
 *
 * record Precio(BigDecimal valor): ¿new Precio(new BigDecimal("5.0")).equals(new Precio(new BigDecimal("5.00"))) da true o false? ¿Por qué, y cómo se arregla?
 * 	R: 	Devuelve false, por que simbolicamente el 5.0 y 5.00 son diferentes, la forma de arreglarlo seria quitar los ceros ala derecha al momento de comparar.
 *
 * Un record tiene un campo List<String>. ¿Cómo lo pueden cambiar desde fuera (dos formas) y cómo se impide?
 * 	R: pues cuando hacen un get obtiene el objeto como tal y puedes pueden modificarlo, se impide si devolvemos el unmodificable de la clase collections.
 *
 * Entidad vs objeto de valor: da un ejemplo de cada uno de tu proyecto y di cuál es record.
 * 	R: nose!!
 *
 * ¿Por qué un enum es mejor que un String para un estado, y por qué no se usa ordinal() para la lógica?
 * 	R: Por que el enum tiene un lista estados que son unicos y que los mismos no pueden fallar por que si te equivocas sintacticamente no compila, por que el ordinal puede cambiar especialmente si te lo guardar en la db.
 *
 * ¿Qué es el fall-through del switch viejo, y por qué el switch con -> no lo tiene?
 * 	R: por que no necesita del break para salir el switch
 *
 * ¿Por qué un switch expresión sobre un enum no debería tener default? ¿Qué pasa si agregas un valor nuevo al enum?
 * 	R: no deberia tener un default por que si agregamos un nuevo valor el enum ira en default y el compilador no te molestara.
 *
 * ¿Cuándo devuelves Optional y en qué tres lugares no se usa?
 * 	R: Cuando son list vacios, cuando sabes que el metodo devuelve valores diferentes de null.
 *
 * ¿Qué pasa con opt.get() si está vacío, y qué dos alternativas seguras tienes?
 * 	R: si opt.get() esta vacio sale un error, si puede preguntar si opt esta vacio o directamente usar el orelse en el optional.
 *
 */


