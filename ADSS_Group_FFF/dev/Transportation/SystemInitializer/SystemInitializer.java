package Transportation.SystemInitializer;

import Transportation.Domain.*;
import Transportation.Presentation.TransportationMenu;
import Transportation.Service.*;

public class SystemInitializer {
    public static TransportationMenu buildApplication() {
        // === HR.tests.Domain Managers ===
        DriverManager driverManager = new DriverManager();
        ItemManager itemManager = new ItemManager();
        TaskManager taskManager = new TaskManager();

        // === Services (pass managers) ===
        DriverService driverService = new DriverService(driverManager);
        TruckService truckService = new TruckService();
        TaskService taskService = new TaskService(taskManager);
        SiteService siteService = new SiteService();
        ZoneService zoneService = new ZoneService();
        ItemService itemService = new ItemService(itemManager);
        SiteZoneService siteZoneService = new SiteZoneService();
       // DataService dataService = new DataService(itemService,driverService,truckService,zoneService,siteService,taskService);

        // === Menus (pass services) ===
        return new TransportationMenu(driverService, truckService, taskService, zoneService, siteService, siteZoneService, itemService /*dataService*/);
    }
}