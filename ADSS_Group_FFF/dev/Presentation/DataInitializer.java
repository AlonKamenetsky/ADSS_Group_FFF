// Presentation/DataInitializer.java
package Presentation;

import Domain.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

public class DataInitializer {
    public static void initializeExampleData() throws ParseException {
        // ——— 1) Seed Roles & Employees ———
        RolesRepo rolesRepo   = RolesRepo.getInstance();
        EmployeesRepo empRepo = EmployeesRepo.getInstance();

        Arrays.asList("HR", "Shift Manager", "Cashier", "Warehouse", "Cleaner", "Driver")
                .forEach(name -> rolesRepo.addRole(new Role(name)));

        Role hrRole       = rolesRepo.getRoleByName("HR");
        Role cashierRole  = rolesRepo.getRoleByName("Cashier");
        Role warehouseRole= rolesRepo.getRoleByName("Warehouse");
        Role cleanerRole  = rolesRepo.getRoleByName("Cleaner");
        Role driverRole   = rolesRepo.getRoleByName("Driver");
        Role shiftMgrRole = rolesRepo.getRoleByName("Shift Manager");

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setLenient(false);
        Date hireDate = df.parse("2020-01-01");

        Employee dana = new Employee("1",
                new LinkedList<>(Arrays.asList(shiftMgrRole, cashierRole)),
                "Dana", "123", "IL123BANK", 5000f, hireDate);
        dana.setPassword("123");
        empRepo.addEmployee(dana);

        Employee john = new Employee("2",
                new LinkedList<>(Arrays.asList(warehouseRole, cashierRole)),
                "John", "456", "IL456BANK", 4500f, hireDate);
        john.setPassword("456");
        empRepo.addEmployee(john);

        Employee hr = new Employee("hr",
                new LinkedList<>(Arrays.asList(hrRole)),
                "HR Manager", "123", "IL456BANK", 4500f, hireDate);
        hr.setPassword("123");
        empRepo.addEmployee(hr);

        // ——— 2) Define recurring-shift templates ———
        ShiftsRepo shiftsRepo = ShiftsRepo.getInstance();
        for (DayOfWeek dow : DayOfWeek.values()) {
            shiftsRepo.addTemplate(new RecurringShift(dow, Shift.ShiftTime.Morning));
            shiftsRepo.addTemplate(new RecurringShift(dow, Shift.ShiftTime.Evening));
        }

        // ——— 3) Bootstrap the rolling schedule based on current time ———
        LocalDate today     = LocalDate.now(ZoneId.systemDefault());
        LocalDate saturday  = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));
        LocalDateTime cutoff= saturday.atTime(18, 0);
        LocalDateTime now   = LocalDateTime.now(ZoneId.systemDefault());

        if (!now.isBefore(cutoff)) {
            // After Saturday 18:00 → currentWeek = week after this Saturday
            shiftsRepo.getSchedule().resetNextWeek(shiftsRepo.getTemplates(), saturday);
            shiftsRepo.getSchedule().swapWeeks();
            shiftsRepo.getSchedule().resetNextWeek(shiftsRepo.getTemplates(), saturday.plusDays(7));
        } else {
            // Before Saturday 18:00 → currentWeek = week after last Saturday
            LocalDate prevSat = saturday.minusDays(7);
            shiftsRepo.getSchedule().resetNextWeek(shiftsRepo.getTemplates(), prevSat);
            shiftsRepo.getSchedule().swapWeeks();
            shiftsRepo.getSchedule().resetNextWeek(shiftsRepo.getTemplates(), saturday);
        }

        ConsoleUtils.typewriterPrint(
                "Example data and recurring-shift templates loaded successfully.",
                20
        );
    }
}
