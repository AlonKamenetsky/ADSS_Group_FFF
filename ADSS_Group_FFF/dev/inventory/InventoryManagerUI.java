package inventory;

public class InventoryManagerUI {
    private InventoryService service;

    public InventoryManagerUI(InventoryService service) {
        this.service = service;
    }

    public void printAllItems() {
        System.out.println("=== All Inventory Items ===");
        for (InventoryItem item : service.getAllItems()) {
            System.out.println(item);
        }
    }

    public void printLowStockItems() {
        System.out.println("=== Low Stock Items ===");
        for (InventoryItem item : service.getLowStockItems()) {
            System.out.println(item);
        }
    }

    public void printCategories() {
        System.out.println("=== Categories ===");
        for (Category cat : service.getAllCategories()) {
            System.out.println(cat);
        }
    }
}
