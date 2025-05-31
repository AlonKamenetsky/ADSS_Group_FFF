package inventory.presentationLayer;


import IntegrationInventoryAndSupplier.MutualProduct;
import inventory.domainLayer.*;
import inventory.serviceLayer.InventoryService;

import java.util.*;

public class InventoryCLI {
    private final InventoryService service;
    private final Scanner scanner = new Scanner(System.in);

    public InventoryCLI(InventoryService service) {
        this.service = service;
    }

    public void run() {
        System.out.println("=== Welcome to Inventory CLI ===");

        // One-time sample data load prompt
        System.out.print("Load sample data? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            AppInitializer.loadSampleData(service);
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
                case "7" -> addNewProduct();
                case "8" -> addNewCategory();
                case "9" -> exit = true;

                default -> System.out.println("Invalid option.");
            }
        }

        System.out.println("Goodbye!");
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
        System.out.print("Choice: ");
    }

    private void listAllItems() {
        for (InventoryProduct item : service.getAllProducts()) {
            System.out.println(item);
        }
    }

    private void listLowStockItems() {
        for (InventoryProduct item : service.getLowStockItems()) {
            System.out.println(item);
        }
    }

    private void listCategories() {
        for (Category cat : service.getAllCategories()) {
            System.out.println(cat);
        }
    }

    private void updateQuantities() {
        System.out.print("Enter item ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Shelf quantity delta (e.g. +3 or -2): ");
        int shelf = Integer.parseInt(scanner.nextLine());
        System.out.print("Backroom quantity delta: ");
        int backroom = Integer.parseInt(scanner.nextLine());
        service.updateItemQuantity(id, shelf, backroom);
        System.out.println("Quantities updated.");
    }

    private void markItemAsDamaged() {
        System.out.print("Enter item ID to mark as damaged: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        InventoryProduct item = getProductById(id);
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
        InventoryReport report = service.generateReport(id, null, status);
        System.out.println(report.toString());
    }

    private void addNewProduct() {
        // 1) Fetch available products from the supplier
        List<MutualProduct> available = service.getAllAvailableProducts();
        if (available.isEmpty()) {
            System.out.println("No products currently available from the supplier.");
            return;
        }

        // 2) Display them in a simple table
        System.out.println("Available supplier products:");
        System.out.printf("%-5s  %-20s  %-15s%n", "ID", "Name", "Manufacturer");
        for (MutualProduct mp : available) {
            System.out.printf(
                    "%-5d  %-20s  %-15s%n",
                    mp.getId(),
                    mp.getName(),
                    mp.getManufacturer()
            );
        }

        // 3) Let user pick one of these IDs
        System.out.print("Enter the ID of the supplier product you want to add: ");
        int chosenId;
        try {
            chosenId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Must be an integer.");
            return;
        }

        // 4) Find the chosen MutualProduct
        MutualProduct chosenProduct = null;
        for (MutualProduct mp : available) {
            if (mp.getId() == chosenId) {
                chosenProduct = mp;
                break;
            }
        }
        if (chosenProduct == null) {
            System.out.println("That ID is not in the supplier list. Aborting.");
            return;
        }

        // 5) Ask for category and verify
        System.out.print("Enter category name for this product: ");
        String categoryName = scanner.nextLine().trim();
        Category category = service.getCategory(categoryName);
        if (category == null) {
            System.out.println("Category does not exist. Please add it first or choose an existing one.");
            return;
        }

        // 6) Prompt for purchase price (since MutualProduct has no costPrice)
        double purchasePrice;
        System.out.print("Enter purchase price for this product: ");
        try {
            purchasePrice = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid purchase price format.");
            return;
        }

        // 7) Prompt for sale price
        double salePrice;
        System.out.print("Enter sale price for this product: ");
        try {
            salePrice = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid sale price format.");
            return;
        }

        // 8) Prompt for initial shelf/backroom quantities and min threshold
        System.out.print("Initial shelf quantity (integer): ");
        int shelfQty;
        try {
            shelfQty = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid shelf quantity format.");
            return;
        }

        System.out.print("Initial backroom quantity (integer): ");
        int backroomQty;
        try {
            backroomQty = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid backroom quantity format.");
            return;
        }

        System.out.print("Minimum threshold (integer): ");
        int minThreshold;
        try {
            minThreshold = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid min threshold format.");
            return;
        }

        // 9) Extract ID, name, manufacturer from the chosen MutualProduct
        int id = chosenProduct.getId();
        String name = chosenProduct.getName();
        String manufacturer = chosenProduct.getManufacturer();

        // 10) Call service.addProduct(...)
        try {
            service.addProduct(
                    id,
                    name,
                    manufacturer,
                    shelfQty,
                    backroomQty,
                    minThreshold,
                    purchasePrice,
                    salePrice,
                    ItemStatus.NORMAL,
                    categoryName
            );
            System.out.println("Product added to inventory.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error adding product: " + ex.getMessage());
        }
    }


    private void addNewCategory() {
        System.out.print("Category name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter parent category name (or leave blank): ");
        String parentName = scanner.nextLine().trim();
        if (parentName.isEmpty()) {
            parentName = null;
        }

        try {
            service.addCategory(name, parentName);
            System.out.println("Category added.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error adding category: " + ex.getMessage());
        }
    }

    // Utility lookup methods
    private InventoryProduct getProductById(int id) {
        for (InventoryProduct product : service.getAllProducts()) {
            if (product.getId() == id) return product;
        }
        return null;
    }

    private Category getCategoryByName(String name) {
        for (Category cat : service.getAllCategories()) {
            if (cat.getName().equals(name)) return cat;
        }
        return null;
    }

}

