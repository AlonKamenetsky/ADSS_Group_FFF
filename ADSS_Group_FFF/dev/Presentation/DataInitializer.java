package Presentation;
import Domain.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.text.ParseException;

public class DataInitializer {
    public static void initializeExampleData() throws ParseException {
        RolesRepo rolesRepo = RolesRepo.getInstance();
        EmployeesRepo employeesRepo = EmployeesRepo.getInstance();

        // Add Roles
        rolesRepo.addRole(new Role("HR"));
        rolesRepo.addRole(new Role("Shift Manager"));
        rolesRepo.addRole(new Role("Cashier"));
        rolesRepo.addRole(new Role("Warehouse"));
        rolesRepo.addRole(new Role("Cleaner"));
        rolesRepo.addRole(new Role("Driver"));

        // Get Roles
        Role hrRole = rolesRepo.getRoleByName("HR");
        Role managerRole = rolesRepo.getRoleByName("Shift Manager");
        Role cashierRole = rolesRepo.getRoleByName("Cashier");
        Role warehouseRole = rolesRepo.getRoleByName("Warehouse");
        Role cleanerRole = rolesRepo.getRoleByName("Cleaner");
        Role driverRole = rolesRepo.getRoleByName("Driver");

        // Add Employees
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        Date employmentDate = dateFormat.parse("01-01-2020");

        Employee dana = new Employee("1",
                new LinkedList<>(Arrays.asList(hrRole, cashierRole)),
                "Dana",
                "123",
                "IL123BANK",
                5000f,
                employmentDate);
        dana.setPassword("123");

        Employee john = new Employee("2",
                new LinkedList<>(Arrays.asList(warehouseRole)),
                "John",
                "456",
                "IL456BANK",
                4500f,
                employmentDate);
        john.setPassword("456");

        employeesRepo.addEmployee(dana);
        employeesRepo.addEmployee(john);

        // Create two example shifts

        // Prepare a date format for shift dates.
        SimpleDateFormat shiftDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        shiftDateFormat.setLenient(false);
        Date shiftDate1 = shiftDateFormat.parse("15-09-2023");
        Date shiftDate2 = shiftDateFormat.parse("16-09-2023");

        // Build required roles and required counts for Shift 1.
        Map<Role, List<Employee>> requiredRoles1 = new HashMap<>();
        Map<Role, Integer> requiredCounts1 = new HashMap<>();
        // Get all roles from RolesRepo.
        for (Role role : rolesRepo.getRoles()) {
            // Skip HR role entirely.
            if (role.getName().equalsIgnoreCase("HR")) {
                continue;
            }
            // Automatically set Shift Manager required count to 1.
            else if (role.getName().equalsIgnoreCase("Shift Manager") || role.getName().equalsIgnoreCase("ShiftManager")) {
                requiredCounts1.put(role, 1);
                requiredRoles1.put(role, new ArrayList<>());
            }
            // For other roles, set required count to 1 (you can adjust these numbers as needed).
            else {
                requiredCounts1.put(role, 1);
                requiredRoles1.put(role, new ArrayList<>());
            }
        }
        // Create Shift 1 (Morning shift).
        Shift shift1 = new Shift("SHIFT1", shiftDate1, Shift.ShiftTime.Morning, requiredRoles1, requiredCounts1);

        // Build required roles and required counts for Shift 2.
        Map<Role, List<Employee>> requiredRoles2 = new HashMap<>();
        Map<Role, Integer> requiredCounts2 = new HashMap<>();
        for (Role role : rolesRepo.getRoles()) {
            if (role.getName().equalsIgnoreCase("HR")) {
                continue;
            } else if (role.getName().equalsIgnoreCase("Shift Manager") || role.getName().equalsIgnoreCase("ShiftManager")) {
                requiredCounts2.put(role, 1);
                requiredRoles2.put(role, new ArrayList<>());
            } else {
                requiredCounts2.put(role, 1);
                requiredRoles2.put(role, new ArrayList<>());
            }
        }
        // Create Shift 2 (Evening shift).
        Shift shift2 = new Shift("SHIFT2", shiftDate2, Shift.ShiftTime.Evening, requiredRoles2, requiredCounts2);

        // Add the shifts to ShiftsRepo.
        ShiftsRepo.getInstance().addShift(shift1);
        ShiftsRepo.getInstance().addShift(shift2);

        System.out.println("Example data loaded successfully.");
    }
}
