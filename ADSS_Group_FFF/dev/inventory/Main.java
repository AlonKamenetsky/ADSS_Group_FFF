package inventory;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        InventoryService service = new InventoryService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Inventory Management System!");

        System.out.println("\nChoose Interface:");
        System.out.println("1. CLI (Text Mode)");
        System.out.println("2. GUI (Window Mode)");
        System.out.print("Enter your choice: ");
        String uiChoice = scanner.nextLine();

        if (uiChoice.equals("1")) {
            SimpleCLIManager cli = new SimpleCLIManager(service);
            cli.run();
        } else if (uiChoice.equals("2")) {
            SwingGUIManager gui = new SwingGUIManager(service);
            gui.setVisible(true);
            gui.bringToFront();
            gui.askToLoadSampleData(); // <<< NEW
        } else {
            System.out.println("Invalid interface choice. Exiting.");
        }
    }
}
