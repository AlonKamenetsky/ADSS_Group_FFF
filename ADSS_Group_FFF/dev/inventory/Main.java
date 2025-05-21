package inventory;

import communicationInventoryAndSupplier.FakeSupplierService;

public class Main {
    public static void main(String[] args) {
        InventoryController controller = new InventoryController();
        controller.setSupplierService(new FakeSupplierService());

        InventoryCLI cli = new InventoryCLI(controller);
        cli.run();
    }
}
