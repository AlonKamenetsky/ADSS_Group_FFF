package HR.Presentation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import HR.Domain.*;
import HR.Service.EmployeeService;
import HR.Service.RoleService;
import HR.Service.ShiftService;
import HR.Service.SwapService;

public class HRInterface {
    private final String currentUserId;
    private Role currentUserRole;
    private final ShiftService shiftService = ShiftService.getInstance();
    private final SwapService swapService = SwapService.getInstance();
    private final EmployeeService employeeService = EmployeeService.getInstance();
    private final RoleService roleService = RoleService.getInstance();
    private final Role HR_ROLE = roleService.getRoleByName("HR");
    private final Employee  currentUser;


    public HRInterface(String currentUserId) {
        this.currentUserId = currentUserId;
        this.currentUser = employeeService.getEmployeeById(currentUserId);
    }


    private void assignEmployeeToShift(Scanner scanner) {
        if (!currentUserRole.equals(HR_ROLE)) {
            PresentationUtils.typewriterPrint("Access Denied: Only HR or Shift Manager can assign employees to shifts.", 20);
            return;
        }

//        shiftService.ensureShiftsRepoUpToDate();
        List<Shift> shifts = shiftService.getCurrentWeekShifts();
        List<Employee> employees = employeeService.getEmployees();

        if (shifts.isEmpty()) {
            PresentationUtils.typewriterPrint("No shifts scheduled for this week.", 20);
            return;
        }

        PresentationUtils.typewriterPrint("Available Shifts:", 20);
        for (int i = 0; i < shifts.size(); i++) {
            Shift s = shifts.get(i);
            PresentationUtils.typewriterPrint((i + 1) + ". " + s.getID() + " on " + new SimpleDateFormat("yyyy-MM-dd").format(s.getDate()), 20);
        }
        PresentationUtils.typewriterPrint("Select shift:", 20);
        int shiftIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (shiftIndex < 0 || shiftIndex >= shifts.size()) {
            PresentationUtils.typewriterPrint("Invalid shift selection.", 20);
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
                PresentationUtils.typewriterPrint("All roles for shift " + shift.getID() + " have been fully assigned.", 20
);
                break;
            }

            // Display the current status for each role.
            PresentationUtils.typewriterPrint("\nRole assignment status for shift " + shift.getID() + ":", 20
);
            List<Role> rolesList = new ArrayList<>(requiredCounts.keySet());
            for (int i = 0; i < rolesList.size(); i++) {
                Role role = rolesList.get(i);
                int required = requiredCounts.get(role);
                int assigned = (assignedMap.get(role) != null) ? assignedMap.get(role).size() : 0;
                int missing = required - assigned;
                String status = (missing == 0) ? "Full" : "Missing: " + missing;
                PresentationUtils.typewriterPrint((i + 1) + ". " + role.getName() + " (" + status + ")", 20
);
            }
            PresentationUtils.typewriterPrint("0. Quit assignment for this shift", 20
);

            PresentationUtils.typewriterPrint("Enter the number corresponding to the role you want to fill:", 20
);
            int roleChoice = scanner.nextInt() - 1;
            scanner.nextLine();
            if (roleChoice == -1) {  // User entered 0.
                exitLoop = true;
                break;
            }
            if (roleChoice < 0 || roleChoice >= rolesList.size()) {
                PresentationUtils.typewriterPrint("Invalid role selection.", 20
);
                continue;
            }

            Role selectedRole = rolesList.get(roleChoice);
            // Check if the selected role still needs employees.
            int required = requiredCounts.get(selectedRole);
            int assigned = (assignedMap.get(selectedRole) != null) ? assignedMap.get(selectedRole).size() : 0;
            if (assigned >= required) {
                PresentationUtils.typewriterPrint("This role has already been fully assigned.", 20
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
                PresentationUtils.typewriterPrint("No qualified employees available for role " + selectedRole.getName(), 20
);
                continue;
            }

            // Display the qualified employees.
            PresentationUtils.typewriterPrint("Qualified Employees for role " + selectedRole.getName() + ":", 20
);
            for (int i = 0; i < qualifiedEmployees.size(); i++) {
                PresentationUtils.typewriterPrint((i + 1) + ". " + qualifiedEmployees.get(i).getName(), 20
);
            }
            PresentationUtils.typewriterPrint("Select employee to assign:", 20
);
            int employeeIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            if (employeeIndex < 0 || employeeIndex >= qualifiedEmployees.size()) {
                PresentationUtils.typewriterPrint("Invalid employee selection.", 20
);
                continue;
            }

            Employee selectedEmployee = qualifiedEmployees.get(employeeIndex);
            shiftService.AssignEmployeeToShift(shift,selectedEmployee,selectedRole);
        }

        // After the assignment loop, output the final completion status.
        PresentationUtils.typewriterPrint("\nFinal shift completion status for shift " + shift.getID() + ":", 20
);
        Map<Role, Integer> finalRequiredCounts = shift.getRequiredCounts();
        Map<Role, ArrayList<Employee>> finalAssignedMap = shift.getRequiredRoles();
        for (Role role : finalRequiredCounts.keySet()) {
            int req = finalRequiredCounts.get(role);
            int asg = (finalAssignedMap.get(role) != null) ? finalAssignedMap.get(role).size() : 0;
            int missing = req - asg;
            String status = (missing == 0) ? "Full" : "Missing " + missing + " employee(s).";
            PresentationUtils.typewriterPrint("Role " + role.getName() + ": " + status, 20
);
        }
    }



    public void addNewRole(Scanner scanner) {
        if (!currentUserRole.equals(HR_ROLE)) {
            PresentationUtils.typewriterPrint("Access Denied: Only HR or Shift Manager can add new roles.", 20
);
            return;
        }
        PresentationUtils.typewriterPrint("Enter new role name:", 20
);
        String newRoleName = scanner.nextLine();
        roleService.addRole(newRoleName);
        PresentationUtils.typewriterPrint("New Role Added Successfully!", 20
);
    }
    public void updateEmployeeData(Scanner scanner) {
        if (!currentUserRole.equals(HR_ROLE)) {
            PresentationUtils.typewriterPrint("Access Denied: Only HR Manager can update employee data.", 20
);
            return;
        }

        PresentationUtils.typewriterPrint("Enter Employee's ID to Update:", 20
);
        String input = scanner.nextLine().trim();
        Employee employee = employeeService.getEmployeeById(input);

        boolean exit = false;
        while (!exit) {
            PresentationUtils.typewriterPrint("Select data to update:", 20
);
            PresentationUtils.typewriterPrint("1. Bank Account", 20
);
            PresentationUtils.typewriterPrint("2. Salary", 20
);
            PresentationUtils.typewriterPrint("3. Update Licenses (for drivers only)", 20
);
            PresentationUtils.typewriterPrint("4. Exit", 20
);

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    PresentationUtils.typewriterPrint("Enter new bank account:", 20
);
                    String newBankAccount = scanner.nextLine();
                    employeeService.setBankAccount(employee,newBankAccount);
                    PresentationUtils.typewriterPrint("Bank account updated successfully!", 20
);
                    break;
                case 2:
                    PresentationUtils.typewriterPrint("Enter new salary:", 20
);
                    float newSalary = scanner.nextFloat();
                    scanner.nextLine();
                    employeeService.setSalary(employee,newSalary);
                    PresentationUtils.typewriterPrint("Salary updated successfully!", 20
);
                    break;
                case 3:
                    if (!employee.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("Driver"))) {
                        PresentationUtils.typewriterPrint("This employee is not a driver. Cannot update licenses.", 20);
                        break;}
                    // Assume 'employee' is the selected Employee and is a driver
                    List<DriverInfo.LicenseType> allTypes = Arrays.asList(DriverInfo.LicenseType.values());
                    Set<DriverInfo.LicenseType> currentLicenses = new HashSet<>(employeeService.getDriverLicenses(employee));

                    while (true) {
                        PresentationUtils.typewriterPrint("Driver Licenses (toggle selection):", 20);
                        for (int i = 0; i < allTypes.size(); i++) {
                            DriverInfo.LicenseType type = allTypes.get(i);
                            boolean has = currentLicenses.contains(type);
                            String mark = has ? "[X]" : "[ ]";
                            PresentationUtils.typewriterPrint((i + 1) + ") " + mark + " " + type.name(), 20);
                        }
                        PresentationUtils.typewriterPrint("Enter number to toggle license, or 0 to finish:", 20);

                        String _input = scanner.nextLine().trim();
                        int num;
                        try {
                            num = Integer.parseInt(_input);
                        } catch (NumberFormatException e) {
                            PresentationUtils.typewriterPrint("Invalid input. Please enter a number.", 20);
                            continue;
                        }
                        if (num == 0) break;
                        if (num < 1 || num > allTypes.size()) {
                            PresentationUtils.typewriterPrint("Invalid selection.", 20);
                            continue;
                        }
                        DriverInfo.LicenseType selected = allTypes.get(num - 1);
                        if (currentLicenses.contains(selected)) {
                            currentLicenses.remove(selected);
                            PresentationUtils.typewriterPrint("Removed license: " + selected, 20);
                        } else {
                            currentLicenses.add(selected);
                            PresentationUtils.typewriterPrint("Added license: " + selected, 20);
                        }
                    }

                    // Update the employee's licenses
                    employeeService.setDriverLicenses(employee, new ArrayList<>(currentLicenses));
                    PresentationUtils.typewriterPrint("Driver licenses updated.", 20);
                case 4:
                    exit = true;
                    break;
                default:
                    PresentationUtils.typewriterPrint("Invalid choice. Please try again.", 20
);
                    break;
            }
        }
    }

    public void removeRole(Scanner scanner) {
        if (!currentUserRole.equals(HR_ROLE)) {
            PresentationUtils.typewriterPrint("Access Denied: Only HR or Shift Manager can remove roles.", 20
            );
            return;
        }
        List<Role> roles = roleService.getRoles();
        if (roles.isEmpty()) {
            PresentationUtils.typewriterPrint("No roles available to remove.", 20
            );
            return;
        }
        PresentationUtils.typewriterPrint("Available Roles:", 20
        );
        for (int i = 0; i < roles.size(); i++) {
            PresentationUtils.typewriterPrint((i + 1) + ": " + roles.get(i).getName(), 20
            );
        }
        PresentationUtils.typewriterPrint("Select role to remove (enter the number):", 20
        );
        int roleIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (roleIndex < 0 || roleIndex >= roles.size()) {
            PresentationUtils.typewriterPrint("Invalid selection.", 20
            );
            return;
        }
        Role roleToRemove = roles.get(roleIndex);
        PresentationUtils.typewriterPrint("Are you sure you want to remove the role '" + roleToRemove.getName() + "'? (yes/no)", 20
        );
        String confirmation = scanner.nextLine();
        if (!confirmation.equalsIgnoreCase("yes")) {
            PresentationUtils.typewriterPrint("Role removal cancelled.", 20);
            return;
        }
        roleService.removeRole(roleToRemove);
    }

    // New method: AddEmployee
    public void addEmployee(Scanner scanner) {
        if (!currentUserRole.equals(HR_ROLE)) {
            PresentationUtils.typewriterPrint("Access Denied: Only HR Manager can add employees.", 20
);
            return;
        }

        PresentationUtils.typewriterPrint("Enter Employee ID:", 20
);
        String id = scanner.nextLine();
        PresentationUtils.typewriterPrint("Enter Employee Name:", 20
);
        String name = scanner.nextLine();
        PresentationUtils.typewriterPrint("Enter Password:", 20
);
        String password = scanner.nextLine();
        PresentationUtils.typewriterPrint("Enter Bank Account:", 20
);
        String bankAccount = scanner.nextLine();
        PresentationUtils.typewriterPrint("Enter Salary:", 20
);
        float salary = scanner.nextFloat();
        scanner.nextLine();
        PresentationUtils.typewriterPrint("Enter Employment Date (format yyyy-MM-dd):", 20
);
        String dateStr = scanner.nextLine();
        Date employmentDate = null;
        try {
            employmentDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            PresentationUtils.typewriterPrint("Invalid date format. Employee not added.", 20
);
            return;
        }

        // Show available roles and allow multiple selection
        List<Role> allRoles = roleService.getRoles();
        List<Role> rolesList = new ArrayList<>();
        while (true) {
            PresentationUtils.typewriterPrint("Available Roles:", 20);
            for (int i = 0; i < allRoles.size(); i++) {
                PresentationUtils.typewriterPrint((i + 1) + ". " + allRoles.get(i).getName(), 20);
            }
            PresentationUtils.typewriterPrint("Enter the number of a role to add (0 to finish):", 20);
            String input = scanner.nextLine().trim();
            int roleNum;
            try {
                roleNum = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                PresentationUtils.typewriterPrint("Invalid input. Please enter a number.", 20);
                continue;
            }
            if (roleNum == 0) break;
            if (roleNum < 1 || roleNum > allRoles.size()) {
                PresentationUtils.typewriterPrint("Invalid role number.", 20);
                continue;
            }
            Role selectedRole = allRoles.get(roleNum - 1);
            if (!rolesList.contains(selectedRole)) {
                rolesList.add(selectedRole);
                PresentationUtils.typewriterPrint("Added role: " + selectedRole.getName(), 20);
                } else {
                PresentationUtils.typewriterPrint("Role already added.", 20);
            }
        }
        if (rolesList.isEmpty()) {
            PresentationUtils.typewriterPrint("No roles selected. Adding employee without any role.", 20);
            employeeService.addEmployee(id, rolesList, name, password, bankAccount, salary, employmentDate);
            return;
        }

// Check if "Driver" is one of the selected roles
        boolean isDriver = rolesList.stream().anyMatch(role -> role.getName().equalsIgnoreCase("Driver"));
        if (isDriver) {
            PresentationUtils.typewriterPrint("Driver role selected. Please select license type(s):", 20);
            List<DriverInfo.LicenseType> licenseTypes = new ArrayList<>();
            while (true) {
                PresentationUtils.typewriterPrint("License Type (Truck Size):", 20);
                PresentationUtils.typewriterPrint("1) B (Small)", 20);
                PresentationUtils.typewriterPrint("2) C (Medium)", 20);
                PresentationUtils.typewriterPrint("3) C1 (Large)", 20);
                PresentationUtils.typewriterPrint("Enter the number of a license to add (0 to finish):", 20);

                String input = scanner.nextLine().trim();
                int licenseNum;
                try {
                    licenseNum = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    PresentationUtils.typewriterPrint("Invalid input. Please enter a number.", 20);
                    continue;
                }
                if (licenseNum == 0) break;
                try {
                    DriverInfo.LicenseType licenseType = employeeService.getDriverLicenseType(licenseNum);
                    if (!licenseTypes.contains(licenseType)) {
                        licenseTypes.add(licenseType);
                        PresentationUtils.typewriterPrint("Added license type: " + licenseType, 20);
                    } else {
                        PresentationUtils.typewriterPrint("License type already added.", 20);
                    }
                } catch (IllegalArgumentException e) {
                    PresentationUtils.typewriterPrint("Invalid license type selected.", 20);
                }
            }
            if (licenseTypes.isEmpty()) {
                PresentationUtils.typewriterPrint("Driver must have at least one license type. Employee not added.", 20);
                return;
            }
            employeeService.addEmployee(id, rolesList, name, password, bankAccount, salary, employmentDate, licenseTypes);
        } else {
            employeeService.addEmployee(id, rolesList, name, password, bankAccount, salary, employmentDate);
        }

    }
    // New method: RemoveEmployee
    public void removeEmployee(Scanner scanner) {
        List<Employee> employees = employeeService.getEmployees();
        if (!currentUserRole.equals(HR_ROLE)) {
            PresentationUtils.typewriterPrint("Access Denied: Only HR Manager can remove employees.", 20
);
            return;
        }
        if (employees.isEmpty()) {
            PresentationUtils.typewriterPrint("No employees available.", 20
);
            return;
        }
        PresentationUtils.typewriterPrint("Select Employee to Remove:", 20
);
        for (int i = 0; i < employees.size(); i++) {
            PresentationUtils.typewriterPrint((i + 1) + ". " + employees.get(i).getName(), 20
);
        }
        int index = scanner.nextInt() - 1;
        scanner.nextLine();
        if (index < 0 || index >= employees.size()) {
            PresentationUtils.typewriterPrint("Invalid selection.", 20
);
            return;
        }
        PresentationUtils.typewriterPrint("Are you sure you want to remove " + employees.get(index).getName() + "? (yes/no)", 20
);
        String confirmation = scanner.nextLine();
        if (!confirmation.equalsIgnoreCase("yes")) {
            PresentationUtils.typewriterPrint("Employee removal cancelled.", 20
);
            return;
        }
        Employee employee = employees.get(index);
        employeeService.removeEmployee(employee);
    }


    public void configureShiftRoles(Scanner scanner) {
        if (!currentUserRole.equals(HR_ROLE)) {
            PresentationUtils.typewriterPrint("Access Denied: Only HR can configure shift roles.", 20);
            return;
        }

//        shiftService.ensureShiftsRepoUpToDate();

        PresentationUtils.typewriterPrint("Configure roles for: 1) Current week   2) Next week", 20);
        int wk = scanner.nextInt(); scanner.nextLine();

        List<Shift> weekShifts = (wk == 1)
                ? shiftService.getCurrentWeekShifts()
                : shiftService.getNextWeekShifts();

        if (weekShifts.isEmpty()) {
            PresentationUtils.typewriterPrint("No shifts available to configure.", 20);
            return;
        }

        weekShifts.sort(Comparator.comparing(Shift::getDate));
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < weekShifts.size(); i++) {
            Shift s = weekShifts.get(i);
            PresentationUtils.typewriterPrint(
                    String.format("%d) %s on %s (%s)",
                            i+1,
                            s.getID(),
                            fmt.format(s.getDate()),
                            s.getType()
                    ), 20
            );
        }
        PresentationUtils.typewriterPrint("0) Exit", 20);
        PresentationUtils.typewriterPrint("Select shift to configure: ", 20);
        int idx = scanner.nextInt() - 1; scanner.nextLine();
        if (idx < 0 || idx >= weekShifts.size()) {
            PresentationUtils.typewriterPrint("Exiting configuration.", 20);
            return;
        }

        Shift shift = weekShifts.get(idx);
        List<Role> roles = roleService.getRoles();
        Map<Role, Integer> requiredCounts = new HashMap<>();
        for (Role role : roles) {
            if (role.getName().equals("Shift Manager") || role.getName().equals("HR Manager")) {
                continue;
            }
            PresentationUtils.typewriterPrint(
                    String.format("Required # for role %s:", role.getName()), 20
            );
            int cnt = scanner.nextInt(); scanner.nextLine();
            requiredCounts.put(role, cnt);
        }
        shiftService.ConfigureShiftRoles(shift, requiredCounts);
    }



    /**
     * Processes two swap requests and updates both shifts' assignedEmployees lists.
     */
    public void processSwapRequests(Scanner scanner) {
        List<SwapRequest> swapRequests = swapService.getSwapRequests();
        if (swapRequests.isEmpty()) {
            PresentationUtils.typewriterPrint("No swap requests available.", 20
);
            return;
        }

        PresentationUtils.typewriterPrint("Current Swap Requests:", 20
);
        for (int i = 0; i < swapRequests.size(); i++) {
            PresentationUtils.typewriterPrint((i+1) + ". " + swapRequests.get(i), 20
);
        }
        PresentationUtils.typewriterPrint("Select a swap request to process: ", 20
);
        int first = scanner.nextInt() - 1; scanner.nextLine();
        if (first < 0 || first >= swapRequests.size()) {
            PresentationUtils.typewriterPrint("Invalid selection.", 20
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
            PresentationUtils.typewriterPrint("No compatible swap requests found.", 20
);
            return;
        }

        PresentationUtils.typewriterPrint("Compatible Swap Requests:", 20
);
        for (int i = 0; i < compat.size(); i++) {
            PresentationUtils.typewriterPrint((i+1) + ". " + compat.get(i), 20
);
        }
        PresentationUtils.typewriterPrint("Select one to swap with: ", 20
);
        int second = scanner.nextInt() - 1; scanner.nextLine();
        if (second < 0 || second >= compat.size()) {
            PresentationUtils.typewriterPrint("Invalid selection.", 20
);
            return;
        }
        SwapRequest req2 = compat.get(second);
        swapService.AcceptSwapRequests(req1,req2);

    }



    public void managerMainMenu(Scanner scanner) {
        boolean exit = false;
        while (!exit) {
            PresentationUtils.typewriterPrint("Manager Menu:", 20
);
            PresentationUtils.typewriterPrint("1. View My Info", 20
);
            PresentationUtils.typewriterPrint("2. Add Employee", 20
);
            PresentationUtils.typewriterPrint("3. Remove Employee", 20
);
            PresentationUtils.typewriterPrint("4. Update Employee Data", 20
);
            PresentationUtils.typewriterPrint("5. Add New Role", 20
);
            PresentationUtils.typewriterPrint("6. Remove Role", 20
);
            PresentationUtils.typewriterPrint("7. Assign Employee to Shift", 20
);
            PresentationUtils.typewriterPrint("8. Process Swap Requests", 20
);
            PresentationUtils.typewriterPrint("9. Set Roles For Shift", 20
);
            PresentationUtils.typewriterPrint("10. Exit", 20
);

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> employeeService.ShowInfo(currentUser);
                case 2 -> addEmployee(scanner);
                case 3 -> removeEmployee(scanner);
                case 4 -> updateEmployeeData(scanner);
                case 5 -> addNewRole(scanner);
                case 6 -> removeRole(scanner);
                case 7 -> assignEmployeeToShift(scanner);
                case 8 -> processSwapRequests(scanner);
                case 9 -> configureShiftRoles(scanner);
                case 10 -> exit = true;

            }
        }
    }

    public void setCurrentUserRole(Role selectedRole) {
        this.currentUserRole = selectedRole;
    }

}
