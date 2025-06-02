package HR.tests.PresentationTests;

import HR.DTO.EmployeeDTO;
import HR.DTO.RoleDTO;
import HR.DTO.ShiftDTO;
import HR.DTO.SwapRequestDTO;
import HR.Presentation.HRInterface;
import HR.Service.EmployeeService;
import HR.Service.RoleService;
import HR.Service.ShiftService;
import HR.Service.SwapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class HRInterfaceTest {

    private EmployeeService employeeService;
    private RoleService roleService;
    private ShiftService shiftService;
    private SwapService swapService;

    @BeforeEach
    void setUp() throws ParseException {
        employeeService = EmployeeService.getInstance();
        roleService     = RoleService.getInstance();
        shiftService    = ShiftService.getInstance();
        swapService     = SwapService.getInstance();

        // Clear any existing data between tests:
        // roleService.clearAll();
        // employeeService.clearAll();
        // shiftService.clearAll();
        // swapService.clearAll();

        // 1) Create one HR user
        roleService.addRole(new RoleDTO("HR"));
        EmployeeDTO hrDto = new EmployeeDTO();
        hrDto.setId("HR1");
        hrDto.setName("PrimaryHR");
        hrDto.setRoles(Collections.singletonList(new RoleDTO("HR")));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date d = df.parse("2021-01-01");
        hrDto.setBankAccount("HRBANK");
        hrDto.setSalary(6000f);
        hrDto.setEmploymentDate(d);
        employeeService.addEmployee(hrDto);
        employeeService.setPassword("HR1", "secret");
    }

    @Test
    void viewMyInfo_printsWithoutException() {
        HRInterface ui = new HRInterface("HR1");
        PrintStream originalOut = System.out;
        try {
            ui.viewMyInfo();
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void addNewRole_and_removeRole_updateRoleServiceCorrectly() {
        HRInterface ui = new HRInterface("HR1");

        // 1) Add a brand‐new role “Tester”
        String inputForAdd = "Tester\n";
        Scanner scannerAdd = new Scanner(new ByteArrayInputStream(inputForAdd.getBytes()));
        ui.addNewRole(scannerAdd);

        List<RoleDTO> afterAdd = roleService.getRoles();
        assertTrue(afterAdd.stream()
                        .map(RoleDTO::getName)
                        .anyMatch(rn -> rn.equals("Tester")),
                "Role ‘Tester’ should have been added");

        // 2) Now remove “Tester”
        //    We need to show it in the list and confirm “yes”
        StringBuilder sb = new StringBuilder();
        // Suppose “Tester” is at index N in the getRoles() list. We locate it first:
        int testerIndex = -1;
        List<RoleDTO> currentRoles = roleService.getRoles();
        for (int i = 0; i < currentRoles.size(); i++) {
            if (currentRoles.get(i).getName().equals("Tester")) {
                testerIndex = i + 1;
                break;
            }
        }
        assertTrue(testerIndex > 0, "Tester role should exist in the list before removal");

        // Now simulate “<position>\nyes\n”
        sb.append(testerIndex).append("\n");
        sb.append("yes\n");
        Scanner scannerRemove = new Scanner(new ByteArrayInputStream(sb.toString().getBytes()));
        ui.removeRole(scannerRemove);

        List<RoleDTO> afterRemove = roleService.getRoles();
        assertFalse(afterRemove.stream()
                        .map(RoleDTO::getName)
                        .anyMatch(rn -> rn.equals("Tester")),
                "Role ‘Tester’ should have been removed");
    }

    @Test
    void addEmployee_and_removeEmployee_behaveCorrectly() throws ParseException {
        HRInterface ui = new HRInterface("HR1");

        // 1) Simulate adding a new employee “E999”
        //    We need to provide: ID, Name, Password, BankAccount, Salary, EmploymentDate, roles (+ license if “Driver”)
        //    We’ll use a role “DummyRole” that we add first
        roleService.addRole(new RoleDTO("DummyRole"));

        StringBuilder sb = new StringBuilder();
        sb.append("E999\n");             // ID
        sb.append("NewEmp\n");           // Name
        sb.append("pwd123\n");           // Password
        sb.append("BANKX\n");            // Bank account
        sb.append("3500\n");             // Salary
        sb.append("2022-02-02\n");       // Employment date

        // Now select “DummyRole” from the list of roles:
        List<RoleDTO> allRoles = roleService.getRoles();
        int dummyIndex = -1;
        for (int i = 0; i < allRoles.size(); i++) {
            if (allRoles.get(i).getName().equals("DummyRole")) {
                dummyIndex = i + 1;
                break;
            }
        }
        assertTrue(dummyIndex > 0, "DummyRole must exist before adding employee");

        sb.append(dummyIndex).append("\n"); // choose DummyRole
        sb.append("0\n");                  // finish role selection
        // Since “DummyRole” ≠ “Driver”, no license prompt

        Scanner scannerAddEmp = new Scanner(new ByteArrayInputStream(sb.toString().getBytes()));
        ui.addEmployee(scannerAddEmp);

        // Verify employee E999 was created
        EmployeeDTO e999 = employeeService.getEmployeeById("E999");
        assertNotNull(e999, "E999 should have been created");
        assertEquals("NewEmp", e999.getName());
        assertTrue(e999.getRoles().contains("DummyRole"));

        // 2) Now remove employee E999
        //    List all employees and pick “E999”
        List<EmployeeDTO> emps = employeeService.getEmployees();
        int removeIndex = -1;
        for (int i = 0; i < emps.size(); i++) {
            if (emps.get(i).getId().equals("E999")) {
                removeIndex = i + 1;
                break;
            }
        }
        assertTrue(removeIndex > 0, "E999 must appear in the employees list before removal");

        StringBuilder sb2 = new StringBuilder();
        sb2.append(removeIndex).append("\n");
        sb2.append("yes\n");
        Scanner scannerRemoveEmp = new Scanner(new ByteArrayInputStream(sb2.toString().getBytes()));
        ui.removeEmployee(scannerRemoveEmp);

        EmployeeDTO afterRemoval = employeeService.getEmployeeById("E999");
        assertNull(afterRemoval, "E999 should have been removed");
    }

    @Test
    void updateEmployeeData_bankAccountAndSalaryAndRolesAndLicenses() throws ParseException {
        // 1) First create a non‐driver employee EABC
        EmployeeDTO eabc = new EmployeeDTO();
        eabc.setId("EABC");
        eabc.setName("BaseUser");
        eabc.setRoles(Collections.singletonList(new RoleDTO("BaseRole")));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        eabc.setBankAccount("BANK1");
        eabc.setSalary(2000f);
        eabc.setEmploymentDate(df.parse("2020-03-03"));
        employeeService.addEmployee(eabc);
        employeeService.setPassword("EABC", "ppw");

        // 2) Now call updateEmployeeData to:
        //    a) Change bank account to “NEWBANK”
        //    b) Change salary to 2500
        //    c) Add a new role “Driver” and supply one license type “B”
        //    d) Toggle licenses for “Driver” (remove “B” and add “C”)
        //    e) Remove “Driver” role entirely

        // Make sure “Driver” role exists
        roleService.addRole(new RoleDTO("Driver"));

        HRInterface ui = new HRInterface("HR1");

        // Build interactive input:
        // Step 1: ID = EABC
        // Step 2: Choose 1 (bank), provide “NEWBANK”
        // Step 3: Choose 2 (salary), provide “2500”
        // Step 4: Choose 3 (roles)
        //     i) Show current roles → “BaseRole” (index 1)
        //     ii) Show available roles → “Driver” (if this is index 2)
        //     iii) We pick “Driver” by entering “2”, then license loop:
        //           1 (adds B), 0 (finish)
        // Step 5: Choose 4 (licenses). Toggle from “B” to “C”:
        //           1 (toggle off B), 2 (toggle on C), 0 (finish)
        // Step 6: Choose 3 again to remove Driver:
        //           We see “Driver” in current roles (index maybe 2), so we enter “2” → it’s "Driver", removing it
        // Step 7: Then we enter 5 (exit)

        StringBuilder sb = new StringBuilder();
        sb.append("EABC\n");  // ID to update
        sb.append("1\n");     // Case 1: Update bank
        sb.append("NEWBANK\n");
        sb.append("2\n");     // Case 2: Update salary
        sb.append("2500\n");
        sb.append("3\n");     // Case 3: Update roles
        // Suppose currentRoles = [BaseRole], allRoles = [Driver, HR, Shift Manager …]. We know “Driver” is in the drawn list.
        // We look up each index:
        List<RoleDTO> afterAddingDriverRole = roleService.getRoles();
        int driverIndex = -1;
        for (int i = 0; i < afterAddingDriverRole.size(); i++) {
            if (afterAddingDriverRole.get(i).getName().equals("Driver")) {
                driverIndex = i + 1;
                break;
            }
        }
        assertTrue(driverIndex > 0, "Driver role must exist");
        sb.append(driverIndex).append("\n"); // Add Driver
        sb.append("1\n");     // License loop: “1” selects “B”
        sb.append("0\n");     // finish license loop
        // Now Step 5: Case 4 (Update licenses)
        sb.append("4\n");
        // We assumed license types print as [B],[C],[C1], so B is “1”, C is “2”
        sb.append("1\n");     // toggle off B
        sb.append("2\n");     // toggle on C
        sb.append("0\n");     // finish
        // Step 6: Case 3 again to remove Driver
        sb.append("3\n");
        // Now “Driver” should appear in currentRoles as index X. We find it again:
        List<RoleDTO> nowRoles = roleService.getRoles();
        int driverIndexAgain = -1;
        for (int i = 0; i < nowRoles.size(); i++) {
            if (nowRoles.get(i).getName().equals("Driver")) {
                driverIndexAgain = i + 1;
                break;
            }
        }
        assertTrue(driverIndexAgain > 0, "Driver must still appear in allRoles list");
        sb.append(driverIndexAgain).append("\n"); // This triggers removal of Driver
        sb.append("5\n");    // Exit

        Scanner scanner = new Scanner(new ByteArrayInputStream(sb.toString().getBytes()));
        ui.updateEmployeeData(scanner);

        // Verify bank account changed:
        EmployeeDTO updated = employeeService.getEmployeeById("EABC");
        assertEquals("NEWBANK", updated.getBankAccount());
        assertEquals(2500f, updated.getSalary());

        // “Driver” role was added then removed → final roles should only contain “BaseRole”:
        assertEquals(1, updated.getRoles().size());
        assertEquals("BaseRole", updated.getRoles().get(0));

        // Finally, driver‐info should no longer exist:
        assertThrows(IllegalArgumentException.class,
                () -> employeeService.getDriverLicenses("EABC"),
                "Driver licenses should not exist after removal");
    }

    @Test
    void assignEmployeeToShift_configureShiftRolesAddsCorrectCounts() {
        // 1) Add two roles: “Cashier” and “Cleaner”
        roleService.addRole(new RoleDTO("Cashier"));
        roleService.addRole(new RoleDTO("Cleaner"));

        // 2) Create a shift template (so there is at least one “current week” shift).
        shiftService.addTemplate(new HR.DTO.ShiftTemplateDTO(DayOfWeek.MONDAY, HR.Domain.Shift.ShiftTime.Morning, Collections.emptyMap()));
        // Force the shift creation via configureShiftRoles:
        // We’ll pick “Cashier” = 2, “Cleaner” = 1
        HRInterface ui = new HRInterface("HR1");
        StringBuilder sb = new StringBuilder();
        sb.append("1\n");  // “Current week” (option 1)
        sb.append("1\n");  // select shift #1
        // Now supply counts:
        // Suppose “Cashier” appears at index 2, “Cleaner” appears at index 3 (HR is index 1).
        List<RoleDTO> allRoles = roleService.getRoles();
        int cashierIdx = -1, cleanerIdx = -1;
        for (int i = 0; i < allRoles.size(); i++) {
            if (allRoles.get(i).getName().equals("Cashier")) cashierIdx = i + 1;
            if (allRoles.get(i).getName().equals("Cleaner")) cleanerIdx = i + 1;
        }
        assertTrue(cashierIdx > 0 && cleanerIdx > 0);

        sb.append("2\n");  // Required # for Cashier
        sb.append("1\n");  // Required # for Cleaner

        Scanner scanner = new Scanner(new ByteArrayInputStream(sb.toString().getBytes()));
        ui.assignEmployeeToShift(scanner);

        // Now fetch the shift and verify that requiredCounts map contains (Cashier->2, Cleaner->1):
        List<ShiftDTO> weekShifts = shiftService.getCurrentWeekShifts();
        assertFalse(weekShifts.isEmpty(), "There should be at least one current‐week shift");

        ShiftDTO chosen = weekShifts.get(0);
        assertEquals(2, chosen.getRequiredCounts().get("Cashier"));
        assertEquals(1, chosen.getRequiredCounts().get("Cleaner"));
    }

    @Test
    void processSwapRequests_swapsAssignmentsAndRemovesRequests() throws ParseException {
        // 1) Add two employees and assign them to two different shifts
        roleService.addRole(new RoleDTO("Worker"));
        EmployeeDTO e1 = new EmployeeDTO();
        e1.setId("SW1");
        e1.setName("SwapOne");
        e1.setRoles(Collections.singletonList(new RoleDTO("Worker")));
        e1.setBankAccount("B1");
        e1.setSalary(2000f);
        e1.setEmploymentDate(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-01"));
        employeeService.addEmployee(e1);
        employeeService.setPassword("SW1", "pw1");

        EmployeeDTO e2 = new EmployeeDTO();
        e2.setId("SW2");
        e2.setName("SwapTwo");
        e2.setRoles(Collections.singletonList(new RoleDTO("Worker")));
        e2.setBankAccount("B2");
        e2.setSalary(2000f);
        e2.setEmploymentDate(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-02"));
        employeeService.addEmployee(e2);
        employeeService.setPassword("SW2", "pw2");

        // 2) Create two shifts and assign SW1→ShiftA, SW2→ShiftB under role “Worker”
        shiftService.addTemplate(new HR.DTO.ShiftTemplateDTO(DayOfWeek.TUESDAY, HR.Domain.Shift.ShiftTime.Morning, Collections.singletonMap("Worker",1)));
        shiftService.addTemplate(new HR.DTO.ShiftTemplateDTO(DayOfWeek.WEDNESDAY, HR.Domain.Shift.ShiftTime.Morning, Collections.singletonMap("Worker",1)));

        shiftService.configureShiftRoles("ShiftA", Collections.singletonMap("Worker",1));
        shiftService.configureShiftRoles("ShiftB", Collections.singletonMap("Worker",1));
        shiftService.assignEmployeeToShift("ShiftA", "SW1", "Worker");
        shiftService.assignEmployeeToShift("ShiftB", "SW2", "Worker");

        // 3) Both employees send swap requests
        SwapService swapSvc = SwapService.getInstance();
        swapSvc.sendSwapRequest("SW1", "ShiftA", "Worker");
        swapSvc.sendSwapRequest("SW2", "ShiftB", "Worker");

        List<SwapRequestDTO> pending = swapSvc.getSwapRequests();
        assertEquals(2, pending.size());

        // 4) Now process via HRInterface.processSwapRequests:
        HRInterface ui = new HRInterface("HR1");
        // Present the two requests, pick 1 then pick 2:
        StringBuilder sb = new StringBuilder();
        sb.append("1\n");  // select first request
        sb.append("1\n");  // select “the only compatible one” (index 1 of the filtered list)
        Scanner scanner = new Scanner(new ByteArrayInputStream(sb.toString().getBytes()));
        ui.processSwapRequests(scanner);

        // 5) After swapping:
        // ShiftA should now contain SW2 in role “Worker”
        ShiftDTO aNow = shiftService.getShiftById("ShiftA");
        assertTrue(aNow.getAssignedEmployees().stream()
                .anyMatch(sa -> sa.getEmployeeId().equals("SW2") && sa.getRoleName().equals("Worker")));

        // ShiftB should now contain SW1
        ShiftDTO bNow = shiftService.getShiftById("ShiftB");
        assertTrue(bNow.getAssignedEmployees().stream()
                .anyMatch(sa -> sa.getEmployeeId().equals("SW1") && sa.getRoleName().equals("Worker")));

        // 6) All swap requests should have been deleted
        assertTrue(swapSvc.getSwapRequests().isEmpty());
    }
}
