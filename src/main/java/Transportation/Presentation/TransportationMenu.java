package Transportation.Presentation;

import Transportation.Service.*;
import java.util.Scanner;

public class TransportationMenu {
    private final TManagerRoleMenu TManagerMenuUI;
    private final Scanner input;

    public TransportationMenu( TruckService truckService, TaskService taskService,
                               ZoneService zoneService, SiteService siteService, SiteZoneService siteZoneService,
                               ItemService itemService) {
        this.TManagerMenuUI = new TManagerRoleMenu(truckService, taskService, zoneService, siteService, itemService, siteZoneService);
        this.input = new Scanner(System.in);
    }

    public void show() {
        TManagerMenuUI.show();
    }
}
