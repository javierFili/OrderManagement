package practical.lists;

import java.util.ArrayList;
import java.util.List;

public class ListBasics {
    public static void main(String args[]) {
        System.out.println("1------------");
        List<String> names = new ArrayList<>();

        System.out.println("2------------");
        System.out.println(names.isEmpty());//espero: true
        System.out.println(names.size());//espero: 0

        System.out.println("3------------");
        names.add("ana");
        names.add("luis");
        names.add("Marta");
        names.add("Pedro");
        names.add("ana");

        System.out.println("4----------------");
        System.out.println(names);// espero: ["ana","luis","Marta","Pedro","ana"] // salio: [ana,luis,Marta,Pedro,ana]

        System.out.println("5------------");
        System.out.println(names.get(0));// espero: "ana" real: ana
        System.out.println(names.get(names.size() - 1));//espero: "ana" real: ana

        System.out.println("6------------");
        names.add(2, "carlos");
        System.out.println(names);// espero: ["ana","luis","carlos","Marta","Pedro","ana"] real: [ana,luis,carlos,Marta,Pedro,ana]

        System.out.println("7-------------");
        System.out.println(names.set(0, "sofia"));//espero: sofia
        System.out.println(names);// espero: ["sofia","ana","luis","carlos","Marta","Pedro","ana"] real: [sofia,luis,carlos,Marta,Pedro,ana]

        System.out.println("8------------");
        System.out.println(names.contains("ana"));// espero: true
        System.out.println(names.indexOf("ana"));// espero: 1 real:5

        System.out.println("9-------------");
        System.out.println(names.remove("ana")); //true
        System.out.println(names);// espero: ["sofia","luis","carlos","Marta","Pedro","ana"]
        System.out.println("10-----------------");
        System.out.println(names.remove(1)); // espero: luis
        System.out.println(names);// espero: [sofia,carlos,Marta,Pedro,ana]

        System.out.println("11-------------------");
        try {
            names.get(names.size());
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage()); //real: index 4 out bounds for length 4
            //espero: un desvorde de o algo asi, se que es un error.
        }

        System.out.println("12---------------");
        names.clear();
        System.out.println(names.isEmpty());// espero: true
    }
}
/***
 * 1: por que nos estamos pasando el indice, el ultiumo elemento de array es el size()-1 ya que se inicia a contar desde el 0
 * 2: el add agrega un nuevo elemento en la posicion 2 la cual desplaza a los demas, pero el set reemplaza el objeto que esta en dicha posicion.
 * 3: solo se elimina una, ya que el proceso de eliminacion termina cuando se tiene la primer coencidencia, puede que sea la primera
 */