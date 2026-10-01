package practical.lists;

import Models.Product;
import Models.products.*;

import java.lang.ref.PhantomReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ContainsLab {
    public static void main (String args[]){
        System.out.println("1---------------");
        List<Product> list = new ArrayList<>();
        Product firstLaptop = new Laptop(new BigInteger("1"), "Laptop", new BigDecimal("3452.4"), new BigDecimal("32"));
        Product secondLaptop = new Laptop(new BigInteger("1"), "Laptop", new BigDecimal("3452.4"), new BigDecimal("32"));
        list.add(firstLaptop);
        list.add(secondLaptop);
        System.out.println(list.contains(secondLaptop));
        System.out.println(list.indexOf(secondLaptop));
        System.out.println(list.contains(firstLaptop));
        // espero true,true
    }
}
/**
 * 1: quizas tenga algo que ver el contais, ya que son objetos "complejos" asi que no se que criterio este tomando para esto.
 * 2: yo pienso que el contains lo esta usando el Object
 * 3: en realidad slo se agrega, no tenemos forma de verificar duplicados.
 */