package Presentation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import Domain.*;

public class HRInterface {
    private static final Role HR_ROLE = RolesRepo.getInstance().getRoleByName("HR");
    private final String currentUserId;
    private String selectedShiftId;
    private Role currentUserRole;
    private final List<SwapRequest> swapRequests = SwapRequestsRepo.getInstance().getSwapRequests();
    private final List<Employee> employees = EmployeesRepo.getInstance().getEmployees();
    private final List<Shift> shifts = ShiftsRepo.getInstance().getShifts();


    public HRInterface(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void assignEmployeeToShift(Scanner scanner, List<Employee> employees, List<Shift> shifts) {
        if (!currentUserRole.equals(HR_ROLE)) {
            System.out.println("Access Denied: Only HR or Shift Manager can assign employees to shifts.");
            return;
        }

        // Display the list of available shifts
        System.out.println("Available Shifts:");
        for (int i = 0; i < shifts.size(); i++) {
            System.out.println((i + 1) + ". " + shifts.get(i).getID() + " on " + shifts.get(i).getDate());
        }
        System.out.println("Select shift:");
        int shiftIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (shiftIndex < 0 || shiftIndex >= shifts.size()) {
            System.out.println("Invalid shift selection.");
            return;
        }
        Shift shift = shifts.get(shiftIndex);

        // Display the roles required in this shift with numbers
        List<Role> rolesList = new ArrayList<>(shift.getRequiredRoles().keySet());
        if (rolesList.isEmpty()) {
            System.out.println("No roles defined for this shift.");
            return;
        }
        System.out.println("Available roles required in this shift:");
        for (int i = 0; i < rolesList.size(); i++) {
            System.out.println((i + 1) + ". " + rolesList.get(i).getName());
        }
        System.out.println("Enter the number corresponding to the role to assign:");
        int roleChoice = scanner.nextInt() - 1;
        scanner.nextLine();
        if (roleChoice < 0 || roleChoice >= rolesList.size()) {
            System.out.println("Invalid role selection.");
            return;
        }
        Role selectedRole = rolesList.get(roleChoice);

        // Filter for employees who have the required role and are available.
        List<Employee> qualifiedEmployees = new ArrayList<>();
        for (Employee e : employees) {
            if (e.getRoles().contains(selectedRole)) {
                boolean available = false;
                if (e.getWeeklyAvailability().isEmpty()) {
                    available = true;
                } else {
                    for (Availability avail : e.getWeeklyAvailability()) {
                        if (shift.getDate().equals(avail.getDate()) && shift.getType().equals(avail.getType())) {
                            available = true;
                            break;
                        }
                    }
                }
                if (available) {
                    qualifiedEmployees.add(e);
                }
            }
        }

        if (qualifiedEmployees.isEmpty()) {
            System.out.println("No qualified employees available based on availability.");
            return;
        }

        // Display the qualified employees for assignment
        System.out.println("Qualified Employees:");
        for (int i = 0; i < qualifiedEmployees.size(); i++) {
            System.out.println((i + 1) + ". " + qualifiedEmployees.get(i).getName());
        }
        System.out.println("Select employee to assign:");
        int employeeIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (employeeIndex < 0 || employeeIndex >= qualifiedEmployees.size()) {
            System.out.println("Invalid employee selection.");
            return;
        }
        Employee selectedEmployee = qualifiedEmployees.get(employeeIndex);
        shift.assignEmployee(selectedEmployee, selectedRole);

        System.out.println("Employee " + selectedEmployee.getName() + " assigned to role " +
                selectedRole.getName() + " in shift " + shift.getID());
    }

    public void addNewRole(Scanner scanner) {
        if (!currentUserRole.equals(HR_ROLE)) {
            System.out.println("Access Denied: Only HR or Shift Manager can add new roles.");
            return;
        }
        RolesRepo rolesRepo = RolesRepo.getInstance();
        System.out.println("Enter new role name:");
        String newRoleName = scanner.nextLine();
        rolesRepo.addRole(new Role(newRoleName));
        System.out.println("New Role Added Successfully!");
    }
    public void updateEmployeeData(Scanner scanner, List<Employee> employees) {
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

        Employee employee = employees.get(employeeIndex);

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
        List<Role> roles = rolesRepo.getRoles();
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
        Role roleToRemove = roles.get(roleIndex);
        rolesRepo.getRoles().remove(roleToRemove);
        System.out.println("Role " + roleToRemove.getName() + " removed successfully.");
    }

    // New method: AddEmployee
    public void addEmployee(Scanner scanner, List<Employee> employees) {
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
        Role role = RolesRepo.getInstance().getRoleByName(roleName);
        List<Role> rolesList = new ArrayList<>();
        if (role != null) {
            rolesList.add(role);
        } else {
            System.out.println("Role not found. Adding employee without any role.");
        }

        Employee newEmployee = new Employee(id, rolesList, name, password, bankAccount, salary, employmentDate);

        try {
            employees.add(newEmployee);
        } catch (UnsupportedOperationException ex) {
            // If the list is unmodifiable, wrap it in a new modifiable ArrayList and update the reference.
            List<Employee> modifiableEmployees = new ArrayList<>(employees);
            modifiableEmployees.add(newEmployee);
            // If possible, update the original reference or notify the caller about this change.
            System.out.println("The employees list was unmodifiable. Created a new modifiable list with the new employee.");
            // Depending on your application architecture, you might then propagate this new list.
        }

        System.out.println("Employee " + name + " added successfully.");
    }
    // New method: RemoveEmployee
    public void removeEmployee(Scanner scanner, List<Employee> employees) {
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
        Employee employee = employees.get(index);
        employees.remove(index);
        System.out.println("Employee " + employee.getName() + " removed successfully.");
    }
    public void processSwapRequests(Scanner scanner) {
        if (swapRequests.isEmpty()) {
            System.out.println("No swap requests available.");
            return;
        }

        // 1. Display all swap requests
        System.out.println("Current Swap Requests:");
        for (int i = 0; i < swapRequests.size(); i++) {
            System.out.println((i + 1) + ". " + swapRequests.get(i).toString());
        }
        System.out.println("Select a swap request to process (enter number):");
        int chosenIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (chosenIndex < 0 || chosenIndex >= swapRequests.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        SwapRequest selectedRequest = swapRequests.get(chosenIndex);

        // 2. Filter for compatible requests:
        // Must have a different employee, a different shift, but the same role.
        List<SwapRequest> compatibleRequests = new ArrayList<>();
        for (SwapRequest sr : swapRequests) {
            if (sr == selectedRequest) continue;
            if (!sr.getEmployee().equals(selectedRequest.getEmployee()) &&
                    !sr.getShift().equals(selectedRequest.getShift()) &&
                    sr.getRole().equals(selectedRequest.getRole())) {
                compatibleRequests.add(sr);
            }
        }
        if (compatibleRequests.isEmpty()) {
            System.out.println("No compatible swap requests found for the selected request.");
            return;
        }

        // 3. Display compatible swap requests and prompt for selection.
        System.out.println("Compatible Swap Requests:");
        for (int i = 0; i < compatibleRequests.size(); i++) {
            System.out.println((i + 1) + ". " + compatibleRequests.get(i).toString());
        }
        System.out.println("Select one of the compatible swap requests (enter number):");
        int candidateIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (candidateIndex < 0 || candidateIndex >= compatibleRequests.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        SwapRequest candidateRequest = compatibleRequests.get(candidateIndex);

        // 4. Perform the swap between the two selected requests.
        swapShifts(selectedRequest, candidateRequest);

        // 5. Remove both swap requests from the list.
        swapRequests.remove(selectedRequest);
        swapRequests.remove(candidateRequest);
    }

    /**
     * Helper method that swaps employees between two shifts for the same role.
     * It extracts the employees, shifts, and role from the provided SwapRequest objects,
     * then updates each shift's required roles mapping accordingly.
     *
     * @param req1 The first SwapRequest.
     * @param req2 The second SwapRequest.
     */
    private void swapShifts(SwapRequest req1, SwapRequest req2) {
        Employee emp1 = req1.getEmployee();
        Employee emp2 = req2.getEmployee();
        Shift  shift1 = req1.getShift();
        Shift  shift2 = req2.getShift();
        Role role = req1.getRole(); // Both requests have the same role after filtering

        // Retrieve the required roles mappings from each shift.
        Map<Role, List<Employee>> rolesMap1 = shift1.getRequiredRoles();
        Map<Role, List<Employee>> rolesMap2 = shift2.getRequiredRoles();

        // From shift1: remove emp1 and add emp2 for the role.
        List<Employee> employeesForRole1 = rolesMap1.get(role);
        if (employeesForRole1 != null && employeesForRole1.contains(emp1)) {
            employeesForRole1.remove(emp1);
            employeesForRole1.add(emp2);
        } else {
            System.out.println("Employee " + emp1.getId() + " was not found for role " +
                    role.getName() + " in shift " + shift1.getID());
        }

        // From shift2: remove emp2 and add emp1 for the role.
        List<Employee> employeesForRole2 = rolesMap2.get(role);
        if (employeesForRole2 != null && employeesForRole2.contains(emp2)) {
            employeesForRole2.remove(emp2);
            employeesForRole2.add(emp1);
        } else {
            System.out.println("Employee " + emp2.getId() + " was not found for role " +
                    role.getName() + " in shift " + shift2.getID());
        }

        System.out.println("Swapped employees " + emp1.getName() + " and " + emp2.getName() +
                " for role " + role.getName() + " between shifts " +
                shift1.getID() + " and " + shift2.getID());
    }

    /**
     * Helper method to externally add a new swap request.
     * Typically called from the EmployeeInterface.
     */
    public void addSwapRequest(SwapRequest request) {
        swapRequests.add(request);
    }
    /**
     * Creates a new shift by asking the HR manager for details.
     * The method prompts for:
     * - Shift ID
     * - Date (in yyyy-MM-dd format)
     * - Shift type (Morning/Evening)
     * Then, it iterates over all roles in the system (from RolesRepo) and for each role,
     * prompts for the number of employees required. For each role, an empty ArrayList
     * is created with the given initial capacity (note: capacity is not the list size).
     * The resulting mapping is passed into the Shift constructor.
     * Finally, the new shift is added to the ShiftsRepo.
     *
     * @param scanner A Scanner object for user input.
     */
    public void createShift(Scanner scanner) {
        try {
            // Automatically generate shift ID based on number of existing shifts.
            String shiftId = "SHIFT" + (ShiftsRepo.getInstance().getShifts().size() + 1);
            System.out.println("Generating Shift ID: " + shiftId);

            System.out.println("Enter shift date (format yyyy-MM-dd):");
            String dateStr = scanner.nextLine();
            Date shiftDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);

            System.out.println("Enter shift type (1 for Morning, 2 for Evening):");
            int typeChoice = scanner.nextInt();
            scanner.nextLine();
            Shift.ShiftTime shiftType;
            if (typeChoice == 1) {
                shiftType = Shift.ShiftTime.Morning;
            } else if (typeChoice == 2) {
                shiftType = Shift.ShiftTime.Evening;
            } else {
                System.out.println("Invalid shift type; defaulting to Morning.");
                shiftType = Shift.ShiftTime.Morning;
            }

            // Build the mapping for required roles.
            Map<Role, List<Employee>> requiredRoles = new HashMap<>();
            Map<Role, Integer> requiredCounts = new HashMap<>();
            List<Role> roles = RolesRepo.getInstance().getRoles();
            for (Role role : roles) {
                System.out.println("Enter the number of required employees for role: " + role.getName());
                int requiredCount = scanner.nextInt();
                scanner.nextLine();
                requiredCounts.put(role, requiredCount);
                // Initialize an empty ArrayList for employee assignments.
                requiredRoles.put(role, new ArrayList<>());
            }

            // Create the new shift using the automatically generated ID.
            Shift newShift = new Shift(shiftId, shiftDate, shiftType, requiredRoles, requiredCounts);
            ShiftsRepo.getInstance().addShift(newShift);
            System.out.println("Shift created successfully: " + newShift.getID());
        } catch (ParseException e) {
            System.out.println("Invalid date format. Shift creation aborted.");
        }
    }



    public void managerMainMenu(Scanner scanner) {
        boolean exit = false;
        while (!exit) {
            System.out.println("Manager Menu:");
            System.out.println("1. Add Employee");
            System.out.println("2. Remove Employee");
            System.out.println("3. Update Employee Data");
            System.out.println("4. Add New Role");
            System.out.println("5. Remove Role");
            System.out.println("6. Assign Employee to Shift");
            System.out.println("7. Process Swap Requests");
            System.out.println("8. Create Shift");
            System.out.println("9. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addEmployee(scanner, employees);
                    break;
                case 2:
                    removeEmployee(scanner, employees);
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
                case 6:
                    assignEmployeeToShift(scanner, employees, shifts);
                    break;
                case 7:
                    processSwapRequests(scanner);
                    break;
                case 8:
                    createShift(scanner);
                    break;
                case 9:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    public void setCurrentUserRole(Role selectedRole) {
        this.currentUserRole = selectedRole;
    }

}
