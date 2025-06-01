package IntegrationInventoryAndSupplier;

import inventory.presentationLayer.InventoryCLI;
import inventory.serviceLayer.InventoryService;

public class Main {
    public static void main(String[] args) {
        // will hold 2 singletons - inventoryService and SupplierService supplierService

        // if block for choosing if you wanna log in as a Supplier or Inventory

        // will call appropriate CLI

        // for example, calls SupplierCLI
        // supplierService.getInstance().CLI.run();

        System.out.println("testing main");
        InventoryService inventoryService = InventoryService.getInstance();
        InventoryCLI inventoryCLI = new InventoryCLI(inventoryService);
        inventoryCLI.run();




    }
}