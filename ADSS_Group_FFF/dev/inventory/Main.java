package inventory;

import IntegrationInventoryAndSupplier.FakeSupplierService;

public class Main {
    public static void main(String[] args) {
        InventoryService controller = new InventoryService();
        controller.setSupplierService(new FakeSupplierService());

        InventoryCLI cli = new InventoryCLI(controller);
        cli.run();
    }
}
