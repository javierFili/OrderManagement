package practical.sets;

import Models.Product;
import Models.products.Laptop;
import Models.products.PhysicalProduct;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class EqualityTypeLab {
    public static  void main(String args[]){
        Product physical = new PhysicalProduct(new BigInteger("5"),"Monitor",new BigDecimal("300"),new BigDecimal("4"));
        Product laptop = new Laptop(new BigInteger("5"),"Monitor",new BigDecimal("300"),new BigDecimal("4"));
        System.out.println(physical.equals(laptop));
        // espero true ✅
        System.out.println(laptop.equals(physical));
        // espero true ✅
        Set<Product> setProducts = new HashSet<>();
        setProducts.add(physical);
        setProducts.add(laptop);
        System.out.println(setProducts.size());
        // espero: 1 ✅


    }
}

/**
 * Me quedare con el instanceof ya que es menos codigo y yo lo entiendo mejor.
 * 1: No, la version con instanceof es simetrica y la con getClass no lo es, ya que son objetos diferentes que heredan de un mismo padre... ahora mismo se rompio, ya que intanceof no hace una evaluacion estricta.
 * 2: no no puede repetirse los id,son unicos, si queremos hace una evalucion estricta de tipo se usa el getClass, pero yo prefiero usar el intanceof ya que si tenemos objectos heredados a nivel del backend y los queremos  comparar esa comparacion se romperia con getClass().
 */
/**
 * | Línea                        | instanceof | getClass |
 * |------------------------------|------------|----------|
 * | physical.equals(laptop)      |    true    |  false   |
 * | laptop.equals(physical)      |    true    |  false   |
 * | set.size()                   |     1      |  1       |
 */