// HR.tests.Presentation/DataInitializer.java
package HR.Presentation;

import HR.DataAccess.EmployeesRepo;
import HR.DataAccess.RolesRepo;
import HR.DataAccess.WeeklyAvailabilityDAO;
import HR.Domain.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class DemoDataLoader {
    public static void initializeExampleData(int i) throws ParseException {
        // ——— 1) Seed Roles & Employees ———
        RolesRepo rolesRepo   = RolesRepo.getInstance();
        EmployeesRepo empRepo = EmployeesRepo.getInstance();
        WeeklyAvailabilityDAO.ShiftsRepo shiftsRepo = WeeklyAvailabilityDAO.ShiftsRepo.getInstance();
        switch (i){
        case 1:
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
            for (DayOfWeek dow : DayOfWeek.values()) {
                shiftsRepo.addTemplate(new ShiftTemplate(dow, Shift.ShiftTime.Morning));
                shiftsRepo.addTemplate(new ShiftTemplate(dow, Shift.ShiftTime.Evening));
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

            PresentationUtils.typewriterPrint(
                    "Example data and recurring-shift templates loaded successfully.",
                    20
            );
            break;
        case 0:

            rolesRepo.addRole(new Role("HR"));
            List<Role> HRRoles = new ArrayList<>();
            HRRoles.add(rolesRepo.getRoleByName("HR"));

            SimpleDateFormat df_ = new SimpleDateFormat("yyyy-MM-dd");
            df_.setLenient(false);

            for (DayOfWeek dow : DayOfWeek.values()) {
                shiftsRepo.addTemplate(new ShiftTemplate(dow, Shift.ShiftTime.Morning));
                shiftsRepo.addTemplate(new ShiftTemplate(dow, Shift.ShiftTime.Evening));
            }

            // ——— 3) Bootstrap the rolling schedule based on current time ———
            LocalDate today_     = LocalDate.now(ZoneId.systemDefault());
            LocalDate saturday_  = today_.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));
            LocalDateTime cutoff_= saturday_.atTime(18, 0);
            LocalDateTime now_   = LocalDateTime.now(ZoneId.systemDefault());

            if (!now_.isBefore(cutoff_)) {
                // After Saturday 18:00 → currentWeek = week after this Saturday
                shiftsRepo.getSchedule().resetNextWeek(shiftsRepo.getTemplates(), saturday_);
                shiftsRepo.getSchedule().swapWeeks();
                shiftsRepo.getSchedule().resetNextWeek(shiftsRepo.getTemplates(), saturday_.plusDays(7));
            } else {
                // Before Saturday 18:00 → currentWeek = week after last Saturday
                LocalDate prevSat_ = saturday_.minusDays(7);
                shiftsRepo.getSchedule().resetNextWeek(shiftsRepo.getTemplates(), prevSat_);
                shiftsRepo.getSchedule().swapWeeks();
                shiftsRepo.getSchedule().resetNextWeek(shiftsRepo.getTemplates(), saturday_);
            }

            Scanner scanner = new Scanner(System.in);
            String newId;
            while (true) {
                PresentationUtils.typewriterPrint("Enter ID for initial HR user: ", 20);
                newId = scanner.nextLine().trim();
                if (newId.isEmpty()) {
                    PresentationUtils.typewriterPrint("ID cannot be empty. Try again.", 20);
                    continue;
                }
                break;
            }

            String newName;
            while (true) {
                PresentationUtils.typewriterPrint("Enter name for initial HR user: ", 20);
                newName = scanner.nextLine().trim();
                if (newName.isEmpty()) {
                    PresentationUtils.typewriterPrint("Name cannot be empty. Try again.", 20);
                    continue;
                }
                break;
            }

            String newPw;
            while (true) {
                PresentationUtils.typewriterPrint("Enter password for initial HR user: ", 20);
                newPw = scanner.nextLine().trim();
                if (newPw.isEmpty()) {
                    PresentationUtils.typewriterPrint("Password cannot be empty. Try again.", 20);
                    continue;
                }
                break;
            }

            String newBankAccount;
            while (true) {
                PresentationUtils.typewriterPrint("Enter bank account for initial HR user: ", 20);
                newBankAccount = scanner.nextLine().trim();
                if (newBankAccount.isEmpty()) {
                    PresentationUtils.typewriterPrint("Bank account cannot be empty. Try again.", 20);
                    continue;
                }
                break;
            }

            Float newSalary;
            while (true) {
                PresentationUtils.typewriterPrint("Enter salary for initial HR user: ", 20);
                String salaryLine = scanner.nextLine().trim();
                try {
                    newSalary = Float.valueOf(salaryLine);
                    if (newSalary < 0) {
                        PresentationUtils.typewriterPrint("Salary must be non-negative. Try again.", 20);
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    PresentationUtils.typewriterPrint("Invalid number. Please enter a valid salary.", 20);
                }
            }

            Date newEmpDate;
            while (true) {
                PresentationUtils.typewriterPrint("Enter employment date (YYYY-MM-DD): ", 20);
                String dateLine = scanner.nextLine().trim();
                try {
                    newEmpDate = df_.parse(dateLine);
                    break;
                } catch (ParseException e) {
                    PresentationUtils.typewriterPrint("Invalid date format. Use YYYY-MM-DD. Try again.", 20);
                }
            }

            Employee hrUser = new Employee(
                    newId,
                    HRRoles,
                    newName,
                    newPw,
                    newBankAccount,
                    newSalary,
                    newEmpDate
            );
            EmployeesRepo.getInstance().addEmployee(hrUser);
        }

    }
}

