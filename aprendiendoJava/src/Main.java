import Interfaces.ShippingStragy;
import Models.*;
import Models.products.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Customer customer = new Customer(new BigInteger("1"), "John Doe", "javierfiligrana@gmail.com");
        Product laptop = new PhysicalProduct(new BigInteger("1"), "Laptop", new BigDecimal("1000"), new BigDecimal("43"));
        Product mouse = new PhysicalProduct(new BigInteger("2"), "Mouse", new BigDecimal("50"), new BigDecimal("54"));
        Product keyboard = new PhysicalProduct(new BigInteger("3"), "Keyboard", new BigDecimal("80"),new BigDecimal("62"));

        Product courseJava = new DigitalProduct(new BigInteger("4"), "Java Course", new BigDecimal("100"), new BigDecimal("100"));
        Product coursePython = new DigitalProduct(new BigInteger("5"), "Python Course", new BigDecimal("100"), new BigDecimal("100"));

        Product netflixSubscription = new RenewableProduct(new BigInteger("6"), "netflixSubscription", new BigDecimal("100"), 3);

        Product consultingService = new ConsultingService(new BigInteger("7"), "Consultoria de casas", new BigDecimal("123"));

        Order order = new Order(new BigInteger("1"), customer);

        order.addItem(laptop, 3);
        order.addItem(courseJava, 4);
        order.addItem(netflixSubscription, 2);
        order.addItem(consultingService, 2);
        order.addItem(keyboard,3);
        order.addItem(coursePython,1);

        System.out.println(order.getTotalPrice());

        for (OrderItem item : order.getItems()) {
            String print = item.getProduct().getName() + " " + "x" + item.getQuantity() + " " + item.getProduct().getPrice() + " " + item.getSubTotalPrice();
            System.out.println(print);
        }
        System.out.println("Total: " + order.getTotalPrice());

        try {
            OrderItem ordI = new OrderItem(mouse, 3);
            order.getItems().add(ordI);
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName());
        }

        try {
            order.getItems();
            order.removeItem(99);
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName());
        }
        /**
         * tarea 6
         */
        System.out.println("---------------Tarea 6");

        order.addItem(laptop,1);
        Product laptop2 = new PhysicalProduct(new BigInteger("1"),"laptop2",new BigDecimal("32.4"),new BigDecimal("3.4"));
        order.addItem(laptop2,1);

        System.out.println(order.getItemCount());
        // espero: 8 check

        System.out.println(order.getDistinctProduct().size());
        // espero: 7
        // real: 6 --> existen dos productos repetidos, la laptop y el mouse que tienen el mismo id, es decir el id=1 comprendes? que mas quieres que te explique?? tiene el mismo id y punto, ese es nuestro criterio de evaluacion para sabe si son iguales o no.

        Set<Product> distincProduct = order.getDistinctProduct();
        System.out.println(distincProduct);
        // espero: orden de insercion 1,2,3,4,5,6,7 obvio en la forma del toString.
        // real2: [Product{id=1, name='Laptop' , price= 1000}, Product{id=4, name='Java Course' , price= 100}, Product{id=6, name='netflixSubscription' , price= 100}, Product{id=7, name='Consultoria de casas' , price= 123}, Product{id=3, name='Keyboard' , price= 80}, Product{id=5, name='Python Course' , price= 100}]
        // real3: [Product{id=1, name='Laptop' , price= 1000}, Product{id=4, name='Java Course' , price= 100}, Product{id=6, name='netflixSubscription' , price= 100}, Product{id=7, name='Consultoria de casas' , price= 123}, Product{id=3, name='Keyboard' , price= 80}, Product{id=5, name='Python Course' , price= 100}]
        try {
            Product mouse1 = new Laptop(new BigInteger("54"),"Mouse",new BigDecimal("2.4"),new BigDecimal("4.3"));
            distincProduct.add(mouse1);
            // espero: una unsupportedOperationException e
            System.out.println("pasa??");
        }catch (UnsupportedOperationException e){
            System.out.println(order.getDistinctProduct());
            System.out.println(e);
            //espero que entre aqui
        }
        System.out.println(order.getTotalPrice());
        //espero: 5218.4

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

/**
 * Tarea 6:
 *  1: por que queremos que se muestren productos unicos, sin repetir, la mejor forma es usar el Set
 *  2: no se habria eliminado si no se sobre escribia el todo, por que no existiria un campo valido de identificacion de hashCode, estaria tomando el por defecto que siempre es diferente entre la instancia de un objeto y otro
 *  3: Se pierde el orden cuando se usa el copyof
 */