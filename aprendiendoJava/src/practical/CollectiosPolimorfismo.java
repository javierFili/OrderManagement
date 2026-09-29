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
import java.util.List;

public class CollectiosPolimorfismo {
    public static void main(String args[]){
        PhysicalProduct laptop = new Laptop(new BigInteger("324"),"Laptop",new BigDecimal("2534.4"),new BigDecimal("5345.3"));
        PhysicalProduct physicalBook = new PhysicalBook(new BigInteger("325"),"Physical Book",new BigDecimal("2734.4"),new BigDecimal("5345.3"));
        // muebles
        PhysicalProduct furniture = new Furnuture(new BigInteger("326"),"Furniture",new BigDecimal("2394.4"),new BigDecimal("5345.3"));
        List<Shippable> listProducts = new ArrayList<>();
        listProducts.add(laptop);
        listProducts.add(physicalBook);
        listProducts.add(furniture);

        for(Shippable product:listProducts){
            System.out.println(product.calculateShippingCost());
        }
        // el objeto esta aceptando las diferentes clases debido a que estamos usando clases que implementan esa interfaz, por lo tanto cumplen con el contrato
        // ademas heredan comportamiento de alguna manera, de esa forma esque se puede usar el polimorfismo.
    }
}
