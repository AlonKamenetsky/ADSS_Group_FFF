package Transportation;

import Transportation.Presentation.*;
import Transportation.Domain.*;
import Transportation.Service.*;

public class TransportationMain {
    public static void main(String[] args) {
        // === Domain Managers ===
        DriverManager driverManager = new DriverManager();
        TruckManager truckManager = new TruckManager();
        ZoneManager zoneManager = new ZoneManager();
        SiteManager siteManager = new SiteManager(zoneManager); // and pass dependencies if needed
        SiteZoneManager siteZoneManager = new SiteZoneManager(siteManager, zoneManager);
        ItemManager itemManager = new ItemManager();
        TaskManager taskManager = new TaskManager(siteManager, driverManager, truckManager, itemManager);

        // === Services (pass managers) ===
        DriverService driverService = new DriverService(driverManager);
        TruckService truckService = new TruckService(truckManager);
        TaskService taskService = new TaskService(taskManager);
        SiteService siteService = new SiteService(siteManager, siteZoneManager);
        ZoneService zoneService = new ZoneService(zoneManager);
        ItemService itemService = new ItemService(itemManager);
        SiteZoneService siteZoneService = new SiteZoneService(siteZoneManager);
        DataService dataService = new DataService(itemService,driverService,truckService,zoneService,siteService,taskService);

        // === Menus (pass services) ===
        TransportationMenu mainMenu = new TransportationMenu(driverService, truckService, taskService, zoneService, siteService, siteZoneService, itemService, dataService);
        mainMenu.show();
    }
}