package Transportation.Presentation;

import Transportation.Service.TruckService;
import Transportation.Service.DataService;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class TruckMenu {
    private final TruckService TrucksHandler;
    private final TManagerRoleMenu managerRoleMenu;
    private Scanner input;

    public TruckMenu(TruckService truckService, TManagerRoleMenu managerRoleMenu) {
        TrucksHandler = truckService;
        this.managerRoleMenu = managerRoleMenu;
        input = new Scanner(System.in);
    }

    public void show() {
        while (true) {
            System.out.println("""
                    === Truck Management ===
                    1. View All Trucks
                    2. View Truck By license number
                    3. Add Truck
                    4. Set Truck Availability
                    5. Remove Truck
                    0. Return to Main Menu""");

            String choice = input.nextLine();
            switch (choice) {
                case "1":
                    viewAllTrucks();
                    break;
                case "2":
                    viewTruckByLicenseNumber();
                    break;
                case "3":
                    addTruck();
                    break;
                case "4":
                    setDriverAvailability();
                    break;
                case "5":
                    removeTruck();
                    break;
                case "0":
                    returnToMain();
                    return;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }

    private void removeTruck() {
        System.out.println("Enter truck's license number:");
        try {
            TrucksHandler.deleteTruck(input.nextLine());
            System.out.println("Truck removed successfully");
        } catch (NullPointerException e) {
            System.out.println("Empty license number entered.");
        } catch (NoSuchElementException n) {
            System.out.println("Truck doesn't exist.");
        }
    }

    private void setDriverAvailability() {
        System.out.println("Enter truck's license number:");
        String inputLicenseNumber = input.nextLine();
        System.out.println("What is the truck's availability? (Free/Busy)");
        String currStatus = input.nextLine();
        try {
            switch (currStatus.toLowerCase()) {
                case "free" -> TrucksHandler.ChangeTruckAvailability(inputLicenseNumber, true);
                case "busy" -> TrucksHandler.ChangeTruckAvailability(inputLicenseNumber, false);
                default -> System.out.println("Invalid input.");
            }
        } catch (NoSuchElementException e) {
            System.out.println("Given truck doesn't exist.");
        }
    }

    private void addTruck() {
        float inputNetWeight = 0, inputMaxWeight = 0;
        System.out.println("Enter new truck's license number:");
        String inputLicenseNumber = input.nextLine();
        System.out.println("Enter new truck's type (Small/Medium/Large):");
        String inputTruckType = input.nextLine();
        if (!inputTruckType.equalsIgnoreCase("Small") && !inputTruckType.equalsIgnoreCase("Medium") && !inputTruckType.equalsIgnoreCase("Large")) {
            System.out.println("Invalid truck type. Please enter Small, Medium, or Large.");
            return;
        }
        System.out.println("Enter new truck's model:");
        String inputModel = input.nextLine();
        System.out.println("Enter new truck's Net Weight:");
        try {
            inputNetWeight = Float.parseFloat(input.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Not a valid weight");
        }
        System.out.println("Enter new truck's Maximum Weight:");
        try {
            inputMaxWeight = Float.parseFloat(input.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Not a valid weight");
        }
        try {
            TrucksHandler.AddTruck(inputTruckType, inputLicenseNumber, inputModel, inputNetWeight, inputMaxWeight);
            System.out.println("Truck added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());

        } catch (NullPointerException n) {
            System.out.println("One of the inputs you provided is empty.");
        }
    }

    public void returnToMain() {
        managerRoleMenu.show();
    }

    public void viewAllTrucks() {
        System.out.println(TrucksHandler.viewAllTrucks());
    }

    public void viewTruckByLicenseNumber() {
        System.out.println("Enter a truck's license number:");
        try {
            String answer = TrucksHandler.getTruckByLicenseNumber(input.nextLine());
            System.out.println(answer);
        } catch (NullPointerException e) {
            System.out.println("Not a valid license number");
        }
        catch (NoSuchElementException n) {
            System.out.println("Truck doesn't exist.");
        }
    }
}