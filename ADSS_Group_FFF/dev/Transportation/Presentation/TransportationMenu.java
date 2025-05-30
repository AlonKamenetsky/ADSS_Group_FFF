package Transportation.Presentation;

import Transportation.Service.*;

import javax.management.InstanceAlreadyExistsException;
import java.util.Scanner;

public class TransportationMenu {
    private final TManagerRoleMenu TManagerMenuUI;
    private final DriverRoleMenu DriverRoleMenuUI;
    private final DataService dataService;
    private final Scanner input;

    public TransportationMenu(DriverService driverService, TruckService truckService, TaskService taskService, ZoneService zoneService, SiteService siteService, SiteZoneService siteZoneService, ItemService itemService, DataService dataService) {
        this.TManagerMenuUI = new TManagerRoleMenu(driverService, truckService, taskService, zoneService, siteService, itemService, siteZoneService);
        this.DriverRoleMenuUI = new DriverRoleMenu(driverService, taskService);
        this.dataService = dataService;
        input = new Scanner(System.in);
    }

    public void show() {
        boolean validChoice1 = false;
        while (!validChoice1) {
            System.out.println("Do you want to initialize a new system? (Y/N)");
            String answer = input.nextLine();
            switch (answer) {
                case "Y":
                    dataService.loadItemData();
                    validChoice1 = true;
                    showHelper();
                    break;

                case "N":
                    dataService.loadItemData();
                    dataService.loadExtraData();
                    showHelper();
                    validChoice1 = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }

    }

    public void showHelper() {
        boolean validChoice = false;
        while (!validChoice) {
            System.out.println("Hello, what role are you in? (Driver / Manager)");
            String choice = input.nextLine();
            switch (choice.toLowerCase()) {
                case "driver":
                    System.out.println("What is your id?");
                    choice = input.nextLine();
                    DriverRoleMenuUI.modifyDriverId(choice);
                    DriverRoleMenuUI.show();
                    validChoice = true;
                    break;
                case "manager":
                    TManagerMenuUI.show();
                    validChoice = true;
                    break;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }
}