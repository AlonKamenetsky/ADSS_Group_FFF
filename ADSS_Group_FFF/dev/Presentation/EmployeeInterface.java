package Presentation;

import Domain.EmployeeDL;
import Domain.ShiftAssignmentDL;
import Domain.ShiftDL;

import java.util.List;
import java.util.Scanner;

public class EmployeeInterface {
    private EmployeeDL employee;

    public EmployeeInterface(EmployeeDL employee) {
        this.employee = employee;
    }

    public void employeeMainMenu(Scanner scanner, List<ShiftDL> shifts) {
        boolean exit = false;
        while (!exit) {
            System.out.println("Employee Menu:");
            System.out.println("1. View My Shifts");
            System.out.println("2. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewAssignedShifts(shifts);
                    break;
                case 2:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    public void viewAssignedShifts(List<ShiftDL> shifts) {
        System.out.println("Your Assigned Shifts:");
        for (ShiftDL shift : shifts) {
            for (ShiftAssignmentDL assignment : shift.getAssignedEmployees()) {
                if (assignment.getEmployeeId().equals(employee.getId())) {
                    System.out.println("Shift ID: " + shift.getID() + ", Date: " + shift.getDate() + ", Role: " + assignment.getRole().getName());
                }
            }
        }
    }
}

