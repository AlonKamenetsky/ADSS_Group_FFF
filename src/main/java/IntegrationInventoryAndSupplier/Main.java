package IntegrationInventoryAndSupplier;

import Integration4Modules.Interfaces.SupplierInterface;
import SuppliersModule.DataLayer.CsvToDatabaseImporter;
import SuppliersModule.PresentationLayer.SupplierCLI;
import SuppliersModule.ServiceLayer.ServiceController;
import inventory.presentationLayer.InventoryCLI;
import inventory.serviceLayer.InventoryService;


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // if block for choosing if you wanna log in as a Supplier or Inventory

        // will call appropriate CLI

        // for example, calls SupplierCLI
        // supplierService.getInstance().CLI.run();

        SupplierInterface serviceController = ServiceController.getInstance();
        InventoryService inventoryService = InventoryService.getInstance();
        inventoryService.setSupplierService(serviceController);

        System.out.println("testing main");
        System.out.print("Load sample data? PLEASE NOTE THAT CHOOSING YES MEANS DELETING ALL CURRENT DATA ON THE DATABASE (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {

            try {
                // 1) Import CSV data into the SQLite DB
                CsvToDatabaseImporter.importAll();
            } catch (Exception e) {
                System.err.println("CSV import failed:");
                e.printStackTrace();
                return;
            }

            System.out.println("Sample data loaded.");
        }

        while(true) {

            System.out.println("Do you wanna log in as inventory or supplier? (i/s)");
             String ans = scanner.nextLine().trim();
            if (ans.equalsIgnoreCase("i")) {
                InventoryCLI inventoryCLI = new InventoryCLI();
                System.out.println("Logging in as inventory");
                inventoryCLI.run();
            } else if (ans.equalsIgnoreCase("s")) {
                SupplierCLI supplierCLI = new SupplierCLI();
                System.out.println("Logging in as supplier");
                supplierCLI.mainCliMenu();
            }
        }

    }
}