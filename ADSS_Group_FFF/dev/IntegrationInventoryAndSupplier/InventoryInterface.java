package IntegrationInventoryAndSupplier;

import inventory.serviceLayer.*;

public interface InventoryInterface {


    InventoryService getInstance();


    // with this method, supplierModule will update InventoryModule that a delivery arrived
    // (whether its scheduled or due to lack in stock)

    boolean acceptDelivery(int itemId, int quantity);

}
