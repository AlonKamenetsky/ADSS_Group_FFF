package Presentation;

import java.util.List;
import java.util.Scanner;
import Domain.*;

public class HRInterface {
    private String currentUserId;
    private String selectedShiftId;

    public HRInterface(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void assignEmployeeToShift(Scanner scanner, List<EmployeeDL> employees, List<ShiftDL> shifts) {
        System.out.println("Available Shifts:");
        for (int i = 0; i < shifts.size(); i++) {
            System.out.println(i + 1 + ". " + shifts.get(i).getID());
        }
        System.out.println("Select shift:");
        int shiftIndex = scanner.nextInt() - 1;

        System.out.println("Available Employees:");
        for (int i = 0; i < employees.size(); i++) {
            System.out.println(i + 1 + ". " + employees.get(i).getName());
        }
        System.out.println("Select employee:");
        int employeeIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        ShiftDL shift = shifts.get(shiftIndex);
        EmployeeDL employee = employees.get(employeeIndex);

        System.out.println("Employee " + employee.getName() + " assigned to Shift " + shift.getID());
    }
    public void addNewRole(Scanner scanner) {
        RolesRepo rolesRepo = RolesRepo.getInstance();
        System.out.println("Enter new role name:");
        String newRoleName = scanner.nextLine();
        rolesRepo.addRole(new RoleDL(newRoleName));
        System.out.println("New Role Added Successfully!");
    }
}
