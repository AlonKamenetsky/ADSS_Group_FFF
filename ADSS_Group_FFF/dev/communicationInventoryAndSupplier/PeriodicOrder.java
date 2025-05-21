package communicationInventoryAndSupplier;

import java.time.DayOfWeek;

public class PeriodicOrder extends SupplierOrder {
    private static int nextOrderId = 1;  // shared across all instances

    private final int orderID;
    private final DayOfWeek orderDay;

    public PeriodicOrder(String productId, int quantity, DayOfWeek orderDay) {
        super(null, productId, quantity);
        this.orderID = nextOrderId++;
        this.orderDay = orderDay;
    }

    public int getOrderID() {
        return orderID;
    }

    public DayOfWeek getOrderDay() {
        return orderDay;
    }

    private setSupplier(int supplierId)
    {

    }

    @Override
    public String toString() {
        return "PeriodicOrder{" +
                "orderID=" + orderID +
                ", supplierId='" + getSupplierId() + '\'' +
                ", productId='" + getProductId() + '\'' +
                ", quantity=" + getQuantity() +
                ", orderDay=" + orderDay +
                '}';
    }
}