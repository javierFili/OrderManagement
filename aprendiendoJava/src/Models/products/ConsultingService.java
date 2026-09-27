package Models.products;

import Interfaces.Consultable;
import Interfaces.Downloadable;
import Models.Product;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ConsultingService extends Product implements Consultable, Downloadable {
    public ConsultingService(BigInteger id, String name, BigDecimal price){
        super(id,name,price);

    }

    @Override
    public BigDecimal consultableProduct(){
        return super.getprice();
    }

    @Override
    public String download(){
        return "se descarga el documento de la consulta que se haga";
    }
}
