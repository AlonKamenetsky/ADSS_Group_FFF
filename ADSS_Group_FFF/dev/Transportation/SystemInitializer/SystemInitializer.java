package Transportation.SystemInitializer;

import Transportation.Domain.*;
import Transportation.Presentation.TransportationMenu;
import Transportation.Service.*;
import Util.DatabaseInitializer;

import java.sql.SQLException;
import java.util.Scanner;

public class SystemInitializer {
    public static TransportationMenu buildApplication() {
        // === HR.tests.Domain Managers ===
        ItemManager itemManager = new ItemManager();

        // === Services (pass managers) ===
        TruckService truckService = new TruckService();
        TaskService taskService = new TaskService();
        SiteService siteService = new SiteService();
        ZoneService zoneService = new ZoneService();
        ItemService itemService = new ItemService(itemManager);
        SiteZoneService siteZoneService = new SiteZoneService();

        // === Database Initializer ===
        DatabaseInitializer dbInit = new DatabaseInitializer();

        Scanner scanner = new Scanner(System.in);
        boolean validChoice = false;

        while (!validChoice) {
            System.out.println("Do you want to initialize a new system? (Y/N)");
            String answer = scanner.nextLine().trim().toUpperCase();
            try {
                switch (answer) {
                    case "Y":
                        dbInit.loadItems();
                        validChoice = true;
                        break;
                    case "N":
                        dbInit.loadTransportaionFakeData();
                        dbInit.loadItems();
                        validChoice = true;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (SQLException e) {
                System.out.println("Error during DB initialization: " + e.getMessage());
            }
        }

        // === Transportation Menu ===
        return new TransportationMenu(truckService, taskService, zoneService, siteService, siteZoneService, itemService);
    }
}