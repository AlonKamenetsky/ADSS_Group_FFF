package inventory;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        InventoryService service = new InventoryService();

        // Load sample data into memory (outside domain layer)
        AppInitializer.loadSampleData(service);

        // Basic usage demo
        InventoryManagerUI ui = new InventoryManagerUI(service);

        ui.printAllItems();
        ui.printLowStockItems();
        ui.printCategories();

        // Generate a report for a specific category and status
        InventoryReport report = service.generateReport(
                "RPT001",
                Arrays.asList(service.getAllCategories().get(0)), // filter by "Dairy"
                null // no status filter
        );
        System.out.println(report);
    }
}
