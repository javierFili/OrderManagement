package practical.lists;

import java.util.ArrayList;
import java.util.List;

public class ListBasics {
    public static void main(String args[]) {
        List<String> names = new ArrayList<>();
        names.add("ana");
        names.add("luis");
        names.add("Marta");
        names.add("Pedro");
        names.add("ana");
        System.out.println(names);
        System.out.println("------------");
        System.out.println(names.get(0));
        System.out.println(names.get(names.size() - 1));

        System.out.println("------------");
        String newName = names.set(0,"sofia");
        System.out.println(newName);

        System.out.println("------------");
        names.add(1, "Carlos");
        System.out.println(names);
        System.out.println("------------");
        names.remove("ana");
        System.out.println(names);///solo borra a la primera coencidencia.

        System.out.println("------------");
        System.out.println(names.remove(1));
        try {
            names.get(names.size());
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }
        names.clear();
        System.out.println(names.isEmpty());

    }
}
/***
 * 1: por que nos estamos pasando el indice, el ultiumo elemento de array es el size()-1 ya que se inicia a contar desde el 0
 * 2: el add agrega un nuevo elemento en la posicion 2 la cual desplaza a los demas, pero el set reemplaza el objeto que esta en dicha posicion.
 * 3: solo se elimina una, ya que el proceso de eliminacion termina cuando se tiene la primer coencidencia.
 */