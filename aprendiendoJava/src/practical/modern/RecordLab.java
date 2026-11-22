package practical.modern;

import Models.Money;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

public class RecordLab {
    public static void main(String[] args) {
        Money m1 = new Money(new BigDecimal("10.5"));
        Money m2 = new Money(new BigDecimal("10.50"));
        Money m3 = new Money(new BigDecimal("10.5"));

        print(m1);
        //espero: Money[amount=10.5]✅
        //espero_normalizado: Money[amount=10.50]✅

        print(m1.amount());
        //espero:10.5✅
        //espero_normalizado: 10.50✅

        print(m1 == m3);
        //espero: false✅
        //espero_normalizado: false✅

        print(m1.equals(m3));
        //espero: true✅
        //espero_normalizado: true✅

        print(m1.equals(m2));
        //espero: false✅
        //espero_normalizado: true✅

        print(m1.amount().compareTo(m2.amount()));
        //espero: 0✅
        //espero_normalizado: 0✅

        print(new HashSet<>(List.of(m1, m2)).size());
        //espero: m1 Money{amount=10.5}
        // real: 2
        // por que: bueno dos erroes mios no vi el size, ademas pense que iban como el mismo hashcode, pero se genera hascode de 10.5 y de 10.50 que son diferentes.
        //espero_normalizado: 1.

        print("----Parte B----");

        print("---Paso 6---");

        print(new Money(new BigDecimal("3.333")));
        // espero: Money[amount=3.33]✅
        print(new Money(new BigDecimal("3.335")));
        // espero: Money[amount=3.34]✅
        print(new Money(new BigDecimal("7")));
        // espero: Money[amount=7.00]✅
        try {
            print(new Money(new BigDecimal("-1")));
            print(new Money(null));
        } catch (IllegalArgumentException e) {
            print(e);
            //espero: en ambos casos entra por aqui.✅
        }

        print("----Parte C — comportamiento y la trampa de siempre (pasos 7 a 9)----");

        print("----paso 8---");
        /**
         *   Money m1 = new Money(new BigDecimal("10.5"));
         *         Money m2 = new Money(new BigDecimal("10.50"));
         *         Money m3 = new Money(new BigDecimal("10.5"));
         */
        m1.add(m2);
        System.out.println(m1);
        // espero: 10.5✅
        Money total = m1.add(m2);
        System.out.println(total);
        // espero: 21.00✅
        System.out.println(m1.multiply(3));
        // espero: 31.50✅
        System.out.println(total.isGreaterThan(m1) + " " + m1.isGreaterThan(m2));
        // espero: true false✅
        System.out.println(Money.ZERO);
        // espero: 0.00✅

    }

    private static void print(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }

}

/**
 * P1.1 "m1 == m3 dio false porque compara memoria; m1.equals(m3) dio false porque compara estrictamente 10.5 y 10.50 que no son iguales."
 * P1.2 "Sin normalizar, m1.equals(m2) dio true porque el equals del record usa el equals de BigDecimal, que mira 10.5. Después de setScale(2, ...) dio 10.50 porque esta poniendo a dos decimales."
 * P1.3 "En el paso 8, después de m1.add(m2); m1 vale 10.50, porque add devuelve un objeto nuevo y yo no modifica el m1."
 * P1.4 "Money es un objeto de record (no una entidad) porque nosee. Por eso está bien que sea record, y Product no, porque product ya es un objeto mas complejo que deberia ser personalizado."
 *
 */






















