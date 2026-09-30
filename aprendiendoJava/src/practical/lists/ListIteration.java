package practical.lists;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

public class ListIteration {
    public static void main(String args[]) {
        System.out.println("1------------");
        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        System.out.println("2------------");
        for (Integer num : numbers) {
            System.out.println(num);
        }
        System.out.println("3------------");
        Integer sum = 0;
        for (Integer num : numbers) {
            sum += num;
        }
        System.out.println(sum);
        System.out.println("4------------");
        List<Integer> t = new ArrayList<>(List.of(10, 20, 30));
        t.remove(1);
        System.out.println(t);
        ArrayList<Integer> t1 = new ArrayList<>(List.of(10,20,30));
        t1.remove(Integer.valueOf(10));
        System.out.println(t1);

        System.out.println("5------------");
        try {
            for (Integer num : numbers) {
                if ((num % 2) == 0) {
                    numbers.remove(num);
                }
            }

        } catch (ConcurrentModificationException e) {
            System.out.println(e.getMessage());
            System.out.println(numbers);
        }

        System.out.println("6------------");
        List<Integer> numbers1 = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Iterator<Integer> it = numbers1.iterator();
        while (it.hasNext()) {
            Integer num = it.next();
            if (num % 2 == 0) {
                it.remove();// se elimina de forma segura.
            }
        }

        System.out.println(numbers1);
        System.out.println("7------------");
        List<Integer> numbers2 = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        numbers2.removeIf(n -> n % 2 == 0);
        System.out.println(numbers2);
        System.out.println("8------------");
        try {
            List<Integer> fixed = List.of(1, 2, 3);
            fixed.add(4);
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());

        }
    }
}
/**
 * 1: por que en el paso 6 estamos usando un iterador es decir que usando una condicion while nos estamos asegurando que existe la siguiente objeto para asi no desborarnos, mieentras que
 * en la 5 estamos eliminando directamente sin aseguarnos el que pasara despues
 * 2: el paso 2 funciono como deberia funcionar, es decir elimino, quzas no hize bien el procedimiento?
 * 3: se usa el list.of para cuando quieres que la lista que estar creando sea inmutable, mientras que el new array es para crear listar que posteriormente puedan crecer o no
 */