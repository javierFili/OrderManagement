package practical.modern;

import Models.Order;
import Models.OrderStatus;

import javax.print.attribute.standard.PrinterInfo;
import java.util.ArrayList;
import java.util.List;

public class EnumLab {

    public static void main(String[] args) {

        for (OrderStatus status : OrderStatus.values()) {
            print(status.name() + " " + status.getLabel() + " " + status.isFinal());
        }
        //espero: PENDING Pendiente false
        //espero: PAID Pagado false
        //espero: SHIPPED Enviado false
        //espero: DELIVERED Entregado true
        //espero: CANCELLED Cancelado true

        print("----Paso 4-----");

        try {
            print(OrderStatus.valueOf("PAID"));
            //espero: PAID
        } catch (IllegalArgumentException e) {
            print(e);
        }

        try {
            print(OrderStatus.valueOf("PAID") == OrderStatus.PAID);
            // espero: true
        } catch (IllegalArgumentException e) {
            print(e);
        }

        try {
            print(OrderStatus.valueOf("paid"));
            // espero:
        } catch (IllegalArgumentException e) {
            print(e);
            // espero: illegalArgumentException
        }

        try {
            print(OrderStatus.valueOf("Pagado"));
        } catch (IllegalArgumentException e) {
            print(e);
            // espero: illegalArgumentException
        }

        print("-----Paso 5-------");

        /**
         * desde \ hacia	 PENDING	 PAID	 SHIPPED	 DELIVERED	    CANCELLED
         * PENDING            no          si       no            no              si  -/
         * PAID               no          no       si            no              si  -/
         * SHIPPED            no          no       no            si              no  -/
         * DELIVERED          no          no       no            no              no  -/
         * CANCELLED          no          no       no            no              no  -/
         */

        for (OrderStatus status : OrderStatus.values()) {
            String res = status.getLabel() + "-->     ";
            for (OrderStatus status1 : OrderStatus.values()) {
                res += (status.canTransitionTo(status1) ? " si " : " no ");
            }
            System.out.println(res);
        }

        print("----paso 6 ----");
        print(OrderStatus.PENDING.compareTo(OrderStatus.SHIPPED));
        //espero: -

        print(OrderStatus.CANCELLED.compareTo(OrderStatus.PAID));
        //espero: +

        print("------Paso 7 -----");
        //'switch' expression does not cover all possible input values
    }


    private static void print(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }
}


/***
 * P3.1 "valueOf("Pagado") falla porque valueOf busca por estado, no por valor."
 * P3.2 "Las reglas de transición van dentro del enum (y no en Order ni en Main) porque por que son propias del emun, tambien eso hace que sea reutilziable y no modificable por otras clases. Si mañana agrego un estado RETURNED, el compilador falla, por la exhaustividad."
 * P3.3 "No uso default en el switch de canTransitionTo porque todos tiene un siguiene estado, ademas que si uso default y mas adelante agrego un estado podria entrar en error."
 * P3.4 "No usaría ordinal() para decidir si se puede avanzar (por ejemplo, next.ordinal() > ordinal()) porque en un futuro puede cambiar y además en nuestro caso no esta por ordinal." ← pista para la segunda parte: ¿CANCELLED está "después" de SHIPPED en la declaración? ¿Se puede pasar de SHIPPED a CANCELLED?
 */
