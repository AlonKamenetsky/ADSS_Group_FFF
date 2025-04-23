package Transportation.Presentation;

import Transportation.Service.DriverService;
import Transportation.Service.TaskService;
import java.util.Scanner;

public class DriverRoleMenu {
    private String driverId;
    private final DriverService driverHandler;
    private final TaskService driverTaskHandler;
    private final TransportationMenu transportationMenu; // reference to go back

    public DriverRoleMenu(TransportationMenu transportationMenu, DriverService driverService, TaskService taskService) {
        driverId = "";
        this.transportationMenu = transportationMenu;
        driverHandler = driverService;
        driverTaskHandler = taskService;

    }

    public void modifyDriverId(String driverId) {
        this.driverId = driverId;
    }

    public void show(){
        Scanner input = new Scanner(System.in);
        while(true) {
            System.out.println("Welcome, Driver " + driverId);
            System.out.println("1. View My Info");
            System.out.println("2. View My Tasks");
            System.out.println("0. Logout");
            String choice = input.nextLine();
            switch (choice) {
                case "1":
                    viewMyInfo();
                    break;
                case "2":
                    viewMyTasks();
                    break;
                case "0":
                    transportationMenu.showHelper();
                    return;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }

    private void viewMyInfo() {
        System.out.println(driverHandler.getDriverById(driverId));
    }

    private void viewMyTasks() {
        String tasksOutput = driverTaskHandler.getTasksByDriverId(driverId);

        if (tasksOutput.isBlank()) {
            System.out.println("No tasks assigned for this driver.");
        } else {
            System.out.println(tasksOutput);
        }
    }
}