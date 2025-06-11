package inventory.presentationLayer;


import IntegrationInventoryAndSupplier.MutualProduct;
import inventory.domainLayer.*;
import inventory.serviceLayer.InventoryService;

import java.util.*;

public class InventoryCLI {
    private final InventoryService service;
    private final Scanner scanner = new Scanner(System.in);

    public InventoryCLI() {
        this.service = InventoryService.getInstance();
    }

    public void run() {
        System.out.println("=== Welcome to Inventory CLI ===");

        // One-time sample data load prompt
//        System.out.print("Load sample data? (y/n): ");
//        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
////            AppInitializer.loadSampleData(service);
//            System.out.println("Sample data loaded.");
//        }

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
        System.out.println("Logging out Inventory returning to main menu");

    }


    private void printMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1. View Inventory");
        System.out.println("2. View Low Stock");
        System.out.println("3. View Categories");
        System.out.println("4. Update Quantities");
        System.out.println("5. Mark Damaged");
        System.out.println("6. Generate Report");
        System.out.println("7. Add New Product");
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

    // ────────────── in InventoryCLI.java ──────────────

    private void generateReport() {
        // 1) Fetch and display all existing categories
        List<Category> allCats = service.getAllCategories();
        List<Category> filterCategories = null;

        if (!allCats.isEmpty()) {
            System.out.println("Available categories:");
            for (int i = 0; i < allCats.size(); i++) {
                System.out.printf("  %2d) %s%n", i + 1, allCats.get(i).getName());
            }

            System.out.print("Filter by categories? (y/n): ");
            String catChoice = scanner.nextLine().trim();
            if (catChoice.equalsIgnoreCase("y")) {
                System.out.print("Enter comma-separated numbers of categories to filter: ");
                String line = scanner.nextLine().trim();
                String[] tokens = line.split(",");
                filterCategories = new ArrayList<>();

                for (String tok : tokens) {
                    try {
                        int idx = Integer.parseInt(tok.trim()) - 1;
                        if (idx >= 0 && idx < allCats.size()) {
                            filterCategories.add(allCats.get(idx));
                        } else {
                            System.out.println("  Ignoring invalid category number: " + (idx + 1));
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("  Ignoring invalid input: " + tok.trim());
                    }
                }

                if (filterCategories.isEmpty()) {
                    System.out.println("No valid categories selected; no category filter will be applied.");
                    filterCategories = null;
                }
            }
        } else {
            System.out.println("(No categories defined yet—skipping category filter.)");
        }

        // 2) Prompt for status filter
        System.out.print("Filter by status? (y/n): ");
        ItemStatus status = null;
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.print("Enter status (OK, DAMAGED, EXPIRED): ");
            String statusInput = scanner.nextLine().trim().toUpperCase();
            try {
                status = ItemStatus.valueOf(statusInput);
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid status '" + statusInput + "'. No status filter will be applied.");
                status = null;
            }
        }

        // 3) Call service.generateReport(...) — it auto-generates the next ID internally
        InventoryReport report = service.generateReport(filterCategories, status);

        // 4) Show results
        System.out.println("Generated report with ID = " + report.getId());
        System.out.println(report);
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
        System.out.print("Enter the ID of the product you want to add: ");
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

        // 5) Set Category
        Category category = new Category(chosenProduct.getProductCategory().toString(),null);
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
                    chosenProduct.getProductCategory().toString()
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

