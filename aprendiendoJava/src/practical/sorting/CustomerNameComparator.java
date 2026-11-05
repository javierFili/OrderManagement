package practical.sorting;

import Models.Customer;

import java.util.Comparator;

public class CustomerNameComparator implements Comparator<Customer> {

    @Override
    public int compare(Customer cus,Customer customer){
        return cus.getName().compareToIgnoreCase(customer.getName());
    }
}
