package Models;

import java.math.BigInteger;

public class Product {
    private BigInteger id;
    private String name;
    private BigInteger price;

    public Product(BigInteger id, String name, BigInteger price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getName(){
        return this.name;
    }

    public BigInteger getprice(){
        return this.price;
    }

    public boolean setPrice(BigInteger price){
        if(price.longValueExact()>0){
            this.price = price;
            return true;
        }
        return false;
    }

}
