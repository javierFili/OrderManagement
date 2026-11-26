package practical.modern;

import Models.OrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;

public class SwitchLab {
    public static void main(String[] args) {
        print(oldStyle(OrderStatus.PENDING));
        print(oldStyle(OrderStatus.PAID));
        print(oldStyle(OrderStatus.SHIPPED));
        print(oldStyle(OrderStatus.DELIVERED));
        print(oldStyle(OrderStatus.CANCELLED));

        /**
         * Estado	           ¿en qué case entra?                                        	¿dónde para (break o fin)?	    espero	                        real
         * PENDING          PEDING,PAID                                                         fin                     esperando pago preparando
         * PAID             PAID                                                                break                   preparando
         * SHIPPED          SHIPPED,DELIVERED,DEFAULT                                           fin                     en camino recibido sin acciones
         * DELIVERED        DELIVERED,DEFAULT                                                    fin                     recibido sin acciones
         * CANCELLED        DEFAULT                                                              fin                     sin acciones
         *
         * real
         * esperando pago; preparando;
         * preparando;
         * en camino; recibido; sin acciones;
         * recibido; sin acciones;
         * sin acciones;
         */
        print("------paso2----");
        print(describe(OrderStatus.PENDING));
        //espero: esperando pago
        print(describe(OrderStatus.PAID));
        // espero: preparando
        print(describe(OrderStatus.SHIPPED));
        // espero: en camino
        print(describe(OrderStatus.DELIVERED));
        // espero: recibido
        print(describe(OrderStatus.CANCELLED));
        //espero: sin acciones.

        print("-----Paso 3-----");

        print(deliveryDays("Cusco"));
        // espero: 3
        print(deliveryDays("lima"));
        // espero: 7
        print(deliveryDays("Lima"));
        // espero: 1
        print(deliveryDays("Tacna"));
        // espero: 7
        //'switch' expression does not cover all possible input values

        print("----Paso 4----");
        print(shippingCost("Lima", true));
        //espero: 15
        print(shippingCost("Arequipa", true));
        // espero: 40
        print(shippingCost("Arequipa", false));
        // espero: 20
        print(shippingCost("Tacna", true));
        // espero: 50

        print("------Paso 5------");

        var precio = new BigDecimal("9.90");
        var cantidad = 3;
        var mitad = 7 / 2;
        var nombres = new ArrayList<String>();
        var estado = OrderStatus.valueOf("SHIPPED");
        print(precio);
        // espero: 9.90 BigDecimal
        print(cantidad);
        // espero: 3 int
        print(mitad);
        // espero: 3 int
        print(nombres);
        // espero: ArrayList, List
        print(estado);
        // espero: SHIPPED, OrderStatus.


    }

    static int shippingCost(String city, boolean express) {
        return switch (city) {
            case "Lima" -> express ? 15 : 8;
            case "Cusco", "Arequipa" -> {
                if (express) {
                    yield 20 * 2;
                } else {
                    yield 20;
                }
            }
            default -> express ? 35 + 15 : 35;
        };
    }

    static int deliveryDays(String city) {

        return switch (city) {
            case "Lima" -> 1;
            case "Cusco", "Arequipa" -> 3;
            default -> 7;
        };
    }

    static String describe(OrderStatus s) {
        return switch (s) {
            case PENDING -> "esperando pago";
            case PAID -> "preparando";
            case SHIPPED -> "en camino";
            case DELIVERED -> "recibido";
            case CANCELLED -> "sin acciones";
        };
    }

    static String oldStyle(OrderStatus s) {
        String r = "";
        switch (s) {
            case PENDING:
                r += "esperando pago; ";
            case PAID:
                r += "preparando; ";
                break;
            case SHIPPED:
                r += "en camino; ";
            case DELIVERED:
                r += "recibido; ";
            default:
                r += "sin acciones; ";
        }
        return r;
    }

    private static void print(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }
}

/***
 * P4.1 "En el paso 1, PENDING imprimió esperando pago preparando porque en su case no había RBEACK y la ejecución continua hasta el break de paid ."
 * P4.2 "deliveryDays("lima") dio defial porque el case de un String casesensitive."
 * P4.3 "En un bloque { } de un switch expresión se usa yield y no return porque return saldria de la funcion."
 * P4.4 "var mitad = 7 / 2; es de tipo int y vale 3 porque deduce enteros.."
 *
 */