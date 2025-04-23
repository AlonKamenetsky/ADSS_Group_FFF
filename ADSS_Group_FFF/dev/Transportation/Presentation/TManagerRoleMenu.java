package Transportation.Presentation;

import Transportation.Service.*;

import java.text.ParseException;
import java.util.Scanner;

public class TManagerRoleMenu {
    private final DriverMenu DriverMenuUI;
    private final TaskMenu TaskMenuUI;
    private final TruckMenu TruckMenuUI;
    private final SiteMenu SiteMenuUI;
    private final ZoneMenu ZoneMenuUI;
    private final TransportationMenu transportationMenu;

    public TManagerRoleMenu(DriverService driverService, TruckService truckService, TaskService taskService, ZoneService zoneService, SiteService siteService, ItemService itemService, SiteZoneService siteZoneService, TransportationMenu transportationMenu1) {
        DriverMenuUI = new DriverMenu(driverService, this);
        TaskMenuUI = new TaskMenu(taskService, itemService, this);
        TruckMenuUI = new TruckMenu(truckService, this);
        SiteMenuUI = new SiteMenu(siteService, this);
        ZoneMenuUI = new ZoneMenu(zoneService, siteZoneService,this);
        transportationMenu = transportationMenu1;
    }

    public void show() {
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("""
                    What would you like to use?
                    1. Driver Menu
                    2. Truck Menu
                    3. Task Menu
                    4. Site Menu
                    5. Zone Menu
                    0. Logout""");
            String choiceManager = input.nextLine();
            switch (choiceManager) {
                case "1":
                    DriverMenuUI.show();
                    break;
                case "2":
                    TruckMenuUI.show();
                    break;
                case "3":
                    TaskMenuUI.show();
                    break;
                case "4":
                    SiteMenuUI.show();
                    break;
                case "5":
                    ZoneMenuUI.show();
                    break;
                case "0":
                    transportationMenu.showHelper();
                    return;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }
}