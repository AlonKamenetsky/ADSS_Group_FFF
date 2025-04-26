package inventory;

import java.util.Scanner;

public class SimpleCLIManager {
    private InventoryService service;
    private InventoryManagerUI ui;
    private Scanner scanner;

    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";

    public SimpleCLIManager(InventoryService service) {
        this.service = service;
        this.ui = new InventoryManagerUI(service);
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        clearScreen();
        printAsciiArt();
        askToLoadSampleData(); // <<< NEW
        ui.runMenu();
    }

    private void askToLoadSampleData() {
        System.out.println("\nWould you like to load demo data (example stock)?");
        System.out.println("1. Yes (recommended for quick test)");
        System.out.println("2. No (start empty)");
        System.out.print("Enter your choice: ");
        String demoChoice = scanner.nextLine();

        if (demoChoice.equals("1")) {
            AppInitializer.loadSampleData(service);
            System.out.println("Demo data loaded successfully!");
        } else {
            System.out.println("Starting with empty inventory.");
        }
    }

    private void printAsciiArt() {
        System.out.println(BLUE +
                " Stav & Blanga Present " + RESET);
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
