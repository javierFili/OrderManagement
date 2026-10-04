package practical;

import Interfaces.Shippable;
import Models.Product;
import Models.products.Furnuture;
import Models.products.Laptop;
import Models.products.PhysicalBook;
import Models.products.PhysicalProduct;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectiosPolimorfismo {
    public static void main(String args[]){
        Set<String> tags = new HashSet<>();
        System.out.println(tags.add("java"));      // true  → lo agregó
        System.out.println(tags.add("spring"));    // true
        System.out.println(tags.add("java"));      // false → ya estaba, no hizo nada
        System.out.println(tags.size());           // 2
        System.out.println(tags.contains("JAVA")); // false → mayúsculas distintas
        System.out.println(tags.remove("php"));    // false → no estaba
        System.out.println(tags);
    }
}
