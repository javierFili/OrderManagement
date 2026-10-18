package practical.maps;

import Models.Order;
import Models.OrderItem;
import Models.Product;
import Models.products.Laptop;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

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
        // espero: {pendrive=3, mouse=3, webcam=1, notebook=1}✅
        printl("----T3----");
        Map<String, Integer> cuenta2 = new LinkedHashMap<>();
        for (String item : vendidos) {
            cuenta2.put(item, cuenta2.getOrDefault(item, 0) + 1);
        }
        printl(cuenta2);
        // espero:{pendrive=3, mouse=3, webcam=1, notebook=1}✅
        printl("-----t4----");

        Map<String, Integer> cuenta3 = new HashMap<>();
        for (String item : vendidos) {
            cuenta3.merge(item, 1, Integer::sum);
        }
        printl(cuenta3);
        // espero: {pendrive=3, mouse=3, webcam=1, notebook=1}, pero con el orden diferente.✅
        printl("----T5");
        printl(cuenta1.equals(cuenta2));
        // espero: true✅
        printl(cuenta1.equals(cuenta3));
        // espero: false
        //real: true, por que yo me confundi, pense que por que le hashmap va desordenado el equals me hubiera dicho false, pero resulta que no fue asi.
        Product pendrive = new Laptop(new BigInteger("20"), "pendrive", new BigDecimal("43.2"), new BigDecimal("5.2"));
        Product mouse = new Laptop(new BigInteger("21"), "mouse", new BigDecimal("2.2"), new BigDecimal("2.2"));
        Product webcam = new Laptop(new BigInteger("22"), "webcam", new BigDecimal("4.2"), new BigDecimal("4.2"));
        Product notebook = new Laptop(new BigInteger("1"), "notebook", new BigDecimal("3.2"), new BigDecimal("5.2"));
        List<OrderItem> lineas = new ArrayList<>();
        OrderItem order = new OrderItem(pendrive, 2);//86.4
        OrderItem order1 = new OrderItem(mouse, 1);//2.2
        OrderItem order0 = new OrderItem(pendrive,3);//129.6
        OrderItem order2 = new OrderItem(webcam, 1);//4.2
        OrderItem order3 = new OrderItem(mouse, 4);//8.8
        //101.4
        lineas.add(order);
        lineas.add(order1);
        lineas.add(order2);
        lineas.add(order3);

        Map<String, Integer> unidades = new LinkedHashMap<>();
        for (OrderItem item : lineas) {
            if (!unidades.containsKey(item.getProduct().getName())) {
                unidades.put(item.getProduct().getName(), item.getQuantity());
            } else {
                unidades.put(item.getProduct().getName(), unidades.get(item.getProduct().getName()) + item.getQuantity());
            }
        }
        printl(unidades);
        //espero: {pendrive=2,mouse=5,webcam=1} ✅
        Map<String, Integer> unidades2 = new LinkedHashMap<>();
        for (OrderItem item : lineas) {
            String itemName = item.getProduct().getName();
            unidades2.merge(itemName, item.getQuantity(), Integer::sum);
        }
        printl(unidades2);
        // //espero: {pendrive=2,mouse=5,webcam=1} ✅

        printl("------T5-----");
        Map<String, BigDecimal> subtotales = new LinkedHashMap<>();
        for (OrderItem item : lineas) {
            subtotales.merge(item.getProduct().getName(),item.getSubTotalPrice(),BigDecimal::add);
        }
        printl(subtotales);
        // //espero: {pendrive=86.2,mouse=11,webcam=4.2} ✅

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

/***
 * 1: LA receta de acumulart es : si la clave no esta la agregamos con un valor de 1; si esta sumamanos, En la parte A el avlor inicial es de null y en la Parte B .. nose a que te refieres!!!
 * 2: cuenta1.equals(cuenta3) dio true aunque imprimen un distinto orden, por que Map.equals compara clave valor
 * 3: En la parte B, si en Order uso la clave Product en vez del nombre(String), para que dos objetos con el mismo id cuenten como el mismi producto, Product necsita sobre escribir el metodo equals y hascode.
 *
 */