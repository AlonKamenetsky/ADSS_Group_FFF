package inventory;

import inventory.presentationLayer.InventoryCLI;
import inventory.serviceLayer.InventoryService;

public class testMain {
    public static void main(String[] args) {
        System.out.println("testing main");
        InventoryService inventoryService = InventoryService.getInstance();
        InventoryCLI inventoryCLI = new InventoryCLI(inventoryService);
        inventoryCLI.run();
    }
}
