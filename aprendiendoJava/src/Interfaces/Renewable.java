package Interfaces;

import Models.Product;
import objects.Renewal;
import objects.RenewalStatus;

import java.math.BigDecimal;

public interface Renewable {
    Renewal renew();

    RenewalStatus getRenewalStatus();
}
