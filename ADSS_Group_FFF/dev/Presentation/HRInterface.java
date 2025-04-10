package Presentation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import Domain.*;

public class HRInterface {
    private static final RoleDL HR_ROLE = RolesRepo.getInstance().getRoleByName("HR");
    private String currentUserId;
    private String selectedShiftId;
    private RoleDL currentUserRole;

    public HRInterface(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void assignEmployeeToShift(Scanner scanner, List<EmployeeDL> employees, List<ShiftDL> shifts) {
        if (!currentUserRole.equals(HR_ROLE)) {
            System.out.println("Access Denied: Only HR or Shift Manager can assign employees to shifts.");
            return;
        }

        System.out.println("Available Shifts:");
        for (int i = 0; i < shifts.size(); i++) {
            System.out.println(i + 1 + ". " + shifts.get(i).getID() + " on " + shifts.get(i).getDate());
        }
        System.out.println("Select shift:");
        int shiftIndex = scanner.nextInt() - 1;

        ShiftDL shift = shifts.get(shiftIndex);

        System.out.println("Roles required in this shift:");
        for (RoleDL role : shift.getRequiredRoles().keySet()) {
            System.out.println("- " + role.getName());
        }

        scanner.nextLine();
        System.out.println("Enter role to assign:");
        String roleName = scanner.nextLine();
        RoleDL selectedRole = RolesRepo.getInstance().getRoleByName(roleName);

        List<EmployeeDL> qualifiedEmployees = new ArrayList<>();
        for (EmployeeDL e : employees) {
            if (e.getRoles().contains(selectedRole)) {
                qualifiedEmployees.add(e);
            }
        }

        System.out.println("Qualified Employees:");
        for (int i = 0; i < qualifiedEmployees.size(); i++) {
            System.out.println(i + 1 + ". " + qualifiedEmployees.get(i).getName());
        }

        System.out.println("Select employee to assign:");
        int employeeIndex = scanner.nextInt() - 1;

        EmployeeDL selectedEmployee = qualifiedEmployees.get(employeeIndex);
        shift.assignEmployee(selectedEmployee, selectedRole);

        System.out.println("Employee " + selectedEmployee.getName() + " assigned to role " + selectedRole.getName() + " in shift " + shift.getID());
    }
    public void addNewRole(Scanner scanner) {
        if (!currentUserRole.equals(HR_ROLE)) {
            System.out.println("Access Denied: Only HR or Shift Manager can add new roles.");
            return;
        }
        RolesRepo rolesRepo = RolesRepo.getInstance();
        System.out.println("Enter new role name:");
        String newRoleName = scanner.nextLine();
        rolesRepo.addRole(new RoleDL(newRoleName));
        System.out.println("New Role Added Successfully!");
    }
    public void updateEmployeeData(Scanner scanner, List<EmployeeDL> employees) {
        if (!currentUserRole.equals(HR_ROLE)) {
            System.out.println("Access Denied: Only HR Manager can update employee data.");
            return;
        }

        System.out.println("Select Employee to Update:");
        for (int i = 0; i < employees.size(); i++) {
            System.out.println(i + 1 + ". " + employees.get(i).getName());
        }

        int employeeIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        EmployeeDL employee = employees.get(employeeIndex);

        boolean exit = false;
        while (!exit) {
            System.out.println("Select data to update:");
            System.out.println("1. Bank Account");
            System.out.println("2. Salary");
            System.out.println("3. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Enter new bank account:");
                    String newBankAccount = scanner.nextLine();
                    employee.setBankAccount(newBankAccount);
                    System.out.println("Bank account updated successfully!");
                    break;
                case 2:
                    System.out.println("Enter new salary:");
                    float newSalary = scanner.nextFloat();
                    scanner.nextLine();
                    employee.setSalary(newSalary);
                    System.out.println("Salary updated successfully!");
                    break;
                case 3:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    public void removeRole(Scanner scanner) {
        if (!currentUserRole.equals(HR_ROLE)) {
            System.out.println("Access Denied: Only HR or Shift Manager can remove roles.");
            return;
        }
        RolesRepo rolesRepo = RolesRepo.getInstance();
        List<RoleDL> roles = rolesRepo.getRoles();
        if (roles.isEmpty()) {
            System.out.println("No roles available to remove.");
            return;
        }
        System.out.println("Available Roles:");
        for (int i = 0; i < roles.size(); i++) {
            System.out.println((i + 1) + ": " + roles.get(i).getName());
        }
        System.out.println("Select role to remove (enter the number):");
        int roleIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (roleIndex < 0 || roleIndex >= roles.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        RoleDL roleToRemove = roles.get(roleIndex);
        rolesRepo.getRoles().remove(roleToRemove);
        System.out.println("Role " + roleToRemove.getName() + " removed successfully.");
    }

    // New method: AddEmployee
    public void addEmployee(Scanner scanner, List<EmployeeDL> employees) {
        if (!currentUserRole.equals(HR_ROLE)) {
            System.out.println("Access Denied: Only HR Manager can add employees.");
            return;
        }

        System.out.println("Enter Employee ID:");
        String id = scanner.nextLine();
        System.out.println("Enter Employee Name:");
        String name = scanner.nextLine();
        System.out.println("Enter Password:");
        String password = scanner.nextLine();
        System.out.println("Enter Bank Account:");
        String bankAccount = scanner.nextLine();
        System.out.println("Enter Salary:");
        float salary = scanner.nextFloat();
        scanner.nextLine();
        System.out.println("Enter Employment Date (format yyyy-MM-dd):");
        String dateStr = scanner.nextLine();
        Date employmentDate = null;
        try {
            employmentDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Employee not added.");
            return;
        }

        // Prompt for the employee's role.
        System.out.println("Enter Role for the Employee:");
        String roleName = scanner.nextLine();
        RoleDL role = RolesRepo.getInstance().getRoleByName(roleName);
        List<RoleDL> rolesList = new ArrayList<>();
        if (role != null) {
            rolesList.add(role);
        } else {
            System.out.println("Role not found. Adding employee without any role.");
        }

        EmployeeDL newEmployee = new EmployeeDL(id, rolesList, name, password, bankAccount, salary, employmentDate);

        try {
            employees.add(newEmployee);
        } catch (UnsupportedOperationException ex) {
            // If the list is unmodifiable, wrap it in a new modifiable ArrayList and update the reference.
            List<EmployeeDL> modifiableEmployees = new ArrayList<>(employees);
            modifiableEmployees.add(newEmployee);
            // If possible, update the original reference or notify the caller about this change.
            System.out.println("The employees list was unmodifiable. Created a new modifiable list with the new employee.");
            // Depending on your application architecture, you might then propagate this new list.
        }

        System.out.println("Employee " + name + " added successfully.");
    }
    // New method: RemoveEmployee
    public void removeEmployee(Scanner scanner, List<EmployeeDL> employees) {
        if (!currentUserRole.equals(HR_ROLE)) {
            System.out.println("Access Denied: Only HR Manager can remove employees.");
            return;
        }
        if (employees.isEmpty()) {
            System.out.println("No employees available.");
            return;
        }
        System.out.println("Select Employee to Remove:");
        for (int i = 0; i < employees.size(); i++) {
            System.out.println((i + 1) + ". " + employees.get(i).getName());
        }
        int index = scanner.nextInt() - 1;
        scanner.nextLine();
        if (index < 0 || index >= employees.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        EmployeeDL employee = employees.get(index);
        employees.remove(index);
        System.out.println("Employee " + employee.getName() + " removed successfully.");
    }

    public void managerMainMenu(Scanner scanner, List<EmployeeDL> employees, List<ShiftDL> shifts) {
        boolean exit = false;
        while (!exit) {
            System.out.println("Manager Menu:");
            System.out.println("1. Add Employee");
            System.out.println("2. Remove Employee");
            System.out.println("3. Update Employee Data");
            System.out.println("4. Add New Role");
            System.out.println("5. Remove Role");
            System.out.println("6. Assign Employee to Shift");
            System.out.println("7. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 6:
                    assignEmployeeToShift(scanner, employees, shifts);
                    break;
                case 3:
                    updateEmployeeData(scanner, employees);
                    break;
                case 4:
                    addNewRole(scanner);
                    break;
                case 5:
                    removeRole(scanner);
                    break;
                case 1:
                    addEmployee(scanner, employees);
                    break;
                case 2:
                    removeEmployee(scanner, employees);
                    break;
                case 7:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    public void setCurrentUserRole(RoleDL selectedRole) {
        this.currentUserRole = selectedRole;
    }
}
