package practical.modern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImmutabilityLab {
    record ShoppingList(String owner, List<String> items) {
    }

    record SafeShoppingList(String owner, List<String> items) {
        SafeShoppingList {
            items = List.copyOf(items);
        }
    }

    public static void main(String[] args) {
        List<String> items = new ArrayList<>(List.of("pan", "leche"));
        ShoppingList s = new ShoppingList("Lucía", items);
        print(s);
        items.add("huevos");
        print(s);
        s.items().add("cafe");
        print(s);

        /**
         * #	Acción	                    espero (lo que imprime s)	            real
         * a	(ninguna, recién creado)     ShoppingList[Lucia,items{pan,leche}] | ShoppingList[owner=Lucía, items=[pan, leche]]
         * b	items.add("huevos");         ShoppingList[Lucia,items{pan,leche,huevos}] | ShoppingList[owner=Lucía, items=[pan, leche, huevos]]
         * c	s.items().add("café");       ShoppingList[Lucia,items{pan,leche,huevos,cafe}] |  ShoppingList[owner=Lucía, items=[pan, leche, huevos]]
         */
        print("---------paso3-------");

        List<String> items2 = new ArrayList<>(List.of("pan", "leche"));
        SafeShoppingList s2 = new SafeShoppingList("Lucía", items2);
        print(s2);
        try {
            items2.add("huevos");
            print(s2);

        } catch (UnsupportedOperationException e) {
            print(e);
        }
        try {
            s2.items().add("cafe");
            print(s2);
        } catch (UnsupportedOperationException e) {
            print(e);
        }

        /**
         * #	Acción	                    espero (lo que imprime s)	            real
         * a	(ninguna, recién creado)     ShoppingList[Lucia,items{pan,leche}] | ShoppingList[owner=Lucía, items=[pan, leche]]
         * b	items.add("huevos");         ShoppingList[Lucia,items{pan,leche] |  ShoppingList[Lucia,items{pan,leche]
         * c	s.items().add("café");       UnsupportedUoerationException       | UnsupportedUoerationException
         */

        print("------Paso 4.---------");
        /**
         * #	Acción	                    espero (lo que imprime s)	            real
         * a	(ninguna, recién creado)     ShoppingList[Lucia,items{pan,leche}] | ShoppingList[owner=Lucía, items=[pan, leche]]
         * b	items.add("huevos");         ShoppingList[Lucia,items{pan,leche,huevos] |  ShoppingList[Lucia,items{pan,leche]
         * c	s.items().add("café");       UnsupportedUoerationException       | UnsupportedUoerationException
         */

        print("-------Paso 5------");

        /**
         * Paso 5. En un comentario, llena esta tabla para tu proyecto. En la última columna usa la pregunta de la sección 3.7: "si cambio un dato, ¿sigue siendo el mismo?".
         *
         * Tipo	                                                ¿Tiene id?      	Si cambia un dato, ¿sigue siendo el mismo?	        ¿Entidad o valor?	        ¿record o clase?
         * Product                                                si                 si                                                     entidad                     clase
         * Customer                                               si                 si                                                     entidad                     clase
         * Order                                                  si                 si                                                     entidad                     clase
         * Money                                                  no                 si                                                     valor                       record
         * una dirección de envío (calle, ciudad, código postal)  no                 no                                                     valor                       record
         * OrderItem (producto + cantidad)                        si                 si                                                     valor                       clase
         * un resumen de pedido para mostrar en pantalla          no                 no                                                     valor                       record
         */

    }

    private static void print(Object... o) {
        for (Object item : o) {
            System.out.println(item);
        }
    }
}


/**
 * Preguntas:
 *
 * P2.1 "ShoppingList cambió aunque sus campos son final, porque final impide el cambio de valor luedo de hacer inicializada pero no con objetos. El record guardó referencia de la  lista que yo le pasé, no una copia que no se pueda modificar."
 * P2.2 "Con List.copyOf, la acción (b) solo ocurre para la lista item2 porque el record tiene una copia que nose modifica; la acción (c) va en error porque la copia es inmodificable."
 * P2.3 "Con Collections.unmodifiableList, la acción (b) la aggrego, porque una vista de la lista."
 * P2.4 "Customer es entidad porque puede viene la da bd y tiene valores impotante o relevalantes. Una dirección de envío es un record porque puede tener id, no nesitar la complejidad que tiene un objetos.."
 */
