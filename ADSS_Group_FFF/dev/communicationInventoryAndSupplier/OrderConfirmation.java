package communicationInventoryAndSupplier;

public class OrderConfirmation {
    private final boolean success;

    public OrderConfirmation(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}

