package HR.Presentation;

import HR.DTO.*;
import HR.Service.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;

public class HRInterface {
    private final String currentUserId;
    private final String currentUserRoleName;
    private final EmployeeService employeeService = EmployeeService.getInstance();
    private final RoleService roleService         = RoleService.getInstance();
    private final ShiftService shiftService       = ShiftService.getInstance();
    private final SwapService swapService         = SwapService.getInstance();
    private final String HR_ROLE_NAME             = "HR";

    public HRInterface(String currentUserId) {
        this.currentUserId = currentUserId;
        this.currentUserRoleName = HR_ROLE_NAME;
    }

    public void managerMainMenu(Scanner scanner) {
        boolean exit = false;
        while (!exit) {
            PresentationUtils.typewriterPrint("Manager Menu:", 20);
            PresentationUtils.typewriterPrint("1. View My Info", 20);
            PresentationUtils.typewriterPrint("2. Add Employee", 20);
            PresentationUtils.typewriterPrint("3. Remove Employee", 20);
            PresentationUtils.typewriterPrint("4. Update Employee Data", 20);
            PresentationUtils.typewriterPrint("5. Add New Role", 20);
            PresentationUtils.typewriterPrint("6. Remove Role", 20);
            PresentationUtils.typewriterPrint("7. Assign Employee to Shift", 20);
            PresentationUtils.typewriterPrint("8. Process Swap Requests", 20);
            PresentationUtils.typewriterPrint("9. Configure Shift Roles", 20);
            PresentationUtils.typewriterPrint("10. Exit", 20);

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewMyInfo();
                case 2 -> addEmployee(scanner);
                case 3 -> removeEmployee(scanner);
                case 4 -> updateEmployeeData(scanner);
                case 5 -> addNewRole(scanner);
                case 6 -> removeRole(scanner);
                case 7 -> assignEmployeeToShift(scanner);
                case 8 -> processSwapRequests(scanner);
                case 9 -> configureShiftRoles(scanner);
                case 10 -> exit = true;
                default -> PresentationUtils.typewriterPrint("Invalid choice.", 20);
            }
        }
    }

    public void viewMyInfo() {
        EmployeeDTO dto = employeeService.getEmployeeById(currentUserId);
        if (dto == null) {
            PresentationUtils.typewriterPrint("Employee not found.", 20);
        } else {
            PresentationUtils.printEmployeeDTO(dto);
        }
    }

    public void addNewRole(Scanner scanner) {
        if (!currentUserRoleName.equalsIgnoreCase(HR_ROLE_NAME)) {
            PresentationUtils.typewriterPrint(
                    "Access Denied: Only HR or Shift Manager can add new roles.", 20);
            return;
        }
        PresentationUtils.typewriterPrint("Enter new role name:", 20);
        String newRoleName = scanner.nextLine().trim();
        if (newRoleName.isEmpty()) {
            PresentationUtils.typewriterPrint("Role name cannot be empty.", 20);
            return;
        }
        RoleDTO newRoleDto = new RoleDTO(newRoleName);
        roleService.addRole(newRoleDto);
        PresentationUtils.typewriterPrint("New Role Added Successfully!", 20);
    }

    public void removeRole(Scanner scanner) {
        if (!currentUserRoleName.equalsIgnoreCase(HR_ROLE_NAME)) {
            PresentationUtils.typewriterPrint(
                    "Access Denied: Only HR or Shift Manager can remove roles.", 20);
            return;
        }
        List<RoleDTO> roles = roleService.getRoles();
        if (roles.isEmpty()) {
            PresentationUtils.typewriterPrint("No roles available to remove.", 20);
            return;
        }
        PresentationUtils.typewriterPrint("Available Roles:", 20);
        for (int i = 0; i < roles.size(); i++) {
            PresentationUtils.typewriterPrint((i + 1) + ": " + roles.get(i).getName(), 20);
        }
        PresentationUtils.typewriterPrint("Select role to remove (enter the number):", 20);
        int roleIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (roleIndex < 0 || roleIndex >= roles.size()) {
            PresentationUtils.typewriterPrint("Invalid selection.", 20);
            return;
        }
        RoleDTO roleToRemove = roles.get(roleIndex);
        PresentationUtils.typewriterPrint(
                "Are you sure you want to remove the role '" + roleToRemove.getName() + "'? (yes/no)", 20);
        String confirmation = scanner.nextLine().trim();
        if (!confirmation.equalsIgnoreCase("yes")) {
            PresentationUtils.typewriterPrint("Role removal cancelled.", 20);
            return;
        }
        roleService.removeRole(roleToRemove.getName());
    }

    public void addEmployee(Scanner scanner) {
        if (!currentUserRoleName.equalsIgnoreCase(HR_ROLE_NAME)) {
            PresentationUtils.typewriterPrint(
                    "Access Denied: Only HR Manager can add employees.", 20);
            return;
        }

        PresentationUtils.typewriterPrint("Enter Employee ID:", 20);
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            PresentationUtils.typewriterPrint("ID cannot be empty.", 20);
            return;
        }

        PresentationUtils.typewriterPrint("Enter Employee Name:", 20);
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            PresentationUtils.typewriterPrint("Name cannot be empty.", 20);
            return;
        }

        PresentationUtils.typewriterPrint("Enter Password:", 20);
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) {
            PresentationUtils.typewriterPrint("Password cannot be empty.", 20);
            return;
        }

        PresentationUtils.typewriterPrint("Enter Bank Account:", 20);
        String bankAccount = scanner.nextLine().trim();
        if (bankAccount.isEmpty()) {
            PresentationUtils.typewriterPrint("Bank account cannot be empty.", 20);
            return;
        }

        PresentationUtils.typewriterPrint("Enter Salary:", 20);
        Float salary;
        try {
            salary = Float.valueOf(scanner.nextLine().trim());
            if (salary < 0) {
                PresentationUtils.typewriterPrint("Salary cannot be negative.", 20);
                return;
            }
        } catch (NumberFormatException e) {
            PresentationUtils.typewriterPrint("Invalid salary. Must be a number.", 20);
            return;
        }

        PresentationUtils.typewriterPrint("Enter Employment Date (yyyy-MM-dd):", 20);
        String dateStr = scanner.nextLine().trim();
        Date employmentDate;
        try {
            employmentDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            PresentationUtils.typewriterPrint("Invalid date format. Employee not added.", 20);
            return;
        }

        // Show available roles
        List<RoleDTO> allRolesDto = roleService.getRoles();
        List<RoleDTO> rolesList = new ArrayList<>();
        while (true) {
            PresentationUtils.typewriterPrint("Available Roles:", 20);
            for (int i = 0; i < allRolesDto.size(); i++) {
                PresentationUtils.typewriterPrint((i + 1) + ". " + allRolesDto.get(i).getName(), 20);
            }
            PresentationUtils.typewriterPrint("Enter the number of a role to add (0 to finish):", 20);
            String input = scanner.nextLine().trim();
            int roleNum;
            try {
                roleNum = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                PresentationUtils.typewriterPrint("Invalid input. Please enter a number.", 20);
                continue;
            }
            if (roleNum == 0) break;
            if (roleNum < 1 || roleNum > allRolesDto.size()) {
                PresentationUtils.typewriterPrint("Invalid role number.", 20);
                continue;
            }
            RoleDTO roleDto = allRolesDto.get(roleNum - 1);
            if (!rolesList.contains(roleDto)) {
                rolesList.add(roleDto);
                PresentationUtils.typewriterPrint("Added role: " + roleDto.getName(), 20);
            } else {
                PresentationUtils.typewriterPrint("Role already added.", 20);
            }
        }

        if (rolesList.isEmpty()) {
            PresentationUtils.typewriterPrint("No roles selected. Adding employee without roles.", 20);
            CreateEmployeeDTO baseDto = new CreateEmployeeDTO();
            baseDto.setId(id);
            baseDto.setName(name);
            baseDto.setRawPassword(password);
            baseDto.setBankAccount(bankAccount);
            baseDto.setSalary(salary);
            baseDto.setEmploymentDate(employmentDate);
            baseDto.setRoles(Collections.emptyList());
            employeeService.addEmployee(baseDto);
            employeeService.setPassword(id, password);
            return;
        }

        boolean isDriver = rolesList.stream()
                .anyMatch(rdto -> rdto.getName().equalsIgnoreCase("Driver"));

        if (isDriver) {
            List<String> licenseTypes = new ArrayList<>();
            while (true) {
                PresentationUtils.typewriterPrint("License Type (Truck Size):", 20);
                PresentationUtils.typewriterPrint("1) B (Small)", 20);
                PresentationUtils.typewriterPrint("2) C (Medium)", 20);
                PresentationUtils.typewriterPrint("3) C1 (Large)", 20);
                PresentationUtils.typewriterPrint("Enter the number of a license to add (0 to finish):", 20);

                String licInput = scanner.nextLine().trim();
                int licNum;
                try {
                    licNum = Integer.parseInt(licInput);
                } catch (NumberFormatException ex) {
                    PresentationUtils.typewriterPrint("Invalid input. Please enter a number.", 20);
                    continue;
                }
                if (licNum == 0) break;
                String lt;
                switch (licNum) {
                    case 1 -> lt = "B";
                    case 2 -> lt = "C";
                    case 3 -> lt = "C1";
                    default -> {
                        PresentationUtils.typewriterPrint("Invalid license choice.", 20);
                        continue;
                    }
                }
                if (!licenseTypes.contains(lt)) {
                    licenseTypes.add(lt);
                    PresentationUtils.typewriterPrint("Added license type: " + lt, 20);
                } else {
                    PresentationUtils.typewriterPrint("License type already added.", 20);
                }
            }
            if (licenseTypes.isEmpty()) {
                PresentationUtils.typewriterPrint("Driver must have at least one license type. Employee not added.", 20);
                return;
            }

            CreateEmployeeDTO newDto = new CreateEmployeeDTO();
            newDto.setId(id);
            newDto.setName(name);
            newDto.setRawPassword(password);         // ← set the rawPassword here
            newDto.setBankAccount(bankAccount);
            newDto.setSalary(salary);
            newDto.setEmploymentDate(employmentDate);
            newDto.setRoles(rolesList);

            // Convert List<String> licenseTypes → List<DriverInfo.LicenseType>
            List<HR.Domain.DriverInfo.LicenseType> licenseEnums = new ArrayList<>();
            for (String s : licenseTypes) {
                licenseEnums.add(HR.Domain.DriverInfo.LicenseType.valueOf(s));
            }
            employeeService.addEmployee(newDto, licenseEnums);
            employeeService.setPassword(id, password);
        } else {
            CreateEmployeeDTO newDto = new CreateEmployeeDTO();
            newDto.setId(id);
            newDto.setName(name);
            newDto.setRawPassword(password);         // ← set rawPassword even for non‐driver
            newDto.setBankAccount(bankAccount);
            newDto.setSalary(salary);
            newDto.setEmploymentDate(employmentDate);
            newDto.setRoles(rolesList);
            employeeService.addEmployee(newDto);
            employeeService.setPassword(id, password);
        }
    }

    public void removeEmployee(Scanner scanner) {
        if (!currentUserRoleName.equalsIgnoreCase(HR_ROLE_NAME)) {
            PresentationUtils.typewriterPrint(
                    "Access Denied: Only HR Manager can remove employees.", 20);
            return;
        }
        List<EmployeeDTO> employees = employeeService.getEmployees();
        if (employees.isEmpty()) {
            PresentationUtils.typewriterPrint("No employees available.", 20);
            return;
        }
        PresentationUtils.typewriterPrint("Select Employee to Remove:", 20);
        for (int i = 0; i < employees.size(); i++) {
            PresentationUtils.typewriterPrint((i + 1) + ". " + employees.get(i).getName(), 20);
        }
        int index = scanner.nextInt() - 1;
        scanner.nextLine();
        if (index < 0 || index >= employees.size()) {
            PresentationUtils.typewriterPrint("Invalid selection.", 20);
            return;
        }
        EmployeeDTO toRemove = employees.get(index);
        PresentationUtils.typewriterPrint(
                "Are you sure you want to remove " + toRemove.getName() + "? (yes/no)", 20);
        String confirmation = scanner.nextLine().trim();
        if (!confirmation.equalsIgnoreCase("yes")) {
            PresentationUtils.typewriterPrint("Employee removal cancelled.", 20);
            return;
        }
        employeeService.removeEmployee(toRemove.getId());
    }

    public void updateEmployeeData(Scanner scanner) {
        if (!currentUserRoleName.equalsIgnoreCase(HR_ROLE_NAME)) {
            PresentationUtils.typewriterPrint(
                    "Access Denied: Only HR Manager can update employee data.", 20);
            return;
        }

        PresentationUtils.typewriterPrint("Enter Employee's ID to Update:", 20);
        String input = scanner.nextLine().trim();
        EmployeeDTO employeeDto = employeeService.getEmployeeById(input);
        if (employeeDto == null) {
            PresentationUtils.typewriterPrint("No such employee.", 20);
            return;
        }

        boolean exit = false;
        while (!exit) {
            PresentationUtils.typewriterPrint("Select data to update:", 20);
            PresentationUtils.typewriterPrint("1. Bank Account", 20);
            PresentationUtils.typewriterPrint("2. Salary", 20);
            PresentationUtils.typewriterPrint("3. Update Employee's Roles", 20);
            PresentationUtils.typewriterPrint("4. Update Licenses (for drivers only)", 20);
            PresentationUtils.typewriterPrint("5. Exit", 20);

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    PresentationUtils.typewriterPrint("Enter new bank account:", 20);
                    String newBankAccount = scanner.nextLine().trim();
                    employeeDto.setBankAccount(newBankAccount);
                    employeeService.updateEmployee(employeeDto);
                    PresentationUtils.typewriterPrint("Bank account updated successfully!", 20);
                }
                case 2 -> {
                    PresentationUtils.typewriterPrint("Enter new salary:", 20);
                    float newSalary = scanner.nextFloat();
                    scanner.nextLine();
                    employeeDto.setSalary(newSalary);
                    employeeService.updateEmployee(employeeDto);
                    PresentationUtils.typewriterPrint("Salary updated successfully!", 20);
                }
                case 3 -> {
                    List<RoleDTO> currentRoleNames = employeeDto.getRoles();
                    List<RoleDTO> allRolesDto      = roleService.getRoles();

                    PresentationUtils.typewriterPrint("Current Roles:", 20);
                    for (int i = 0; i < currentRoleNames.size(); i++) {
                        PresentationUtils.typewriterPrint((i + 1) + ". " + currentRoleNames.get(i), 20);
                    }
                    PresentationUtils.typewriterPrint("Available Roles:", 20);
                    for (int i = 0; i < allRolesDto.size(); i++) {
                        if (!currentRoleNames.contains(allRolesDto.get(i).getName())) {
                            PresentationUtils.typewriterPrint((i + 1) + ". " + allRolesDto.get(i).getName(), 20);
                        }
                    }
                    PresentationUtils.typewriterPrint("Select role to add (enter number), or 0 to finish:", 20);
                    int roleChoice = scanner.nextInt();
                    scanner.nextLine();
                    if (roleChoice == 0) break;
                    if (roleChoice < 1 || roleChoice > allRolesDto.size()) {
                        PresentationUtils.typewriterPrint("Invalid role selection.", 20);
                        continue;
                    }
                    RoleDTO selectedRole = allRolesDto.get(roleChoice - 1);
                    if (!currentRoleNames.contains(selectedRole)) {
                        currentRoleNames.add(selectedRole);
                        PresentationUtils.typewriterPrint("Added role: " + selectedRole, 20);

                        if (selectedRole.equals("Driver")) {
                            List<String> licenseTypes = new ArrayList<>();
                            while (true) {
                                PresentationUtils.typewriterPrint("License Type (Truck Size):", 20);
                                PresentationUtils.typewriterPrint("1) B (Small)", 20);
                                PresentationUtils.typewriterPrint("2) C (Medium)", 20);
                                PresentationUtils.typewriterPrint("3) C1 (Large)", 20);
                                PresentationUtils.typewriterPrint(
                                        "Enter the number of a license to add (0 to finish):", 20);

                                String _input = scanner.nextLine().trim();
                                int licenseNum;
                                try {
                                    licenseNum = Integer.parseInt(_input);
                                } catch (NumberFormatException e) {
                                    PresentationUtils.typewriterPrint("Invalid input. Please enter a number.", 20);
                                    continue;
                                }
                                if (licenseNum == 0) break;
                                String lt;
                                switch (licenseNum) {
                                    case 1 -> lt = "B";
                                    case 2 -> lt = "C";
                                    case 3 -> lt = "C1";
                                    default -> {
                                        PresentationUtils.typewriterPrint("Invalid license type selected.", 20);
                                        continue;
                                    }
                                }
                                if (!licenseTypes.contains(lt)) {
                                    licenseTypes.add(lt);
                                    PresentationUtils.typewriterPrint("Added license type: " + lt, 20);
                                } else {
                                    PresentationUtils.typewriterPrint("License type already added.", 20);
                                }
                            }
                            if (licenseTypes.isEmpty()) {
                                PresentationUtils.typewriterPrint(
                                        "Driver must have at least one license type. Removing driver role.", 20);
                                currentRoleNames.remove(selectedRole);
                            } else {
                                HR.DTO.DriverInfoDTO diDto = new HR.DTO.DriverInfoDTO();
                                diDto.setEmployeeId(employeeDto.getId());
                                diDto.setLicenseType(licenseTypes);
                                employeeService.updateDriverInfo(diDto);
                            }
                        }
                    } else {
                        // Removing an existing role
                        if (selectedRole.equals("Driver")) {
                            currentRoleNames.remove(selectedRole);
                            HR.DTO.DriverInfoDTO diDto = new HR.DTO.DriverInfoDTO();
                            diDto.setEmployeeId(employeeDto.getId());
                            diDto.setLicenseType(Collections.emptyList());
                            employeeService.updateDriverInfo(diDto);
                            PresentationUtils.typewriterPrint("Driver role and driver info removed.", 20);
                        } else {
                            PresentationUtils.typewriterPrint("Role already exists for this employee.", 20);
                        }
                    }
                    employeeDto.setRoles(currentRoleNames);
                    employeeService.updateEmployee(employeeDto);
                }
                case 4 -> {
                    // Update Licenses (for drivers only)
                    if (employeeDto.getRoles().stream()
                            .noneMatch(r -> r.equals("Driver"))) {
                        PresentationUtils.typewriterPrint(
                                "This employee is not a driver. Cannot update licenses.", 20);
                        break;
                    }
                    Set<String> currentLicenses = new HashSet<>(
                            employeeService.getDriverLicenses(employeeDto.getId())
                                    .stream()
                                    .map(Enum::name)
                                    .toList()
                    );
                    List<String> allTypes = List.of("B", "C", "C1");
                    while (true) {
                        PresentationUtils.typewriterPrint("Driver Licenses (toggle selection):", 20);
                        for (int i = 0; i < allTypes.size(); i++) {
                            String t = allTypes.get(i);
                            boolean has = currentLicenses.contains(t);
                            String mark = has ? "[X]" : "[ ]";
                            PresentationUtils.typewriterPrint((i + 1) + ") " + mark + " " + t, 20);
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
                        String selected = allTypes.get(num - 1);
                        if (currentLicenses.contains(selected)) {
                            currentLicenses.remove(selected);
                            PresentationUtils.typewriterPrint("Removed license: " + selected, 20);
                        } else {
                            currentLicenses.add(selected);
                            PresentationUtils.typewriterPrint("Added license: " + selected, 20);
                        }
                    }

                    HR.DTO.DriverInfoDTO updateDto = new HR.DTO.DriverInfoDTO();
                    updateDto.setEmployeeId(employeeDto.getId());
                    updateDto.setLicenseType(new ArrayList<>(currentLicenses));
                    employeeService.updateDriverInfo(updateDto);
                    PresentationUtils.typewriterPrint("Driver licenses updated.", 20);
                }
                case 5 -> exit = true;
                default -> PresentationUtils.typewriterPrint("Invalid choice. Please try again.", 20);
            }
        }
    }

    public void assignEmployeeToShift(Scanner scanner) {
        if (!currentUserRoleName.equalsIgnoreCase(HR_ROLE_NAME)) {
            PresentationUtils.typewriterPrint(
                    "Access Denied: Only HR or Shift Manager can assign employees to shifts.", 20);
            return;
        }

        // 1) Fetch “this week” shifts via ShiftService (returns ShiftDTOs)
        List<ShiftDTO> shifts = shiftService.getCurrentWeekShifts();
        List<EmployeeDTO> employees = employeeService.getEmployees();

        if (shifts.isEmpty()) {
            PresentationUtils.typewriterPrint("No shifts scheduled for this week.", 20);
            return;
        }

        // 2) Display available shifts
        PresentationUtils.typewriterPrint("Available Shifts:", 20);
        for (int i = 0; i < shifts.size(); i++) {
            ShiftDTO s = shifts.get(i);
            String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(s.getDate());
            PresentationUtils.typewriterPrint(
                    (i + 1) + ". " + s.getId() + " on " + dateStr + " (" + s.getType() + ")",
                    20
            );
        }
        PresentationUtils.typewriterPrint("Select shift:", 20);
        int shiftIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (shiftIndex < 0 || shiftIndex >= shifts.size()) {
            PresentationUtils.typewriterPrint("Invalid shift selection.", 20);
            return;
        }
        ShiftDTO shiftDto = shifts.get(shiftIndex);

        // 3) Repeatedly assign employees until all roles are filled or user quits
        boolean exitLoop = false;
        while (!exitLoop) {
            // 3a) Build a map: roleName -> List of employeeIds already assigned
            Map<String, List<String>> assignedMap = new HashMap<>();
            for (ShiftAssignmentDTO sa : shiftDto.getAssignedEmployees()) {
                String roleName = sa.getRoleName();
                String empId = sa.getEmployeeId();
                assignedMap.computeIfAbsent(roleName, k -> new ArrayList<>()).add(empId);
            }

            Map<String, Integer> requiredCounts = shiftDto.getRequiredCounts();

            // Check if all roles have been assigned fully
            boolean allComplete = true;
            for (String roleName : requiredCounts.keySet()) {
                int required = requiredCounts.get(roleName);
                int assigned = assignedMap.getOrDefault(roleName, Collections.emptyList()).size();
                if (assigned < required) {
                    allComplete = false;
                    break;
                }
            }
            if (allComplete) {
                PresentationUtils.typewriterPrint(
                        "All roles for shift " + shiftDto.getId() + " have been fully assigned.",
                        20
                );
                break;
            }

            // 3b) Show current status for each role
            PresentationUtils.typewriterPrint(
                    "\nRole assignment status for shift " + shiftDto.getId() + ":",
                    20
            );
            List<String> rolesList = new ArrayList<>(requiredCounts.keySet());
            for (int i = 0; i < rolesList.size(); i++) {
                String roleName = rolesList.get(i);
                int required = requiredCounts.get(roleName);
                int assigned = assignedMap.getOrDefault(roleName, Collections.emptyList()).size();
                int missing = required - assigned;
                String status = (missing == 0) ? "Full" : "Missing: " + missing;
                PresentationUtils.typewriterPrint(
                        (i + 1) + ". " + roleName + " (" + status + ")",
                        20
                );
            }
            PresentationUtils.typewriterPrint("0. Quit assignment for this shift", 20);

            PresentationUtils.typewriterPrint(
                    "Enter the number corresponding to the role you want to fill:",
                    20
            );
            int roleChoice = scanner.nextInt() - 1;
            scanner.nextLine();
            if (roleChoice == -1) {
                // User entered 0
                exitLoop = true;
                break;
            }
            if (roleChoice < 0 || roleChoice >= rolesList.size()) {
                PresentationUtils.typewriterPrint("Invalid role selection.", 20);
                continue;
            }

            String selectedRoleName = rolesList.get(roleChoice);
            int requiredForRole = requiredCounts.get(selectedRoleName);
            int assignedForRole = assignedMap.getOrDefault(selectedRoleName, Collections.emptyList()).size();
            if (assignedForRole >= requiredForRole) {
                PresentationUtils.typewriterPrint(
                        "This role has already been fully assigned.",
                        20
                );
                continue;
            }

            // 3c) Build list of qualified EmployeeDTOs
            List<EmployeeDTO> qualifiedEmployees = new ArrayList<>();
            for (EmployeeDTO e : employees) {
                // (i) Must have the selected role
                boolean hasRole = e.getRoles().stream()
                        .anyMatch(rdto -> rdto.getName().equalsIgnoreCase(selectedRoleName));
                if (!hasRole) {
                    continue;
                }

                // (ii) Not already assigned to this shift under that role
                List<String> assignedIds = assignedMap.getOrDefault(selectedRoleName, Collections.emptyList());
                if (assignedIds.contains(e.getId())) {
                    continue;
                }

                // (iii) Not on vacation on this shift’s date
                boolean onVacation = false;
                if (e.getHolidays() != null) {
                    for (Date vacDate : e.getHolidays()) {
                        if (vacDate.equals(shiftDto.getDate())) {
                            onVacation = true;
                            break;
                        }
                    }
                }
                if (onVacation) {
                    continue;
                }

                // (iv) Check availability for this week
                List<WeeklyAvailabilityDTO> availThisWeek = e.getAvailabilityThisWeek();
                if (availThisWeek == null || availThisWeek.isEmpty()) {
                    // no availability constraints ⇒ eligible
                    qualifiedEmployees.add(e);
                } else {
                    // must have at least one matching slot
                    boolean available = false;
                    DayOfWeek shiftDay = shiftDto.getDate().toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .getDayOfWeek();
                    for (WeeklyAvailabilityDTO waDto : availThisWeek) {
                        if (waDto.getDay() == shiftDay
                                && waDto.getTime().equals(shiftDto.getType()))
                        {
                            available = true;
                            break;
                        }
                    }
                    if (available) {
                        qualifiedEmployees.add(e);
                    }
                }
            }

            if (qualifiedEmployees.isEmpty()) {
                PresentationUtils.typewriterPrint(
                        "No qualified employees available for role " + selectedRoleName,
                        20
                );
                continue;
            }

            // 3d) Display qualified employees
            PresentationUtils.typewriterPrint(
                    "Qualified Employees for role " + selectedRoleName + ":",
                    20
            );
            for (int i = 0; i < qualifiedEmployees.size(); i++) {
                PresentationUtils.typewriterPrint(
                        (i + 1) + ". " + qualifiedEmployees.get(i).getName(),
                        20
                );
            }
            PresentationUtils.typewriterPrint("Select employee to assign:", 20);
            int employeeIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            if (employeeIndex < 0 || employeeIndex >= qualifiedEmployees.size()) {
                PresentationUtils.typewriterPrint("Invalid employee selection.", 20);
                continue;
            }

            EmployeeDTO selectedEmployee = qualifiedEmployees.get(employeeIndex);

            // 3e) Use ShiftService to assign
            shiftService.assignEmployeeToShift(
                    shiftDto.getId(),
                    selectedEmployee.getId(),
                    selectedRoleName
            );

            // 3f) Refresh the ShiftDTO’s assignments
            shiftDto = shiftService.getShiftById(shiftDto.getId());
        }

        // 4) After exiting the loop, print final status
        PresentationUtils.typewriterPrint(
                "\nFinal shift completion status for shift " + shiftDto.getId() + ":",
                20
        );
        Map<String, Integer> finalRequired = shiftDto.getRequiredCounts();

        // Rebuild a final assignedMap from ShiftAssignmentDTO list
        Map<String, List<String>> finalAssignedMap = new HashMap<>();
        for (ShiftAssignmentDTO sa : shiftDto.getAssignedEmployees()) {
            String roleName = sa.getRoleName();
            String empId = sa.getEmployeeId();
            finalAssignedMap.computeIfAbsent(roleName, k -> new ArrayList<>()).add(empId);
        }

        for (String roleName : finalRequired.keySet()) {
            int req = finalRequired.get(roleName);
            int asg = finalAssignedMap.getOrDefault(roleName, Collections.emptyList()).size();
            int missing = req - asg;
            String status = (missing == 0) ? "Full" : "Missing " + missing + " employee(s).";
            PresentationUtils.typewriterPrint(
                    "Role " + roleName + ": " + status,
                    20
            );
        }
    }

    private void configureShiftRoles(Scanner scanner) {
        if (!currentUserRoleName.equalsIgnoreCase(HR_ROLE_NAME)) {
            PresentationUtils.typewriterPrint("Access Denied: Only HR can configure shift roles.", 20);
            return;
        }

        PresentationUtils.typewriterPrint("Configure roles for: 1) Current week   2) Next week", 20);
        int wk = scanner.nextInt();
        scanner.nextLine();

        List<ShiftDTO> weekShifts = (wk == 1)
                ? shiftService.getCurrentWeekShifts()
                : shiftService.getNextWeekShifts();

        if (weekShifts.isEmpty()) {
            PresentationUtils.typewriterPrint("No shifts available to configure.", 20);
            return;
        }
        weekShifts.sort(Comparator.comparing(ShiftDTO::getDate));
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < weekShifts.size(); i++) {
            ShiftDTO s = weekShifts.get(i);
            PresentationUtils.typewriterPrint(
                    String.format("%d) %s on %s (%s)",
                            i + 1,
                            s.getId(),
                            fmt.format(s.getDate()),
                            s.getType()
                    ), 20
            );
        }
        PresentationUtils.typewriterPrint("0) Exit", 20);
        PresentationUtils.typewriterPrint("Select shift to configure: ", 20);
        int idx = scanner.nextInt() - 1;
        scanner.nextLine();
        if (idx < 0 || idx >= weekShifts.size()) {
            PresentationUtils.typewriterPrint("Exiting configuration.", 20);
            return;
        }

        String shiftId = weekShifts.get(idx).getId();
        Map<String, Integer> requiredCounts = new HashMap<>();
        List<RoleDTO> roles = roleService.getRoles();
        for (RoleDTO rd : roles) {
            String rn = rd.getName();
            if (rn.equalsIgnoreCase("Shift Manager") || rn.equalsIgnoreCase("HR Manager")) {
                continue;
            }
            PresentationUtils.typewriterPrint("Required # for role " + rn + ":", 20);
            int cnt = scanner.nextInt();
            scanner.nextLine();
            requiredCounts.put(rn, cnt);
        }
        shiftService.configureShiftRoles(shiftId, requiredCounts);
    }

    public void processSwapRequests(Scanner scanner) {
        List<SwapRequestDTO> swapRequests = swapService.getSwapRequests();
        if (swapRequests.isEmpty()) {
            PresentationUtils.typewriterPrint("No swap requests available.", 20);
            return;
        }

        PresentationUtils.typewriterPrint("Current Swap Requests:", 20);
        for (int i = 0; i < swapRequests.size(); i++) {
            PresentationUtils.typewriterPrint((i + 1) + ". " + swapRequests.get(i), 20);
        }
        PresentationUtils.typewriterPrint("Select a swap request to process: ", 20);
        int first = scanner.nextInt() - 1;
        scanner.nextLine();
        if (first < 0 || first >= swapRequests.size()) {
            PresentationUtils.typewriterPrint("Invalid selection.", 20);
            return;
        }
        SwapRequestDTO req1 = swapRequests.get(first);

        List<SwapRequestDTO> compat = new ArrayList<>();
        for (SwapRequestDTO r : swapRequests) {
            if (!r.getEmployeeId().equals(req1.getEmployeeId())
                    && !r.getShiftId().equals(req1.getShiftId())
                    && r.getRoleName().equals(req1.getRoleName())) {
                compat.add(r);
            }
        }
        if (compat.isEmpty()) {
            PresentationUtils.typewriterPrint("No compatible swap requests found.", 20);
            return;
        }

        PresentationUtils.typewriterPrint("Compatible Swap Requests:", 20);
        for (int i = 0; i < compat.size(); i++) {
            PresentationUtils.typewriterPrint((i + 1) + ". " + compat.get(i), 20);
        }
        PresentationUtils.typewriterPrint("Select one to swap with: ", 20);
        int second = scanner.nextInt() - 1;
        scanner.nextLine();
        if (second < 0 || second >= compat.size()) {
            PresentationUtils.typewriterPrint("Invalid selection.", 20);
            return;
        }
        SwapRequestDTO req2 = compat.get(second);
        swapService.acceptSwapRequests(req1.getId(), req2.getId());
    }
}
