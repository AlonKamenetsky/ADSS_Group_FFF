package Transportation.Presentation;

import Transportation.Service.*;

import java.util.Scanner;

public class TManagerRoleMenu {
    private final TaskMenu TaskMenuUI;
    private final TruckMenu TruckMenuUI;
    private final SiteMenu SiteMenuUI;
    private final ZoneMenu ZoneMenuUI;

    public TManagerRoleMenu( TruckService truckService, TaskService taskService, ZoneService zoneService, SiteService siteService, ItemService itemService, SiteZoneService siteZoneService) {
        TaskMenuUI = new TaskMenu(taskService, itemService, this);
        TruckMenuUI = new TruckMenu(this);
        SiteMenuUI = new SiteMenu(this);
        ZoneMenuUI = new ZoneMenu(this);
    }

    public void show() {
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("""
                    What would you like to use?
                    
                    1. Truck Menu
                    2. Task Menu
                    3. Site Menu
                    4. Zone Menu
                    0. Logout""");
            String choiceManager = input.nextLine();
            switch (choiceManager) {
                case "1":
                    TruckMenuUI.show();
                    break;
                case "2":
                    TaskMenuUI.show();
                    break;
                case "3":
                    SiteMenuUI.show();
                    break;
                case "4":
                    ZoneMenuUI.show();
                    break;
                case "0":
                    System.out.println("Bye!");
                    System.exit(0);
                    return;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }
}