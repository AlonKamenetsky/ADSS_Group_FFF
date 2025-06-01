package HR.Presentation;

import Transportation.Presentation.TManagerRoleMenu;
import Transportation.Service.DriverService;

import javax.management.InstanceAlreadyExistsException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class DriverMenu {
    private final DriverService DriversHandler;
    private final TManagerRoleMenu managerRoleMenu;
    private final Scanner input;


    public DriverMenu(DriverService driverService, TManagerRoleMenu managerRoleMenu) {
        DriversHandler = driverService;
        this.managerRoleMenu = managerRoleMenu;
        input = new Scanner(System.in);
    }

    public void show() {
        while (true) {
            System.out.println("""
                    === Driver Management ===
                    1. View All Drivers
                    2. View Driver By ID
                    3. Add Driver
                    4. Add license to Driver
                    5. Set Driver Availability
                    6. Remove Driver
                    0. Return to Main Menu""");

            String choice = input.nextLine();
            switch (choice) {
                case "1":
                    viewAllDrivers();
                    break;
                case "2":
                    viewDriverById();
                    break;
                case "3":
                    addDriver();
                    break;
                case "4":
                    addLicenseToDriver();
                    break;
                case "5":
                    setDriverAvailability();
                    break;
                case "6":
                    removeDriver();
                    break;
                case "0":
                    returnToMain();
                    return;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }

    private void viewAllDrivers() {
        System.out.println(DriversHandler.viewAllDrivers());
    }

    public void viewDriverById() {
        System.out.println("Enter a driver's ID:");
        try {
            String currId = input.nextLine();
            Integer.parseInt(currId);
            System.out.println(DriversHandler.getDriverById(currId));
        } catch (NullPointerException e) {
            System.out.println("Not a valid driver ID.");
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid ID format, only numbers please!");
            return;
        }
    }

    public void returnToMain() {
        managerRoleMenu.show();
    }

    public void addDriver() {
        System.out.println("Enter new driver's ID:");
        String newId = input.nextLine();
        try {
            Integer.parseInt(newId);
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid ID format, only numbers please!");
            return;
        }
        System.out.println("Enter new driver's name:");
        String newName = input.nextLine();
        System.out.println("Enter new driver's license:");
        String license = input.nextLine();
        try {
            DriversHandler.AddDriver(newId, newName, license);
            System.out.println("Driver added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid license type. Please enter B, C, or C1.");
        } catch (NullPointerException n) {
            System.out.println("One of the inputs you provided is empty.");
        }
        catch (InstanceAlreadyExistsException f) {
            System.out.print("Driver with this ID already exists.\n");
            return;
        }
    }

    public void addLicenseToDriver() {
        System.out.println("Enter driver's ID:");
        String currId = input.nextLine();
        try {
            Integer.parseInt(currId);
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid ID format, only numbers please!");
            return;
        }
        if (DriversHandler.getDriverById(currId).equalsIgnoreCase("Driver Doesn't Exist")) {
            System.out.println("Driver Doesn't Exist");
            return;
        }

        System.out.println("Enter license:");
        String license = input.nextLine();
        try {
            if (!DriversHandler.hasLicense(currId, license)) {
                DriversHandler.AddLicense(currId, license);
                System.out.println("License successfully added.");
            } else {
                System.out.println("Driver already has this license.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid license type. Please enter B, C, or C1.");
        }
    }

    public void setDriverAvailability() {
        System.out.println("Enter driver's ID:");
        String currId = input.nextLine();
        try {
            Integer.parseInt(currId);
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid ID format, only numbers please!");
            return;
        }
        System.out.println("What is the driver's availability? (Free/Busy)");
        String currStatus = input.nextLine();
        try {
            switch (currStatus.toLowerCase()) {
                case "free":
                    DriversHandler.ChangeDriverAvailability(currId, true);
                    System.out.println("Driver's availability successfully changed.");
                case "busy":
                    DriversHandler.ChangeDriverAvailability(currId, false);
                    System.out.println("Driver's availability successfully changed.");
                default :
                    System.out.println("Invalid input.");
            }
        } catch (NoSuchElementException e) {
            System.out.println("Given driver doesn't exist.");
        }
    }

    public void removeDriver() {
        System.out.println("Enter driver's ID:");
        try {
            String currId = input.nextLine();
            Integer.parseInt(currId);
            DriversHandler.deleteDriver(currId);
            System.out.println("Driver removed successfully.");
        } catch (NullPointerException e) {
            System.out.println("Empty driver ID entered.");
        } catch (NoSuchElementException n) {
            System.out.println("Driver doesn't exist.");
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid ID format, only numbers please!");
            return;
        }
    }
}