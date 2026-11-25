package Models;

public enum OrderStatus {
    PENDING("Pendiente"), PAID("Pagado"), SHIPPED("Enviado"), DELIVERED("Entregado"), CANCELLED("Cancelado");
    /// PENDING, PAID, SHIPPED, DELIVERED, CANCELLED;
    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case PENDING -> PAID == next || CANCELLED == next;
            case PAID -> next == CANCELLED || next == SHIPPED;
            case SHIPPED -> next == DELIVERED;
            case CANCELLED, DELIVERED -> false;
        };
    }

    public boolean isFinal() {
        return switch (this) {
            case CANCELLED, DELIVERED -> true;
            case PENDING, PAID, SHIPPED -> false;
        };
    }
}