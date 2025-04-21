package Presentation;

import Domain.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

public class DataInitializer {
    public static void initializeExampleData() throws ParseException {
        // ——— 1) Seed Roles & Employees (unchanged) ———
        RolesRepo rolesRepo     = RolesRepo.getInstance();
        EmployeesRepo empRepo   = EmployeesRepo.getInstance();

        // Add Roles
        Arrays.asList("HR", "Shift Manager", "Cashier", "Warehouse", "Cleaner", "Driver")
                .forEach(name -> rolesRepo.addRole(new Role(name)));

        // Lookup Roles
        Role hrRole       = rolesRepo.getRoleByName("HR");
        Role cashierRole  = rolesRepo.getRoleByName("Cashier");
        Role warehouseRole= rolesRepo.getRoleByName("Warehouse");

        // Parse a common hire date
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setLenient(false);
        Date hireDate = df.parse("2020-01-01");

        // Create two example employees
        Employee dana = new Employee(
                "1",
                new LinkedList<>(Arrays.asList(hrRole, cashierRole)),
                "Dana",
                "123",         // user‑facing password
                "IL123BANK",
                5_000f,
                hireDate
        );
        dana.setPassword("123");
        empRepo.addEmployee(dana);

        Employee john = new Employee(
                "2",
                new LinkedList<>(Arrays.asList(warehouseRole,cashierRole)),
                "John",
                "456",
                "IL456BANK",
                4_500f,
                hireDate
        );
        john.setPassword("456");
        empRepo.addEmployee(john);

        // ——— 2) Define the two‐daily recurring templates ———
        ShiftsRepo shiftsRepo = ShiftsRepo.getInstance();
        for (DayOfWeek dow : DayOfWeek.values()) {
            shiftsRepo.addTemplate(new RecurringShift(dow, Shift.ShiftTime.Morning));
            shiftsRepo.addTemplate(new RecurringShift(dow, Shift.ShiftTime.Evening));
        }

        // ——— 3) Bootstrap the rolling schedule ———
        // Find the most recent Saturday (today if it is Saturday)
        LocalDate lastSat = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));
        // Build “nextWeek” from our templates starting that Saturday…
        shiftsRepo.getSchedule().resetNextWeek(shiftsRepo.getTemplates(), lastSat);
        // …then roll it into “currentWeek”
        shiftsRepo.getSchedule().swapWeeks();

        System.out.println("Example data and recurring‑shift templates loaded successfully.");
    }
}
