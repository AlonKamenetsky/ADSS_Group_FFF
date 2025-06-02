package HR.Presentation;

import HR.DTO.CreateEmployeeDTO;
import HR.DTO.EmployeeDTO;
import HR.DTO.RoleDTO;
import HR.DTO.ShiftTemplateDTO;
import HR.Service.EmployeeService;
import HR.Service.RoleService;
import HR.Service.ShiftService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;

public class DemoDataLoader {
    public static void initializeExampleData(int i) throws ParseException {
        RoleService roleService         = RoleService.getInstance();
        EmployeeService employeeService = EmployeeService.getInstance();
        ShiftService shiftService       = ShiftService.getInstance();

        switch (i) {
            case 1:
                // 1) Seed roles
                Arrays.asList("HR", "Shift Manager", "Cashier", "Warehouse", "Cleaner", "Driver")
                        .forEach(roleName -> {
                            RoleDTO dto = new RoleDTO(roleName);
                            roleService.addRole(dto);
                        });

                // 2) Fetch RoleDTOs
                RoleDTO hrRoleDto        = roleService.getRoleByName("HR");
                RoleDTO cashierRoleDto   = roleService.getRoleByName("Cashier");
                RoleDTO warehouseRoleDto = roleService.getRoleByName("Warehouse");
                RoleDTO cleanerRoleDto   = roleService.getRoleByName("Cleaner");
                RoleDTO driverRoleDto    = roleService.getRoleByName("Driver");
                RoleDTO shiftMgrRoleDto  = roleService.getRoleByName("Shift Manager");

                // 3) Create hireDate
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                df.setLenient(false);
                Date hireDate = df.parse("2020-01-01");

                // 4a) Build and insert Employee #1 (ID = "1", roles = ["Shift Manager","Cashier"])
                CreateEmployeeDTO create1 = new CreateEmployeeDTO();
                create1.setId("1");
                create1.setName("Dana");
                create1.setRoles(Arrays.asList(shiftMgrRoleDto, cashierRoleDto));
                create1.setRawPassword("123");
                create1.setBankAccount("IL123BANK");
                create1.setSalary(5000f);
                create1.setEmploymentDate(hireDate);
                create1.setAvailabilityThisWeek(null);
                create1.setAvailabilityNextWeek(null);
                create1.setHolidays(null);
                employeeService.addEmployee(create1);

                // 4b) Build and insert Employee #2 (ID = "2", roles = ["Warehouse","Cashier"])
                CreateEmployeeDTO create2 = new CreateEmployeeDTO();
                create2.setId("2");
                create2.setName("John");
                create2.setRoles(Arrays.asList(warehouseRoleDto, cashierRoleDto));
                create2.setRawPassword("456");
                create2.setBankAccount("IL456BANK");
                create2.setSalary(4500f);
                create2.setEmploymentDate(hireDate);
                create2.setAvailabilityThisWeek(null);
                create2.setAvailabilityNextWeek(null);
                create2.setHolidays(null);
                employeeService.addEmployee(create2);

                // 4c) Build and insert Employee #3 (ID = "hr", roles = ["HR"])
                CreateEmployeeDTO createHR = new CreateEmployeeDTO();
                createHR.setId("hr");
                createHR.setName("HR Manager");
                createHR.setRoles(Collections.singletonList(hrRoleDto));
                createHR.setRawPassword("123");
                createHR.setBankAccount("IL456BANK");
                createHR.setSalary(4500f);
                createHR.setEmploymentDate(hireDate);
                createHR.setAvailabilityThisWeek(null);
                createHR.setAvailabilityNextWeek(null);
                createHR.setHolidays(null);
                employeeService.addEmployee(createHR);

                // 5) Define recurring‐shift templates
                for (DayOfWeek dow : DayOfWeek.values()) {
                    ShiftTemplateDTO morningTpl = new ShiftTemplateDTO();
                    morningTpl.setDay(dow);
                    morningTpl.setTime(HR.Domain.Shift.ShiftTime.Morning);
                    morningTpl.setDefaultCounts(Collections.emptyMap());
                    shiftService.addTemplate(morningTpl);

                    ShiftTemplateDTO eveningTpl = new ShiftTemplateDTO();
                    eveningTpl.setDay(dow);
                    eveningTpl.setTime(HR.Domain.Shift.ShiftTime.Evening);
                    eveningTpl.setDefaultCounts(Collections.emptyMap());
                    shiftService.addTemplate(eveningTpl);
                }

                // 6) (Optional) Bootstrap the rolling schedule based on current time
                //    If your ShiftService has domain‐aware methods, insert them here. Otherwise, skip.

                PresentationUtils.typewriterPrint(
                        "Example data and recurring-shift templates loaded successfully.",
                        20
                );
                break;

            case 0:
                // Minimal “zero” seed: only add HR role, then prompt for initial HR user
                roleService.addRole(new RoleDTO("HR"));

                for (DayOfWeek dow : DayOfWeek.values()) {
                    ShiftTemplateDTO morningTpl = new ShiftTemplateDTO();
                    morningTpl.setDay(dow);
                    morningTpl.setTime(HR.Domain.Shift.ShiftTime.Morning);
                    morningTpl.setDefaultCounts(Collections.emptyMap());
                    shiftService.addTemplate(morningTpl);

                    ShiftTemplateDTO eveningTpl = new ShiftTemplateDTO();
                    eveningTpl.setDay(dow);
                    eveningTpl.setTime(HR.Domain.Shift.ShiftTime.Evening);
                    eveningTpl.setDefaultCounts(Collections.emptyMap());
                    shiftService.addTemplate(eveningTpl);
                }

                // Same bootstrap logic as above for schedule, if needed…

                Scanner scanner = new Scanner(System.in);
                String newId, newName, newPw, newBankAccount;
                Float newSalary;
                Date newEmpDate;

                while (true) {
                    PresentationUtils.typewriterPrint("Enter ID for initial HR user: ", 20);
                    newId = scanner.nextLine().trim();
                    if (newId.isEmpty()) {
                        PresentationUtils.typewriterPrint("ID cannot be empty. Try again.", 20);
                    } else break;
                }
                while (true) {
                    PresentationUtils.typewriterPrint("Enter name for initial HR user: ", 20);
                    newName = scanner.nextLine().trim();
                    if (newName.isEmpty()) {
                        PresentationUtils.typewriterPrint("Name cannot be empty. Try again.", 20);
                    } else break;
                }
                while (true) {
                    PresentationUtils.typewriterPrint("Enter password for initial HR user: ", 20);
                    newPw = scanner.nextLine().trim();
                    if (newPw.isEmpty()) {
                        PresentationUtils.typewriterPrint("Password cannot be empty. Try again.", 20);
                    } else break;
                }
                while (true) {
                    PresentationUtils.typewriterPrint("Enter bank account for initial HR user: ", 20);
                    newBankAccount = scanner.nextLine().trim();
                    if (newBankAccount.isEmpty()) {
                        PresentationUtils.typewriterPrint("Bank account cannot be empty. Try again.", 20);
                    } else break;
                }
                while (true) {
                    PresentationUtils.typewriterPrint("Enter salary for initial HR user: ", 20);
                    String salaryLine = scanner.nextLine().trim();
                    try {
                        newSalary = Float.valueOf(salaryLine);
                        if (newSalary < 0) {
                            PresentationUtils.typewriterPrint("Salary must be non-negative. Try again.", 20);
                        } else break;
                    } catch (NumberFormatException e) {
                        PresentationUtils.typewriterPrint("Invalid number. Please enter a valid salary.", 20);
                    }
                }
                SimpleDateFormat df_ = new SimpleDateFormat("yyyy-MM-dd");
                df_.setLenient(false);
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

                // Build CreateEmployeeDTO for the initial HR user
                CreateEmployeeDTO createHr0 = new CreateEmployeeDTO();
                createHr0.setId(newId);
                createHr0.setName(newName);
                createHr0.setRoles(Collections.singletonList(roleService.getRoleByName("HR")));
                createHr0.setRawPassword(newPw);
                createHr0.setBankAccount(newBankAccount);
                createHr0.setSalary(newSalary);
                createHr0.setEmploymentDate(newEmpDate);
                createHr0.setAvailabilityThisWeek(null);
                createHr0.setAvailabilityNextWeek(null);
                createHr0.setHolidays(null);

                employeeService.addEmployee(createHr0);

                PresentationUtils.typewriterPrint("Initial HR user created.", 20);
                break;
        }
    }
}
