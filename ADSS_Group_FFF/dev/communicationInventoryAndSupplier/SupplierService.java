package communicationInventoryAndSupplier;

import java.util.List;

public interface SupplierService {
    public void takePeriodicOrder(PeriodicOrder p1);
    public void placeOrderSingleProduct(MutualOrder m);
}
