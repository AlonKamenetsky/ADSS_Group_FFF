package inventory;

import communicationInventoryAndSupplier.PeriodicOrder;

import java.time.DayOfWeek;
import java.util.*;

public class InventoryCLI {
    private final InventoryController controller;
    private final Scanner scanner = new Scanner(System.in);

    public InventoryCLI(InventoryController controller) {
        this.controller = controller;
    }

    public void run() {
        System.out.println("=== Welcome to Inventory CLI ===");

        // One-time sample data load prompt
        System.out.print("Load sample data? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            AppInitializer.loadSampleData(controller);
            System.out.println("Sample data loaded.");
        }

        boolean exit = false;
        while (!exit) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> listAllItems();
                case "2" -> listLowStockItems();
                case "3" -> listCategories();
                case "4" -> updateQuantities();
                case "5" -> markItemAsDamaged();
                case "6" -> generateReport();
                case "7" -> addNewItem();
                case "8" -> addNewCategory();
                case "9" -> exit = true;
                case "10" -> advanceDay();
                case "11" -> addPeriodicOrder();
                case "12" -> removePeriodicOrder();

                default -> System.out.println("Invalid option.");
            }
        }

        System.out.println("Goodbye!");
    }

    private void advanceDay() {
        controller.advanceDay();
    }

    private void printMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1. View Inventory");
        System.out.println("2. View Low Stock");
        System.out.println("3. View Categories");
        System.out.println("4. Update Quantities");
        System.out.println("5. Mark Damaged");
        System.out.println("6. Generate Report");
        System.out.println("7. Add New Item");
        System.out.println("8. Add New Category");
        System.out.println("9. Exit");
        System.out.println("10. Advance to Tomorrow");
        System.out.println("11.Add Periodic Order");
        System.out.println("12. Remove Periodic Order");


        System.out.print("Choice: ");
    }

    private void listAllItems() {
        for (InventoryItem item : controller.getAllItems()) {
            System.out.println(item);
        }
    }

    private void listLowStockItems() {
        for (InventoryItem item : controller.getLowStockItems()) {
            System.out.println(item);
        }
    }

    private void listCategories() {
        for (Category cat : controller.getAllCategories()) {
            System.out.println(cat);
        }
    }

    private void updateQuantities() {
        System.out.print("Enter item ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Shelf quantity delta (e.g. +3 or -2): ");
        int shelf = Integer.parseInt(scanner.nextLine());
        System.out.print("Backroom quantity delta: ");
        int backroom = Integer.parseInt(scanner.nextLine());
        controller.updateItemQuantity(id, shelf, backroom);
        System.out.println("Quantities updated.");
    }

    private void markItemAsDamaged() {
        System.out.print("Enter item ID to mark as damaged: ");
        String id = scanner.nextLine().trim();
        InventoryItem item = getItemById(id);
        if (item != null) {
            item.setStatus(ItemStatus.DAMAGED);
            System.out.println("Item marked as damaged.");
        } else {
            System.out.println("Item not found.");
        }
    }

    private void generateReport() {
        System.out.print("Enter report ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Filter by status? (y/n): ");
        ItemStatus status = null;
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.print("Enter status (OK, DAMAGED, EXPIRED): ");
            status = ItemStatus.valueOf(scanner.nextLine().trim().toUpperCase());
        }
        InventoryReport report = controller.generateReport(id, null, status);
        System.out.println(report.toString());
    }

    private void addNewItem() {
        System.out.print("Item ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Category name: ");
        String categoryName = scanner.nextLine().trim();
        Category category = getCategoryByName(categoryName);
        if (category == null) {
            System.out.println("Category does not exist.");
            return;
        }

        System.out.print("Manufacturer: ");
        String manufacturer = scanner.nextLine();
        System.out.print("Purchase price: ");
        double purchasePrice = Double.parseDouble(scanner.nextLine());
        System.out.print("Sale price: ");
        double salePrice = Double.parseDouble(scanner.nextLine());
        System.out.print("Shelf qty: ");
        int shelf = Integer.parseInt(scanner.nextLine());
        System.out.print("Backroom qty: ");
        int backroom = Integer.parseInt(scanner.nextLine());
        System.out.print("Min threshold: ");
        int min = Integer.parseInt(scanner.nextLine());

        InventoryItem item = new InventoryItem(
                id, name, manufacturer, shelf, backroom, min,
                purchasePrice, salePrice, ItemStatus.NORMAL, category
        );

        controller.addItem(item);
        System.out.println("Item added.");
    }

    private void addNewCategory() {
        System.out.print("Category name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter parent category name (or leave blank): ");
        String parentName = scanner.nextLine().trim();
        Category parent = getCategoryByName(parentName);
        if (parentName.isEmpty()) {
            parent = null;
        }
        controller.addCategory(new Category(name, parent));
        System.out.println("Category added.");
    }

    // Utility lookup methods
    private InventoryItem getItemById(String id) {
        for (InventoryItem item : controller.getAllItems()) {
            if (item.getId().equals(id)) return item;
        }
        return null;
    }

    private Category getCategoryByName(String name) {
        for (Category cat : controller.getAllCategories()) {
            if (cat.getName().equals(name)) return cat;
        }
        return null;
    }




    private void addPeriodicOrder() {
        System.out.print("Enter product ID: ");
        String productId = scanner.nextLine().trim();

        System.out.print("Enter quantity: ");
        int quantity;
        try {
            quantity = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity.");
            return;
        }

        System.out.print("Enter delivery day (e.g. mon, tue, wed): ");
        String dayInput = scanner.nextLine().trim().toLowerCase();
        DayOfWeek dayOfWeek = parseDayOfWeek(dayInput);

        if (dayOfWeek == null) {
            System.out.println("Invalid day of week.");
            return;
        }

        try {
            controller.addPeriodicOrder(productId, quantity, dayOfWeek);
            System.out.println("✅ Periodic order added for " + dayOfWeek);
        } catch (Exception e) {
            System.out.println("❌ Failed to add periodic order: " + e.getMessage());
        }
    }

    private DayOfWeek parseDayOfWeek(String input) {
        return switch (input) {
            case "sun" -> DayOfWeek.SUNDAY;
            case "mon" -> DayOfWeek.MONDAY;
            case "tue" -> DayOfWeek.TUESDAY;
            case "wed" -> DayOfWeek.WEDNESDAY;
            case "thu" -> DayOfWeek.THURSDAY;
            case "fri" -> DayOfWeek.FRIDAY;
            case "sat" -> DayOfWeek.SATURDAY;
            default -> null;
        };
    }

    private void removePeriodicOrder() {
        List<PeriodicOrder> periodicOrders = controller.getAllPeriodicOrders();

        if (periodicOrders.isEmpty()) {
            System.out.println("No periodic orders to remove.");
            return;
        }

        System.out.println("📦 Current Periodic Orders:");
        for (PeriodicOrder order : periodicOrders) {
            System.out.println("ID: " + order.getOrderID() +
                    " | Product: " + order.getProductId() +
                    " | Supplier: " + order.getSupplierId() +
                    " | Qty: " + order.getQuantity() +
                    " | Day: " + order.getOrderDay());
        }

        System.out.print("Enter the ID of the order to remove: ");
        String input = scanner.nextLine().trim();
        int id;

        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        boolean success = controller.removePeriodicOrderById(id);

        if (success) {
            System.out.println("✅ Periodic order with ID " + id + " removed.");
        } else {
            System.out.println("❌ No order found with ID " + id + ".");
        }
    }





}
