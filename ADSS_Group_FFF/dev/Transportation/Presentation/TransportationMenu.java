package Transportation.Presentation;

import Transportation.Service.*;

import java.util.Scanner;

public class TransportationMenu {
    private final TManagerRoleMenu TManagerMenuUI;
    private final Scanner input;

    public TransportationMenu(DriverService driverService, TruckService truckService, TaskService taskService,
                              ZoneService zoneService, SiteService siteService, SiteZoneService siteZoneService,
                              ItemService itemService) {
        this.TManagerMenuUI = new TManagerRoleMenu(driverService, truckService, taskService, zoneService, siteService, itemService, siteZoneService);
        this.input = new Scanner(System.in);
    }

    public void show() {
        boolean validChoice = false;
        while (!validChoice) {
            System.out.println("Hello, what role are you in? (Driver / Manager)");
            String choice = input.nextLine();
            switch (choice.toLowerCase()) {
                case "driver":
                    System.out.println("What is your id?");
                    choice = input.nextLine();
                    //DriverRoleMenuUI.show();
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
