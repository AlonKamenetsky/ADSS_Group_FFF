package Presentation;
import Domain.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
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

        System.out.println("Example data loaded successfully.");
    }
}
