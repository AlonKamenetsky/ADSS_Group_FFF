package Presentation;

import Domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeeInterface {
    private Employee employee;
    private final List<Shift> shifts = ShiftsRepo.getInstance().getShifts();

    public EmployeeInterface(Employee employee) {
        this.employee = employee;
    }


    public void employeeMainMenu(Scanner scanner) {
        boolean exit = false;
        while (!exit) {
            System.out.println("Employee Menu:");
            System.out.println("1. View My Shifts");
            System.out.println("2. Send Swap Request");
            System.out.println("3. Send Weekly Availability");
            System.out.println("4. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewAssignedShifts(shifts);
                    break;
                case 2:
                    sendSwapRequest(scanner);
                    break;
                case 3:
                    chooseAvailableShifts(scanner, shifts);
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    public void chooseAvailableShifts(Scanner scanner, List<Shift> allShifts) {
        while (true) {
            // Build a list of shifts without existing availability.
            // Here, we assume that the uniqueness of a shift is defined by its date and type.
            List<Shift> remainingShifts = new ArrayList<>();
            for (Shift shift : allShifts) {
                boolean alreadyAvailable = false;
                for (Availability avail : employee.getWeeklyAvailability()) {
                    if (shift.getDate().equals(avail.getDate()) && shift.getType().equals(avail.getType())) {
                        alreadyAvailable = true;
                        break;
                    }
                }
                if (!alreadyAvailable) {
                    remainingShifts.add(shift);
                }
            }

            if (remainingShifts.isEmpty()) {
                System.out.println("You have marked availability for every shift.");
                break;
            }

            System.out.println("Please choose a shift to mark availability for (or enter 0 to finish):");
            for (int i = 0; i < remainingShifts.size(); i++) {
                Shift s = remainingShifts.get(i);
                System.out.println((i + 1) + ". Shift ID: " + s.getID() +  ", Type: " + s.getType());
            }
            System.out.println("0. Finish selection");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) {
                System.out.println("Finished marking availability.");
                break;
            }
            if (choice < 1 || choice > remainingShifts.size()) {
                System.out.println("Invalid selection. Try again.");
                continue;
            }

            // Mark availability for the selected shift.
            Shift selectedShift = remainingShifts.get(choice - 1);
            Availability newAvailability = new Availability(selectedShift.getDate(), selectedShift.getType());
            employee.getWeeklyAvailability().add(newAvailability);
            System.out.println("Availability marked for Shift ID: " + selectedShift.getID());
        }
    }

    public void sendSwapRequest(Scanner scanner) {
        List<Shift> assignedShifts = employee.getShifts();
        if (assignedShifts == null || assignedShifts.isEmpty()) {
            System.out.println("No assigned shifts available for swap.");
            return;
        }

        System.out.println("Your Assigned Shifts:");
        int displayIndex = 1;
        // Display each shift with its assigned role.
        for (Shift shift : assignedShifts) {
            Role roleForThisShift = null;
            // Determine the role by checking shift assignments.
            for (ShiftAssignment assignment : shift.getAssignedEmployees()) {
                if (assignment.getEmployeeId().equals(employee.getId())) {
                    roleForThisShift = assignment.getRole();
                    break;
                }
            }
            if (roleForThisShift != null) {
                System.out.println(displayIndex + ". Shift ID: " + shift.getID() + " on " + shift.getDate() +
                        " (Role: " + roleForThisShift.getName() + ")");
            } else {
                System.out.println(displayIndex + ". Shift ID: " + shift.getID() + " on " + shift.getDate() +
                        " (Role: Not assigned)");
            }
            displayIndex++;
        }
        System.out.println("Select the shift number you want to request a swap for:");
        int shiftChoice = scanner.nextInt() - 1;
        scanner.nextLine();
        if (shiftChoice < 0 || shiftChoice >= assignedShifts.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        Shift selectedShift = assignedShifts.get(shiftChoice);

        // Automatically determine the role from the selected shift's assignment.
        Role role = null;
        for (ShiftAssignment assignment : selectedShift.getAssignedEmployees()) {
            if (assignment.getEmployeeId().equals(employee.getId())) {
                role = assignment.getRole();
                break;
            }
        }
        if (role == null) {
            System.out.println("Unable to determine your role for the selected shift.");
            return;
        }

        SwapRequest newRequest = new SwapRequest(employee, selectedShift, role);
        // Upload the new swap request to the dedicated repository.
        SwapRequestsRepo.getInstance().addSwapRequest(newRequest);
        System.out.println("Your swap request has been sent: " + newRequest.toString());
    }
    public void viewAssignedShifts(List<Shift> shifts) {
        System.out.println("Your Assigned Shifts:");
        for (Shift shift : shifts) {
            for (ShiftAssignment assignment : shift.getAssignedEmployees()) {
                if (assignment.getEmployeeId().equals(employee.getId())) {
                    System.out.println("Shift ID: " + shift.getID() + ", Date: " + shift.getDate() + ", Role: " + assignment.getRole().getName());
                }
            }
        }
    }
}

