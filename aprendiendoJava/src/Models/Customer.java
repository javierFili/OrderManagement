package Models;

import java.math.BigInteger;
import java.util.Objects;

public class Customer {
    private final BigInteger id;
    private String name;
    private String email;

    public Customer(BigInteger id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigInteger getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object cus) {
        if (cus instanceof Customer) {
            return this.id.equals(((Customer) cus).getId());
        }
        return false;
    }

}
