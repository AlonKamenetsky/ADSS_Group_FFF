//package Transportation.Service;
//
//import javax.management.InstanceAlreadyExistsException;
//import java.text.ParseException;
//import java.util.HashMap;
//
//public class DataService {
//    private final ItemService itemService;
//    private final DriverService driverService;
//    private final TruckService truckService;
//    private final ZoneService zoneService;
//    private final SiteService siteService;
//    private final TaskService taskService;
//
//    public DataService(ItemService itemService, DriverService driverService, TruckService truckService, ZoneService zoneService, SiteService siteService, TaskService taskService) {
//        this.itemService = itemService;
//        this.driverService = driverService;
//        this.truckService = truckService;
//        this.zoneService = zoneService;
//        this.siteService = siteService;
//        this.taskService = taskService;
//    }
//
//    public void loadItemData() {
//        itemService.addItem("Bamba", 0.25F);
//        itemService.addItem("Sugar", 2);
//        itemService.addItem("Milk", 1);
//        itemService.addItem("Chicken", 3);
//        itemService.addItem("Coffee Poles", 4);
//        itemService.addItem("Vegetables Box", 10);
//        itemService.addItem("Potatoes", 5);
//    }
//
//    public void loadExtraData() {
//        addDriversMock();
//        addTrucksMock();
//        addZonesMock();
//        addSitesMock();
//        addTasksMock();
//    }
//
//    public void addDriversMock() {
//        try {
//            driverService.AddDriver("207271966", "Liel", "B");
//            driverService.AddDriver("207271900", "Lidor", "B");
//            driverService.AddDriver("207271901", "Alon", "B");
//            driverService.AddDriver("029349339", "Anat", "B");
//            driverService.AddDriver("12345667", "Alex", "C");
//            driverService.AddDriver("12678999", "Bob", "C1");
//        } catch (InstanceAlreadyExistsException e) {
//            return;
//        }
//    }
//
//    public void addTrucksMock() {
//        try {
//            truckService.AddTruck("SMALL", "3524356", "BYD", 100, 20);
//            truckService.AddTruck("SMALL", "123456", "BMW", 120, 13);
//            truckService.AddTruck("MEDIUM", "999999", "Berlingo", 200, 50);
//            truckService.AddTruck("LARGE", "888888", "KIA", 200, 55);
//            truckService.AddTruck("LARGE", "555555", "OPEL", 200, 150);
//        } catch (InstanceAlreadyExistsException e) {
//            return;
//        }
//    }
//
//    public void addZonesMock() {
//        zoneService.AddZone("CENTRAL");
//        zoneService.AddZone("NORTH");
//    }
//
//    public void addSitesMock() {
//        siteService.addSite("Baraket 20 Shoham", "Liel", "0506309997", "CENTRAL");
//        siteService.addSite("Bazel 12 Tel Aviv", "Lee", "0506304499", "CENTRAL");
//        siteService.addSite("Baraket 45 Hadera", "Ron", "0546677889", "NORTH");
//        siteService.addSite("Medical Center of Galil", "Yaara", "0524667444", "NORTH");
//    }
//
//    public void addTasksMock() {
//        try {
//            // picking items
//            HashMap<String, Integer> items1 = new HashMap<>();
//            items1.put("Bamba", 3);
//            items1.put("Milk", 2);
//            items1.put("Potatoes", 5);
//            items1.put("Chicken", 1);
//
//            HashMap<String, Integer> items2 = new HashMap<>();
//            items2.put("Coffee Poles", 4);
//            items2.put("Vegetables Box", 10);
//            items2.put("Sugar", 2);
//            items2.put("Potatoes", 5);
//
//            // first task, weight is 30.75
//            taskService.addTask("12/04/2025", "13:33", "Baraket 20 Shoham");
//            taskService.addDocToTask("12/04/2025", "13:33", "Baraket 20 Shoham", "Bazel 12 Tel Aviv", items1);
//            taskService.updateWeightForTask("12/04/2025", "13:33", "Baraket 20 Shoham");
//            taskService.assignDriverAndTruckToTask("12/04/2025", "13:33", "Baraket 20 Shoham");
//
//            // second task, weight is 145
//            taskService.addTask("22/04/2025", "10:33", "Bazel 12 Tel Aviv");
//            taskService.addDocToTask("22/04/2025", "10:33", "Bazel 12 Tel Aviv", "Baraket 20 Shoham", items2);
//            taskService.updateWeightForTask("22/04/2025", "10:33", "Bazel 12 Tel Aviv");
//            taskService.assignDriverAndTruckToTask("22/04/2025", "10:33", "Bazel 12 Tel Aviv");
//        } catch (ParseException e) {
//            return;
//        }
//    }
//}