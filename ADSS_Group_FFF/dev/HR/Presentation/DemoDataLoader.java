package HR.Presentation;

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

                // 2) Fetch RoleDTOs (previous code used domain Role; now we use RoleDTO)
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

                // 4a) Build EmployeeDTO #1 (ID = "1", roles = ["Shift Manager","Cashier"])
                EmployeeDTO dto1 = new EmployeeDTO();
                dto1.setId("1");
                dto1.setName("Dana");
                dto1.setRoles(Arrays.asList(shiftMgrRoleDto, cashierRoleDto));
                employeeService.setPassword("1", "123");
                dto1.setBankAccount("IL123BANK");
                dto1.setSalary(5000f);
                dto1.setEmploymentDate(hireDate);
                employeeService.addEmployee(dto1);

                // 4b) Build EmployeeDTO #2 (ID = "2", roles = ["Warehouse","Cashier"])
                EmployeeDTO dto2 = new EmployeeDTO();
                dto2.setId("2");
                dto2.setName("John");
                dto2.setRoles(Arrays.asList(warehouseRoleDto, cashierRoleDto));
                employeeService.setPassword("2", "456");
                dto2.setBankAccount("IL456BANK");
                dto2.setSalary(4500f);
                dto2.setEmploymentDate(hireDate);
                employeeService.addEmployee(dto2);

                // 4c) Build EmployeeDTO #3 (ID = "hr", roles = ["HR"])
                EmployeeDTO dtoHR = new EmployeeDTO();
                dtoHR.setId("hr");
                dtoHR.setName("HR Manager");
                dtoHR.setRoles(Collections.singletonList(hrRoleDto));
                employeeService.setPassword("hr", "123");
                dtoHR.setBankAccount("IL456BANK");
                dtoHR.setSalary(4500f);
                dtoHR.setEmploymentDate(hireDate);
                employeeService.addEmployee(dtoHR);

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

                // 6) Bootstrap the rolling schedule based on current time
                //    Note: your service now returns DTOs, but the underlying domain schedule reset logic
                //    still needs a domain object. If ShiftService.getSchedule() now returns WeeklyShiftsScheduleDTO,
                //    you'd need a separate domain‐aware method. For now, assume getSchedule() returns domain schedule:
                // WeeklyShiftsSchedule domainSchedule = shiftService.getScheduleDomain();
                // The rest remains the same. If your service no longer exposes domain schedule,
                // you would need to adjust this block accordingly.

                // Example (pseudo‐code):
                /*
                LocalDate today     = LocalDate.now(ZoneId.systemDefault());
                LocalDate saturday  = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));
                LocalDateTime cutoff= saturday.atTime(18, 0);
                LocalDateTime now   = LocalDateTime.now(ZoneId.systemDefault());

                if (!now.isBefore(cutoff)) {
                    domainSchedule.resetNextWeek(
                        shiftService.getTemplates()
                                    .stream()
                                    .map(HR.Mapper.ShiftTemplateMapper::fromDTO)
                                    .collect(Collectors.toList()),
                        saturday
                    );
                    domainSchedule.swapWeeks();
                    domainSchedule.resetNextWeek(
                        shiftService.getTemplates()
                                    .stream()
                                    .map(HR.Mapper.ShiftTemplateMapper::fromDTO)
                                    .collect(Collectors.toList()),
                        saturday.plusDays(7)
                    );
                } else {
                    LocalDate prevSat = saturday.minusDays(7);
                    domainSchedule.resetNextWeek(
                        shiftService.getTemplates()
                                    .stream()
                                    .map(HR.Mapper.ShiftTemplateMapper::fromDTO)
                                    .collect(Collectors.toList()),
                        prevSat
                    );
                    domainSchedule.swapWeeks();
                    domainSchedule.resetNextWeek(
                        shiftService.getTemplates()
                                    .stream()
                                    .map(HR.Mapper.ShiftTemplateMapper::fromDTO)
                                    .collect(Collectors.toList()),
                        saturday
                    );
                }
                */

                PresentationUtils.typewriterPrint(
                        "Example data and recurring-shift templates loaded successfully.",
                        20
                );
                break;

            case 0:
                // Minimal “zero” seed: only add HR role and then prompt for initial HR user details
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

                // Same bootstrap logic as above for schedule…

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

                // Build RoleDTO list for HR
                List<RoleDTO> hrRolesDto = Collections.singletonList(
                        roleService.getRoleByName("HR")
                );

                // Build EmployeeDTO and add
                EmployeeDTO newHrDto = new EmployeeDTO();
                newHrDto.setId(newId);
                newHrDto.setName(newName);
                employeeService.setPassword(newId, newPw);
                newHrDto.setBankAccount(newBankAccount);
                newHrDto.setSalary(newSalary);
                newHrDto.setEmploymentDate(newEmpDate);
                newHrDto.setRoles(hrRolesDto);
                employeeService.addEmployee(newHrDto);

                PresentationUtils.typewriterPrint("Initial HR user created.", 20);
                break;
        }
    }
}
