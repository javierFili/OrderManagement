package practical.sets;

import java.util.HashSet;
import java.util.Set;

public class SetBasics {
    public static void main(String args[]) {
        //tarea1
        Set<String> names = new HashSet<>();
        //tarea2
        System.out.println(names.isEmpty());
        // espero: true  ✅
        System.out.println(names.size());
        // espero: 0  ✅

        //tarea3
        boolean first = names.add("Ana");
        System.out.println(first);
        // espero: true  ✅
        System.out.println(names.size());
        // espero: 1   ✅

        //tarea4
        boolean second = names.add("Ana");
        System.out.println(second);
        // espero:  false  ✅
        System.out.println(names.size());
        //espero: 1  ✅

        //tarea5
        names.add("Luis");
        names.add("Marta");
        names.add("ana");
        System.out.println(names.size());
        //espeo 4  ✅
        System.out.println(names);
        // espero: [Ana,Luis,Marta,ana];
        // salio: [Marta, Ana, ana, Luis];
        // por que: la verdad nose, esperaba que tu me expliques por que se mezclo asi, yo no le veno un orden logico.. pero si note que nunca cambia.
        System.out.println("6-----------");
        System.out.println(names.contains("Ana"));
        // espero: true  ✅
        System.out.println(names.contains("ANA"));
        // espero: false ✅
        boolean removeLuis = names.remove("Luis");
        System.out.println(removeLuis);
        // espero true ✅
        System.out.println(names.remove("Pedro"));
        // espero false ✅
        System.out.println(names);
        // espero: [Marta, Ana, ana] ✅

        System.out.println("8-----------");
        // names.get(0); ✅
        // salio el siguiente mensaje: symbol:   method get(int), cannot find symbol, variable names of type java.util.set<java.lang.String>..
        for (String name : names) {
            System.out.println(name);
            // espero cada nombre indiviualmente en cada sola linea. ✅
        }

        System.out.println("9-----------");
        try {
            Set<String> fixed = Set.of("x", "y");
            fixed.add("z");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getClass().getSimpleName());
            // espero: nose que espero realmente
            // real: UnsupportedOperationException
            //por que: parece  ser  que es una excepcion general para este tipo de errores,osea es una clase para los no soportados.
            // ademas que no me deja agregar o usar el add, ya que ese trata de un objeto inmutable.✅
        }
    }
}
/**
 * 1: Significa que le objeto ya existia en la lista set, no se reemplza por que ya existe.
 * 2: Por que  que el indice posicional no se maneja, solo es una coleccion desordenada de elementos unicos.
 * 3: por que java hace diferencia entre cademas de caracteres con mayusculas y minusculas.
 */
