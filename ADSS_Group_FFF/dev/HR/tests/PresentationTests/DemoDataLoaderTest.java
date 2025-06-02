package HR.tests.PresentationTests;

import HR.DTO.EmployeeDTO;
import HR.DTO.RoleDTO;
import HR.DTO.ShiftTemplateDTO;
import HR.Presentation.DemoDataLoader;
import HR.Service.EmployeeService;
import HR.Service.RoleService;
import HR.Service.ShiftService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DemoDataLoaderTest {

    private RoleService roleService;
    private EmployeeService employeeService;
    private ShiftService shiftService;

    @BeforeEach
    void setUp() {
        // Assume these singletons can be reset or a fresh test database is in use.
        roleService     = RoleService.getInstance();
        employeeService = EmployeeService.getInstance();
        shiftService    = ShiftService.getInstance();

        // If your services maintain in-memory collections or caches,
        // you might need to clear them here. For example:
        // roleService.clearAll();
        // employeeService.clearAll();
        // shiftService.clearAll();
    }

    @AfterEach
    void tearDown() {
        // Clean up between tests. Again, uncomment or implement if needed:
        // roleService.clearAll();
        // employeeService.clearAll();
        // shiftService.clearAll();
    }

    @Test
    void initializeExampleData_case1_createsExpectedRolesEmployeesAndTemplates() throws ParseException {
        // Call with i = 1
        DemoDataLoader.initializeExampleData(1);

        // 1) Verify roles “HR”, “Shift Manager”, “Cashier”, “Warehouse”, “Cleaner”, “Driver” exist:
        List<RoleDTO> allRoles = roleService.getRoles();
        Set<String> roleNames = allRoles.stream()
                .map(RoleDTO::getName)
                .collect(Collectors.toSet());
        assertTrue(roleNames.contains("HR"),            "Missing role: HR");
        assertTrue(roleNames.contains("Shift Manager"),  "Missing role: Shift Manager");
        assertTrue(roleNames.contains("Cashier"),       "Missing role: Cashier");
        assertTrue(roleNames.contains("Warehouse"),     "Missing role: Warehouse");
        assertTrue(roleNames.contains("Cleaner"),       "Missing role: Cleaner");
        assertTrue(roleNames.contains("Driver"),        "Missing role: Driver");

        // 2) Verify employees “1”, “2”, “hr” now exist:
        EmployeeDTO e1 = employeeService.getEmployeeById("1");
        assertNotNull(e1, "Employee ‘1’ should exist");
        assertEquals("Dana", e1.getName());
        assertTrue(e1.getRoles().contains("Shift Manager"), "Employee1 should have role “Shift Manager”");
        assertTrue(e1.getRoles().contains("Cashier"),       "Employee1 should have role “Cashier”");
        assertEquals("IL123BANK", e1.getBankAccount());
        assertEquals(5000f, e1.getSalary());

        EmployeeDTO e2 = employeeService.getEmployeeById("2");
        assertNotNull(e2, "Employee ‘2’ should exist");
        assertEquals("John", e2.getName());
        assertTrue(e2.getRoles().contains("Warehouse"), "Employee2 should have role “Warehouse”");
        assertTrue(e2.getRoles().contains("Cashier"),   "Employee2 should have role “Cashier”");
        assertEquals("IL456BANK", e2.getBankAccount());
        assertEquals(4500f, e2.getSalary());

        EmployeeDTO ehr = employeeService.getEmployeeById("hr");
        assertNotNull(ehr, "Employee ‘hr’ should exist");
        assertEquals("HR Manager", ehr.getName());
        assertEquals(1, ehr.getRoles().size());
        assertEquals("HR", ehr.getRoles().get(0));
        assertEquals("IL456BANK", ehr.getBankAccount());
        assertEquals(4500f, ehr.getSalary());

        // 3) Verify shift templates: for each DayOfWeek, there should be two templates (Morning + Evening)
        List<ShiftTemplateDTO> templates = shiftService.getTemplates();
        // Exactly 14 entries (7 days × 2 shift‐times)
        assertEquals(7 * 2, templates.size(), "Expected 14 shift templates (7 days x 2 times)");
        // Optionally confirm that each day/time pair appears:
        Set<String> combos = templates.stream()
                .map(dto -> dto.getDay() + ":" + dto.getTime())
                .collect(Collectors.toSet());
        for (java.time.DayOfWeek dow : java.time.DayOfWeek.values()) {
            assertTrue(combos.contains(dow + ":Morning"), "Missing template for " + dow + " Morning");
            assertTrue(combos.contains(dow + ":Evening"), "Missing template for " + dow + " Evening");
        }
    }

    @Test
    void initializeExampleData_case0_addsOnlyHRRole() throws ParseException {
        // Call with i = 0
        // Because case 0 is interactive, it will prompt. For testing, we only verify
        // that after adding “HR” role, the system contains at least one role named “HR”.
        DemoDataLoader.initializeExampleData(0);
        List<RoleDTO> roles = roleService.getRoles();
        assertTrue(roles.stream()
                        .map(RoleDTO::getName)
                        .anyMatch(n -> n.equals("HR")),
                "After case 0, ‘HR’ role should exist");
    }
}
