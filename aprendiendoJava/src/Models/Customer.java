package Models;

import java.math.BigInteger;

public class Customer {
    private BigInteger id;
    private String name;
    private String email;

    public Customer(BigInteger id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public boolean createClient() {
        return true;
    }

    public Customer getClient() {
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
