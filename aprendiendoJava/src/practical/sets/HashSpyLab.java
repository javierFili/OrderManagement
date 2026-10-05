package practical.sets;

import Models.Product;
import Models.products.Laptop;
import Models.products.PhysicalProduct;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HashSpyLab {
    public static void main (String args[]){
        Product m1 = new Laptop(new BigInteger("7"),"Mouse",new BigDecimal("20"),new BigDecimal("1"));
        Product m2 = new Laptop(new BigInteger("7"),"Mouse",new BigDecimal("20"),new BigDecimal("1"));
        System.out.println(m1.hashCode());
        System.out.println(m2.hashCode());
        // espero: su hashcode diferente
        List<Product> products = new ArrayList<>();
        products.add(m1);
        System.out.println(products.contains(m2));
        // espero: si aparece el  >> equals llamado con Product{id=7, name='Mouse' , price= 20}

        HashSet<Product> hashProducts = new HashSet<>();
        hashProducts.add(m1);
        System.out.println(hashProducts.add(m2));
        // espero: true

    }
}


