import Interfaces.ShippingStragy;
import Models.*;
import Models.products.ConsultingService;
import Models.products.DigitalProduct;
import Models.products.PhysicalProduct;
import Models.products.RenewableProduct;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        Customer customer = new Customer(new BigInteger("1"), "John Doe", "javierfiligrana@gmail.com");
        Product laptop = new PhysicalProduct(new BigInteger("1"), "Laptop", new BigDecimal("1000"),new BigDecimal("43"));
//        Product mouse = new PhysicalProduct(new BigInteger("2"), "Mouse", new BigDecimal("50"),new BigDecimal("54"));
//        Product keyboard = new PhysicalProduct(new BigInteger("3"), "Keyboard", new BigDecimal("80"),new BigDecimal("62"));

        Product courseJava = new DigitalProduct(new BigInteger("4"), "Java Course", new BigDecimal("100"), new BigDecimal("100"));
        //Product coursePython = new DigitalProduct(new BigInteger("5"), "Python Course", new BigDecimal("100"), new BigDecimal("100"));

        Product netflixSubscription = new RenewableProduct(new BigInteger("6"),"netflixSubscription",new BigDecimal("100"),3);

        Product consultingService = new ConsultingService(new BigInteger("7"),"Consultoria de casas",new BigDecimal("123"));

        Order order = new Order(new BigInteger("1"),customer);

        order.addItem(laptop,3);
        order.addItem(courseJava,4);
        order.addItem(netflixSubscription,2);
        order.addItem(consultingService,2);

        System.out.println(order.getTotalPrice());

        for (OrderItem item:order.getItems()){
            String print = item.getProduct().getName()+" "+"x"+item.getQuantity()+" "+item.getProduct().getPrice()+" "+item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity()+""));
            System.out.println(print);
        }

        try {
            order.getItems();
            order.removeItem(99);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        /**
         * 1: Por que se estaria exponiendo la lista, la lista que se devuelve en un get debe inmutable, es dedir que el que la reciba no deberia poder modificarla
         * 2: impide hacer modificaciones una vez que el atriburto fue modificado, si deja usarlo obviamente, pero no modificarlo como tal
         * 3: el count cunta la cantidad de items, mientra el otro cuenta las unidades
         */
    }
}

/**
 * 1: Se convierte en interface ya que ahora es como un contrato que todas las clases que la implementan deben de cumplir, es decir ya no es como la herencia que hereda su comportamiento, si no que el debe cumplir el "contrato", es decir pueden no guardar ningun tipo de realacion entre si entre la clase interface y la clase que lo implementa, simplemente cumple con el contrato
 * 2: Se lo estoy pasando mediante el constructor, pero lo que me choca es que ahora la clase main sera quien deba declarar, instancia, delegar las clases y luego al momento de instanciar el objeto product es cuando se lo envia
 * 3: Lo esta creando la clase main, no se si eso sea correcto, ademas no pude solucionar el como evitar que se envie o no el weight en esa funcion de la interface, estoy pensando que no se necesita interface..
 * 4: Si se agreaga FreeShipping, etc.. Cada uno tendria el contrato de la interface es decir implementa el metodo como lo necesite implementar(sigo con la duda de que pasa si tiene paramatros diferentes!!), y con eso ya no tocamos product sino que creamos esas clases que implementen a ShippingStrategy y al momento de usarse distinguir una de la otra..
 */