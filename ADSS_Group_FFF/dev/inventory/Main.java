package inventory;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        InventoryService service = new InventoryService();

        // Create category
        Category dairy = new Category("Dairy", null);
        Category milk = new Category("Milk", dairy);
        service.addCategory(dairy);
        dairy.addSubCategory(milk);

        // Create item
        InventoryItem item = new InventoryItem(
                "001", "Tnuva 1L Milk", "Tnuva",
                3, 1, 10, 2.50, 4.90, ItemStatus.NORMAL, milk
        );
        service.addItem(item);

        // Create UI and use it
        InventoryManagerUI ui = new InventoryManagerUI(service);
        ui.printAllItems();
        ui.printLowStockItems();

        // Generate and print a report
        InventoryReport report = service.generateReport("RPT001", Arrays.asList(milk));
        System.out.println(report);
    }
}
