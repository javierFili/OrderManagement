package practical.maps;

import Models.Product;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Main {
    public static void main (String args[]){
        printl(4);
        printl("JAvier123");
        printl("mostrar");
        printl(new Product(new BigInteger("1"),"laptop", new BigDecimal("342.4")));

    }
    public static void printl(Object ... o){
        for (Object item:o){
            System.out.println(item);
        }
    }
}
