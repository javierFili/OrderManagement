package Interfaces;

import Models.Product;

import java.math.BigDecimal;
import java.util.Date;

public interface Consultable {
    public BigDecimal calculateConsultancyCost(Date start, Date end);
    public String reviewConsultancy();
    public Date dateStart();
    public Date dateEnd();
    public String getConsultantDetails();
}
