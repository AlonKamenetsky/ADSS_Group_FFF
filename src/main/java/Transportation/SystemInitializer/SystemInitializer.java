package Transportation.SystemInitializer;

import Transportation.Domain.*;
import Transportation.Presentation.TransportationMenu;
import Transportation.Service.*;

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

        // === Transportation Menu ===
        return new TransportationMenu(truckService, taskService, zoneService, siteService, siteZoneService, itemService);
    }
}