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


    public HRInterface(String currentUserId) {
        this.currentUserId = currentUserId;
    }


    private void assignEmployeeToShift(Scanner scanner) {
        if (!currentUserRole.equals(HR_ROLE)) {
            ConsoleUtils.typewriterPrint("Access Denied: Only HR or Shift Manager can assign employees to shifts.", 20);
            return;
        }

        ShiftsRepo repo = ShiftsRepo.getInstance();
        repo.ensureUpToDate();
        List<Shift> shifts = repo.getCurrentWeekShifts();
        List<Employee> employees = EmployeesRepo.getInstance().getEmployees();

        if (shifts.isEmpty()) {
            ConsoleUtils.typewriterPrint("No shifts scheduled for this week.", 20);
            return;
        }

        ConsoleUtils.typewriterPrint("Available Shifts:", 20);
        for (int i = 0; i < shifts.size(); i++) {
            Shift s = shifts.get(i);
            ConsoleUtils.typewriterPrint((i + 1) + ". " + s.getID() + " on " + new SimpleDateFormat("yyyy-MM-dd").format(s.getDate()), 20);
        }
        ConsoleUtils.typewriterPrint("Select shift:", 20);
        int shiftIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (shiftIndex < 0 || shiftIndex >= shifts.size()) {
            ConsoleUtils.typewriterPrint("Invalid shift selection.", 20);
            return;
        }
        Shift shift = shifts.get(shiftIndex);

        // Begin a loop to repeatedly assign employees.
        boolean exitLoop = false;
        while (!exitLoop) {
            // Check if all roles are complete.
            boolean allComplete = true;
            Map<Role, Integer> requiredCounts = shift.getRequiredCounts();
            Map<Role, ArrayList<Employee>> assignedMap = shift.getRequiredRoles();
            for (Role role : requiredCounts.keySet()) {
                int required = requiredCounts.get(role);
                int assigned = (assignedMap.get(role) != null) ? assignedMap.get(role).size() : 0;
                if (assigned < required) {
                    allComplete = false;
                    break;
                }
            }
            if (allComplete) {
                ConsoleUtils.typewriterPrint("All roles for shift " + shift.getID() + " have been fully assigned.", 20
);
                break;
            }

            // Display the current status for each role.
            ConsoleUtils.typewriterPrint("\nRole assignment status for shift " + shift.getID() + ":", 20
);
            List<Role> rolesList = new ArrayList<>(requiredCounts.keySet());
            for (int i = 0; i < rolesList.size(); i++) {
                Role role = rolesList.get(i);
                int required = requiredCounts.get(role);
                int assigned = (assignedMap.get(role) != null) ? assignedMap.get(role).size() : 0;
                int missing = required - assigned;
                String status = (missing == 0) ? "Full" : "Missing: " + missing;
                ConsoleUtils.typewriterPrint((i + 1) + ". " + role.getName() + " (" + status + ")", 20
);
            }
            ConsoleUtils.typewriterPrint("0. Quit assignment for this shift", 20
);

            ConsoleUtils.typewriterPrint("Enter the number corresponding to the role you want to fill:", 20
);
            int roleChoice = scanner.nextInt() - 1;
            scanner.nextLine();
            if (roleChoice == -1) {  // User entered 0.
                exitLoop = true;
                break;
            }
            if (roleChoice < 0 || roleChoice >= rolesList.size()) {
                ConsoleUtils.typewriterPrint("Invalid role selection.", 20
);
                continue;
            }

            Role selectedRole = rolesList.get(roleChoice);
            // Check if the selected role still needs employees.
            int required = requiredCounts.get(selectedRole);
            int assigned = (assignedMap.get(selectedRole) != null) ? assignedMap.get(selectedRole).size() : 0;
            if (assigned >= required) {
                ConsoleUtils.typewriterPrint("This role has already been fully assigned.", 20
);
                continue;
            }

            // Filter for qualified employees:
            // They must have the selected role,
            // be available (either their availability list is empty or it contains an entry matching the shift),
            // and not be on vacation on the shift’s date.
            List<Employee> qualifiedEmployees = new ArrayList<>();
            for (Employee e : employees) {
                if (e.getRoles().contains(selectedRole)) {
                    // Skip employee if already assigned to this shift.
                    boolean alreadyAssigned = false;
                    for (ShiftAssignment sa : shift.getAssignedEmployees()) {
                        if (sa.getEmployeeId().equals(e.getId())) {
                            alreadyAssigned = true;
                            break;
                        }
                    }
                    if (alreadyAssigned) {
                        continue;
                    }

                    // Check if the employee is on vacation on the shift date.
                    boolean onVacation = false;
                    if (e.getHolidays() != null) {
                        for (Date vacDate : e.getHolidays()) {
                            if (vacDate.equals(shift.getDate())) {
                                onVacation = true;
                                break;
                            }
                        }
                    }
                    if (onVacation) {
                        continue;
                    }

                    // Check availability.
                    if (e.getAvailabilityThisWeek().isEmpty() || e.isAvailable(shift.getDate(), shift.getType())) {
                        qualifiedEmployees.add(e);
                    }

                }
            }

            if (qualifiedEmployees.isEmpty()) {
                ConsoleUtils.typewriterPrint("No qualified employees available for role " + selectedRole.getName(), 20
);
                continue;
            }

            // Display the qualified employees.
            ConsoleUtils.typewriterPrint("Qualified Employees for role " + selectedRole.getName() + ":", 20
);
            for (int i = 0; i < qualifiedEmployees.size(); i++) {
                ConsoleUtils.typewriterPrint((i + 1) + ". " + qualifiedEmployees.get(i).getName(), 20
);
            }
            ConsoleUtils.typewriterPrint("Select employee to assign:", 20
);
            int employeeIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            if (employeeIndex < 0 || employeeIndex >= qualifiedEmployees.size()) {
                ConsoleUtils.typewriterPrint("Invalid employee selection.", 20
);
                continue;
            }

            Employee selectedEmployee = qualifiedEmployees.get(employeeIndex);
            shift.assignEmployee(selectedEmployee, selectedRole);
            System.out.println("Employee " + selectedEmployee.getName() + " assigned to role " +
                    selectedRole.getName() + " in shift " + shift.getID());
        }

        // After the assignment loop, output the final completion status.
        ConsoleUtils.typewriterPrint("\nFinal shift completion status for shift " + shift.getID() + ":", 20
);
        Map<Role, Integer> finalRequiredCounts = shift.getRequiredCounts();
        Map<Role, ArrayList<Employee>> finalAssignedMap = shift.getRequiredRoles();
        for (Role role : finalRequiredCounts.keySet()) {
            int req = finalRequiredCounts.get(role);
            int asg = (finalAssignedMap.get(role) != null) ? finalAssignedMap.get(role).size() : 0;
            int missing = req - asg;
            String status = (missing == 0) ? "Full" : "Missing " + missing + " employee(s).";
            ConsoleUtils.typewriterPrint("Role " + role.getName() + ": " + status, 20
);
        }
    }



    public void addNewRole(Scanner scanner) {
        if (!currentUserRole.equals(HR_ROLE)) {
            ConsoleUtils.typewriterPrint("Access Denied: Only HR or Shift Manager can add new roles.", 20
);
            return;
        }
        RolesRepo rolesRepo = RolesRepo.getInstance();
        ConsoleUtils.typewriterPrint("Enter new role name:", 20
);
        String newRoleName = scanner.nextLine();
        rolesRepo.addRole(new Role(newRoleName));
        ConsoleUtils.typewriterPrint("New Role Added Successfully!", 20
);
    }
    public void updateEmployeeData(Scanner scanner, List<Employee> employees) {
        if (!currentUserRole.equals(HR_ROLE)) {
            ConsoleUtils.typewriterPrint("Access Denied: Only HR Manager can update employee data.", 20
);
            return;
        }

        ConsoleUtils.typewriterPrint("Select Employee to Update:", 20
);
        for (int i = 0; i < employees.size(); i++) {
            ConsoleUtils.typewriterPrint(i + 1 + ". " + employees.get(i).getName(), 20
);
        }

        int employeeIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        Employee employee = employees.get(employeeIndex);

        boolean exit = false;
        while (!exit) {
            ConsoleUtils.typewriterPrint("Select data to update:", 20
);
            ConsoleUtils.typewriterPrint("1. Bank Account", 20
);
            ConsoleUtils.typewriterPrint("2. Salary", 20
);
            ConsoleUtils.typewriterPrint("3. Exit", 20
);

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    ConsoleUtils.typewriterPrint("Enter new bank account:", 20
);
                    String newBankAccount = scanner.nextLine();
                    employee.setBankAccount(newBankAccount);
                    ConsoleUtils.typewriterPrint("Bank account updated successfully!", 20
);
                    break;
                case 2:
                    ConsoleUtils.typewriterPrint("Enter new salary:", 20
);
                    float newSalary = scanner.nextFloat();
                    scanner.nextLine();
                    employee.setSalary(newSalary);
                    ConsoleUtils.typewriterPrint("Salary updated successfully!", 20
);
                    break;
                case 3:
                    exit = true;
                    break;
                default:
                    ConsoleUtils.typewriterPrint("Invalid choice. Please try again.", 20
);
                    break;
            }
        }
    }

    public void removeRole(Scanner scanner) {
        if (!currentUserRole.equals(HR_ROLE)) {
            ConsoleUtils.typewriterPrint("Access Denied: Only HR or Shift Manager can remove roles.", 20
);
            return;
        }
        RolesRepo rolesRepo = RolesRepo.getInstance();
        List<Role> roles = rolesRepo.getRoles();
        if (roles.isEmpty()) {
            ConsoleUtils.typewriterPrint("No roles available to remove.", 20
);
            return;
        }
        ConsoleUtils.typewriterPrint("Available Roles:", 20
);
        for (int i = 0; i < roles.size(); i++) {
            ConsoleUtils.typewriterPrint((i + 1) + ": " + roles.get(i).getName(), 20
);
        }
        ConsoleUtils.typewriterPrint("Select role to remove (enter the number):", 20
);
        int roleIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (roleIndex < 0 || roleIndex >= roles.size()) {
            ConsoleUtils.typewriterPrint("Invalid selection.", 20
);
            return;
        }
        Role roleToRemove = roles.get(roleIndex);
        rolesRepo.getRoles().remove(roleToRemove);
        ConsoleUtils.typewriterPrint("Role " + roleToRemove.getName() + " removed successfully.", 20
);
    }

    // New method: AddEmployee
    public void addEmployee(Scanner scanner, List<Employee> employees) {
        if (!currentUserRole.equals(HR_ROLE)) {
            ConsoleUtils.typewriterPrint("Access Denied: Only HR Manager can add employees.", 20
);
            return;
        }

        ConsoleUtils.typewriterPrint("Enter Employee ID:", 20
);
        String id = scanner.nextLine();
        ConsoleUtils.typewriterPrint("Enter Employee Name:", 20
);
        String name = scanner.nextLine();
        ConsoleUtils.typewriterPrint("Enter Password:", 20
);
        String password = scanner.nextLine();
        ConsoleUtils.typewriterPrint("Enter Bank Account:", 20
);
        String bankAccount = scanner.nextLine();
        ConsoleUtils.typewriterPrint("Enter Salary:", 20
);
        float salary = scanner.nextFloat();
        scanner.nextLine();
        ConsoleUtils.typewriterPrint("Enter Employment Date (format yyyy-MM-dd):", 20
);
        String dateStr = scanner.nextLine();
        Date employmentDate = null;
        try {
            employmentDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            ConsoleUtils.typewriterPrint("Invalid date format. Employee not added.", 20
);
            return;
        }

        // Prompt for the employee's role.
        ConsoleUtils.typewriterPrint("Enter Role for the Employee:", 20
);
        String roleName = scanner.nextLine();
        Role role = RolesRepo.getInstance().getRoleByName(roleName);
        List<Role> rolesList = new ArrayList<>();
        if (role != null) {
            rolesList.add(role);
        } else {
            ConsoleUtils.typewriterPrint("Role not found. Adding employee without any role.", 20
);
        }

        Employee newEmployee = new Employee(id, rolesList, name, password, bankAccount, salary, employmentDate);

        try {
            employees.add(newEmployee);
        } catch (UnsupportedOperationException ex) {
            // If the list is unmodifiable, wrap it in a new modifiable ArrayList and update the reference.
            List<Employee> modifiableEmployees = new ArrayList<>(employees);
            modifiableEmployees.add(newEmployee);
            // If possible, update the original reference or notify the caller about this change.
            ConsoleUtils.typewriterPrint("The employees list was unmodifiable. Created a new modifiable list with the new employee.", 20
);
            // Depending on your application architecture, you might then propagate this new list.
        }

        ConsoleUtils.typewriterPrint("Employee " + name + " added successfully.", 20
);
    }
    // New method: RemoveEmployee
    public void removeEmployee(Scanner scanner, List<Employee> employees) {
        if (!currentUserRole.equals(HR_ROLE)) {
            ConsoleUtils.typewriterPrint("Access Denied: Only HR Manager can remove employees.", 20
);
            return;
        }
        if (employees.isEmpty()) {
            ConsoleUtils.typewriterPrint("No employees available.", 20
);
            return;
        }
        ConsoleUtils.typewriterPrint("Select Employee to Remove:", 20
);
        for (int i = 0; i < employees.size(); i++) {
            ConsoleUtils.typewriterPrint((i + 1) + ". " + employees.get(i).getName(), 20
);
        }
        int index = scanner.nextInt() - 1;
        scanner.nextLine();
        if (index < 0 || index >= employees.size()) {
            ConsoleUtils.typewriterPrint("Invalid selection.", 20
);
            return;
        }
        ConsoleUtils.typewriterPrint("Are you sure you want to remove " + employees.get(index).getName() + "? (yes/no)", 20
);
        String confirmation = scanner.nextLine();
        if (!confirmation.equalsIgnoreCase("yes")) {
            ConsoleUtils.typewriterPrint("Employee removal cancelled.", 20
);
            return;
        }
        Employee employee = employees.get(index);
        employees.remove(index);
        ConsoleUtils.typewriterPrint("Employee " + employee.getName() + " removed successfully.", 20
);
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
        Map<Role, ArrayList<Employee>> rolesMap1 = shift1.getRequiredRoles();
        Map<Role, ArrayList<Employee>> rolesMap2 = shift2.getRequiredRoles();

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
    /** Instead of creating a new Shift, pick one of this/next week and set its required roles. */

    public void configureShiftRoles(Scanner scanner) {
        if (!currentUserRole.equals(HR_ROLE)) {
            ConsoleUtils.typewriterPrint("Access Denied: Only HR can configure shift roles.", 20);
            return;
        }

        ShiftsRepo repo = ShiftsRepo.getInstance();
        repo.ensureUpToDate();

        ConsoleUtils.typewriterPrint("Configure roles for: 1) Current week   2) Next week", 20);
        int wk = scanner.nextInt(); scanner.nextLine();

        List<Shift> weekShifts = (wk == 1)
                ? repo.getCurrentWeekShifts()
                : repo.getNextWeekShifts();

        if (weekShifts.isEmpty()) {
            ConsoleUtils.typewriterPrint("No shifts available to configure.", 20);
            return;
        }

        weekShifts.sort(Comparator.comparing(Shift::getDate));
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < weekShifts.size(); i++) {
            Shift s = weekShifts.get(i);
            ConsoleUtils.typewriterPrint(
                    String.format("%d) %s on %s (%s)",
                            i+1,
                            s.getID(),
                            fmt.format(s.getDate()),
                            s.getType()
                    ), 20
            );
        }
        ConsoleUtils.typewriterPrint("0) Exit", 20);
        ConsoleUtils.typewriterPrint("Select shift to configure: ", 20);
        int idx = scanner.nextInt() - 1; scanner.nextLine();
        if (idx < 0 || idx >= weekShifts.size()) {
            ConsoleUtils.typewriterPrint("Exiting configuration.", 20);
            return;
        }
        Shift shift = weekShifts.get(idx);

        Role managerRole = RolesRepo.getInstance().getRoleByName("Shift Manager");
        shift.getRequiredCounts().put(managerRole, 1);
        shift.getRequiredRoles().putIfAbsent(managerRole, new ArrayList<>());

        for (Role role : RolesRepo.getInstance().getRoles()) {
            if (role.equals(HR_ROLE) || role.equals(managerRole)) continue;
            int current = shift.getRequiredCounts().getOrDefault(role, 0);
            ConsoleUtils.typewriterPrint(
                    String.format("Required # for role %s (currently %d):", role.getName(), current),
                    20
            );
            int cnt = scanner.nextInt(); scanner.nextLine();
            shift.getRequiredCounts().put(role, cnt);
            shift.getRequiredRoles().put(role, new ArrayList<>(cnt));
        }

        ConsoleUtils.typewriterPrint("Shift roles updated successfully.", 20);
    }



    /**
     * Processes two swap requests and updates both shifts' assignedEmployees lists.
     */
    public void processSwapRequests(Scanner scanner) {
        if (swapRequests.isEmpty()) {
            ConsoleUtils.typewriterPrint("No swap requests available.", 20
);
            return;
        }

        ConsoleUtils.typewriterPrint("Current Swap Requests:", 20
);
        for (int i = 0; i < swapRequests.size(); i++) {
            ConsoleUtils.typewriterPrint((i+1) + ". " + swapRequests.get(i), 20
);
        }
        ConsoleUtils.typewriterPrint("Select a swap request to process: ", 20
);
        int first = scanner.nextInt() - 1; scanner.nextLine();
        if (first < 0 || first >= swapRequests.size()) {
            ConsoleUtils.typewriterPrint("Invalid selection.", 20
);
            return;
        }
        SwapRequest req1 = swapRequests.get(first);

        // Find compatible requests
        List<SwapRequest> compat = new ArrayList<>();
        for (SwapRequest r : swapRequests) {
            if (r == req1) continue;
            if (!r.getEmployee().equals(req1.getEmployee())
                    && !r.getShift().equals(req1.getShift())
                    && r.getRole().equals(req1.getRole())) {
                compat.add(r);
            }
        }
        if (compat.isEmpty()) {
            ConsoleUtils.typewriterPrint("No compatible swap requests found.", 20
);
            return;
        }

        ConsoleUtils.typewriterPrint("Compatible Swap Requests:", 20
);
        for (int i = 0; i < compat.size(); i++) {
            ConsoleUtils.typewriterPrint((i+1) + ". " + compat.get(i), 20
);
        }
        ConsoleUtils.typewriterPrint("Select one to swap with: ", 20
);
        int second = scanner.nextInt() - 1; scanner.nextLine();
        if (second < 0 || second >= compat.size()) {
            ConsoleUtils.typewriterPrint("Invalid selection.", 20
);
            return;
        }
        SwapRequest req2 = compat.get(second);

        Employee e1 = req1.getEmployee();
        Employee e2 = req2.getEmployee();
        Shift  s1 = req1.getShift();
        Shift  s2 = req2.getShift();
        Role   r  = req1.getRole();

        // Update assignedEmployees lists:
        s1.getAssignedEmployees().removeIf(sa -> sa.getEmployeeId().equals(e1.getId()) && sa.getRole().equals(r));
        s2.getAssignedEmployees().removeIf(sa -> sa.getEmployeeId().equals(e2.getId()) && sa.getRole().equals(r));
        s1.assignEmployee(e2, r);
        s2.assignEmployee(e1, r);

        System.out.printf("Swapped %s and %s for role %s between shifts %s and %s.%n",
                e1.getName(), e2.getName(), r.getName(), s1.getID(), s2.getID());

        // Remove processed requests
        swapRequests.remove(req1);
        swapRequests.remove(req2);
    }



    public void managerMainMenu(Scanner scanner) {
        boolean exit = false;
        while (!exit) {
            ConsoleUtils.typewriterPrint("Manager Menu:", 20
);
            ConsoleUtils.typewriterPrint("1. Add Employee", 20
);
            ConsoleUtils.typewriterPrint("2. Remove Employee", 20
);
            ConsoleUtils.typewriterPrint("3. Update Employee Data", 20
);
            ConsoleUtils.typewriterPrint("4. Add New Role", 20
);
            ConsoleUtils.typewriterPrint("5. Remove Role", 20
);
            ConsoleUtils.typewriterPrint("6. Assign Employee to Shift", 20
);
            ConsoleUtils.typewriterPrint("7. Process Swap Requests", 20
);
            ConsoleUtils.typewriterPrint("8. Set Roles For Shift", 20
);
            ConsoleUtils.typewriterPrint("9. Exit", 20
);

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addEmployee(scanner, employees);
                case 2 -> removeEmployee(scanner, employees);
                case 3 -> updateEmployeeData(scanner, employees);
                case 4 -> addNewRole(scanner);
                case 5 -> removeRole(scanner);
                case 6 -> assignEmployeeToShift(scanner);
                case 7 -> processSwapRequests(scanner);
                case 8 -> configureShiftRoles(scanner);
                case 9 -> exit = true;

            }
        }
    }

    public void setCurrentUserRole(Role selectedRole) {
        this.currentUserRole = selectedRole;
    }

}
