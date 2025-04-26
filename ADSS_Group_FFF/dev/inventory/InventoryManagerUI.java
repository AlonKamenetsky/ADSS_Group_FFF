package inventory;

import java.util.Scanner;
import java.util.List;

public class InventoryManagerUI {
    private InventoryService service;
    private Scanner scanner;

    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";

    public InventoryManagerUI(InventoryService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void runMenu() {
        while (true) {
            clearScreen();
            System.out.println(BLUE + "\n=== Inventory Management Menu ===" + RESET);
            System.out.println(GREEN + "1. View all inventory" + RESET);
            System.out.println(GREEN + "2. View low stock items" + RESET);
            System.out.println(GREEN + "3. View categories" + RESET);
            System.out.println(YELLOW + "4. Update item quantities" + RESET);
            System.out.println(YELLOW + "5. Mark item as DAMAGED or EXPIRED" + RESET);
            System.out.println(YELLOW + "6. Generate inventory report" + RESET);
            System.out.println(RED + "7. Exit" + RESET);
            System.out.print("\nEnter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    printAllItems();
                    break;
                case "2":
                    printLowStockItems();
                    break;
                case "3":
                    printCategories();
                    break;
                case "4":
                    handleUpdateQuantities();
                    break;
                case "5":
                    handleMarkItemStatus();
                    break;
                case "6":
                    handleGenerateReport();
                    break;
                case "7":
                    System.out.println(RED + "Exiting the system. Goodbye!" + RESET);
                    return;
                default:
                    System.out.println(RED + "Invalid choice. Please try again." + RESET);
            }

            System.out.println(YELLOW + "\nPress Enter to continue..." + RESET);
            scanner.nextLine();
        }
    }

    private void handleUpdateQuantities() {
        System.out.print(YELLOW + "Enter Item ID: " + RESET);
        String itemId = scanner.nextLine();

        InventoryItem itemToUpdate = null;
        for (InventoryItem item : service.getAllItems()) {
            if (item.getId().equals(itemId)) {
                itemToUpdate = item;
                break;
            }
        }

        if (itemToUpdate == null) {
            System.out.println(RED + "Item not found." + RESET);
            return;
        }

        System.out.println(BLUE + "\nCurrent Quantities:" + RESET);
        System.out.println(GREEN + "Shelf: " + itemToUpdate.getShelfQuantity() + RESET);
        System.out.println(GREEN + "Backroom: " + itemToUpdate.getBackroomQuantity() + RESET);

        System.out.println(YELLOW + "\nWhat would you like to update?" + RESET);
        System.out.println(GREEN + "1. Shelf Quantity Only" + RESET);
        System.out.println(GREEN + "2. Backroom Quantity Only" + RESET);
        System.out.println(GREEN + "3. Both Shelf and Backroom" + RESET);
        System.out.print(YELLOW + "Enter your choice: " + RESET);
        String choice = scanner.nextLine();

        int shelfDelta = 0;
        int backroomDelta = 0;

        if (choice.equals("1")) {
            System.out.print(YELLOW + "Enter quantity change for shelf (can be negative): " + RESET);
            shelfDelta = Integer.parseInt(scanner.nextLine());
        } else if (choice.equals("2")) {
            System.out.print(YELLOW + "Enter quantity change for backroom (can be negative): " + RESET);
            backroomDelta = Integer.parseInt(scanner.nextLine());
        } else if (choice.equals("3")) {
            System.out.print(YELLOW + "Enter quantity change for shelf (can be negative): " + RESET);
            shelfDelta = Integer.parseInt(scanner.nextLine());
            System.out.print(YELLOW + "Enter quantity change for backroom (can be negative): " + RESET);
            backroomDelta = Integer.parseInt(scanner.nextLine());
        } else {
            System.out.println(RED + "Invalid choice." + RESET);
            return;
        }

        int beforeShelf = itemToUpdate.getShelfQuantity();
        int beforeBackroom = itemToUpdate.getBackroomQuantity();

        service.updateQuantities(itemId, shelfDelta, backroomDelta);

        System.out.println(GREEN + "\nUpdate Successful!" + RESET);
        System.out.println(BLUE + "Before -> After:" + RESET);
        System.out.println(GREEN + "Shelf: " + beforeShelf + " -> " + itemToUpdate.getShelfQuantity() + RESET);
        System.out.println(GREEN + "Backroom: " + beforeBackroom + " -> " + itemToUpdate.getBackroomQuantity() + RESET);
    }


    private void handleMarkItemStatus() {
        System.out.print(YELLOW + "Enter Item ID: " + RESET);
        String itemId = scanner.nextLine();
        System.out.println(YELLOW + "Choose new status:" + RESET);
        System.out.println(GREEN + "1. DAMAGED" + RESET);
        System.out.println(GREEN + "2. EXPIRED" + RESET);
        String statusChoice = scanner.nextLine();

        ItemStatus newStatus = null;
        if (statusChoice.equals("1")) {
            newStatus = ItemStatus.DAMAGED;
        } else if (statusChoice.equals("2")) {
            newStatus = ItemStatus.EXPIRED;
        } else {
            System.out.println(RED + "Invalid status choice." + RESET);
            return;
        }

        for (InventoryItem item : service.getAllItems()) {
            if (item.getId().equals(itemId)) {
                item.setStatus(newStatus);
                System.out.println(GREEN + "Item status updated successfully." + RESET);
                return;
            }
        }

        System.out.println(RED + "Item not found." + RESET);
    }

    private void handleGenerateReport() {
        System.out.print(YELLOW + "Enter report ID: " + RESET);
        String reportId = scanner.nextLine();

        System.out.println(YELLOW + "Would you like to filter by status?" + RESET);
        System.out.println(GREEN + "1. No filter" + RESET);
        System.out.println(GREEN + "2. DAMAGED only" + RESET);
        System.out.println(GREEN + "3. EXPIRED only" + RESET);
        String filterChoice = scanner.nextLine();

        ItemStatus statusFilter = null;
        if (filterChoice.equals("2")) {
            statusFilter = ItemStatus.DAMAGED;
        } else if (filterChoice.equals("3")) {
            statusFilter = ItemStatus.EXPIRED;
        }

        InventoryReport report = service.generateReport(reportId, null, statusFilter);
        System.out.println(BLUE + report + RESET);
    }

    public void printAllItems() {
        System.out.println(BLUE + "\n=== All Inventory Items ===" + RESET);
        for (InventoryItem item : service.getAllItems()) {
            System.out.println(GREEN + item + RESET);
            System.out.println(); // Blank line
        }
    }

    public void printLowStockItems() {
        System.out.println(BLUE + "\n=== Low Stock Items ===" + RESET);
        for (InventoryItem item : service.getLowStockItems()) {
            System.out.println(GREEN + item + RESET);
            System.out.println(); // Blank line
        }
    }

    public void printCategories() {
        System.out.println(BLUE + "\n=== Categories ===" + RESET);
        for (Category cat : service.getAllCategories()) {
            System.out.println(GREEN + cat + RESET);
            System.out.println(); // Blank line
        }
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
