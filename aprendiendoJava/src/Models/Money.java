package Models;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount) {
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money add(Money other) {
        return new Money(amount.add(other.amount()));
    }

    public Money multiply(int quantity) {
        return new Money(amount.multiply(new BigDecimal(quantity).setScale(2, RoundingMode.HALF_UP)));
    }

    public boolean isGreaterThan(Money other) {
        return amount.compareTo(other.amount()) > 0;
    }

    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("amount cannot be null");
        }
        if (amount.signum() == -1) {
            throw new IllegalArgumentException("amount cannot be negative: " + amount);
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }
    // void setAmount(BigDecimal a) { this.amount = a; }
}
