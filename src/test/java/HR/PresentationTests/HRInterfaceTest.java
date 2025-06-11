package HR.tests.PresentationTests;

import HR.DTO.*;
import HR.Presentation.HRInterface;
import HR.Service.EmployeeService;
import HR.Service.RoleService;
import HR.Service.ShiftService;
import HR.Service.SwapService;
import HR.DataAccess.*;
import HR.Domain.*;
import Util.Database;
import org.junit.jupiter.api.*;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class HRInterfaceTest {

    private static Connection conn;
    private static EmployeeService employeeService;
    private static RoleService roleService;
    private static ShiftService shiftService;
    private static SwapService swapService;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeAll
    static void setUpDatabase() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement st = conn.createStatement()) {
            // Create tables used by RoleService, EmployeeService, ShiftService, SwapService
            st.execute("""
                CREATE TABLE roles (
                  name TEXT PRIMARY KEY
                );
                """);
            st.execute("""
                CREATE TABLE employees (
                  id TEXT PRIMARY KEY,
                  name TEXT,
                  password TEXT,
                  bank_account TEXT,
                  salary REAL,
                  employment_date DATE
                );
                """);
            st.execute("""
                CREATE TABLE employee_roles (
                  employee_id TEXT,
                  role_name TEXT,
                  PRIMARY KEY(employee_id, role_name),
                  FOREIGN KEY(employee_id) REFERENCES employees(id),
                  FOREIGN KEY(role_name) REFERENCES roles(name)
                );
                """);
            st.execute("""
                CREATE TABLE driver_info (
                  employee_id TEXT PRIMARY KEY,
                  licenses TEXT
                );
                """);
            st.execute("""
                CREATE TABLE shifts (
                  id TEXT PRIMARY KEY,
                  date DATE,
                  type TEXT
                );
                """);
            st.execute("""
                CREATE TABLE shift_assignments (
                  employee_id TEXT,
                  shift_id TEXT,
                  role_name TEXT
                );
                """);
            st.execute("""
                CREATE TABLE swap_requests (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  employee_id TEXT,
                  shift_id TEXT,
                  role_name TEXT
                );
                """);
        }

        employeeService = EmployeeService.getInstance();
        roleService = RoleService.getInstance();
        shiftService = ShiftService.getInstance();
        swapService = SwapService.getInstance();
    }

    @AfterAll
    static void tearDownDatabase() throws Exception {
        conn.close();
    }

    @BeforeEach
    void clearTables() throws Exception {
        try (Statement st = conn.createStatement()) {
            st.execute("DELETE FROM shift_assignments;");
            st.execute("DELETE FROM shifts;");
            st.execute("DELETE FROM swap_requests;");
            st.execute("DELETE FROM driver_info;");
            st.execute("DELETE FROM employee_roles;");
            st.execute("DELETE FROM employees;");
            st.execute("DELETE FROM roles;");
        }
    }

    @BeforeEach
    void captureOutput() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    void addNewRole_and_removeRole_flow() {
        // Set up interface with HR role
        RoleDAO roleDao = new RoleDAOImpl(conn);
        roleDao.insert(new Role("HR"));
        Employee hrEmp = new Employee("hr1", List.of(new Role("HR")), "HRUser", "pw", "BA", 4000f, new Date());
        EmployeeDAO empDao = new EmployeeDAOImpl(conn);
        empDao.insert(hrEmp);
        try (var ps = conn.prepareStatement("INSERT INTO employee_roles(employee_id, role_name) VALUES(?,?)")) {
            ps.setString(1, "hr1");
            ps.setString(2, "HR");
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        HRInterface hrUi = new HRInterface("hr1");

        // 1) Add a new role "TestRole"
        String inputAdd = "TestRole\n";
        Scanner scannerAdd = new Scanner(new ByteArrayInputStream(inputAdd.getBytes()));
        hrUi.addNewRole(scannerAdd);
        outContent.reset();

        var rolesAfterAdd = roleService.getRoles();
        assertTrue(rolesAfterAdd.stream().anyMatch(r -> r.getName().equals("TestRole")));

        // 2) Remove the new role
        // Prepare input to select "TestRole" (list has at least HR and TestRole).
        List<RoleDTO> allRoles = roleService.getRoles();
        int index = -1;
        for (int i = 0; i < allRoles.size(); i++) {
            if (allRoles.get(i).getName().equals("TestRole")) {
                index = i + 1;
                break;
            }
        }
        assertTrue(index > 0);

        // Confirm "yes"
        String inputRemove = index + "\nyes\n";
        Scanner scannerRemove = new Scanner(new ByteArrayInputStream(inputRemove.getBytes()));
        hrUi.removeRole(scannerRemove);
        outContent.reset();

        var rolesAfterRemove = roleService.getRoles();
        assertFalse(rolesAfterRemove.stream().anyMatch(r -> r.getName().equals("TestRole")));
    }

    @Test
    void assignEmployeeToShift_endToEnd() throws Exception {
        // 1) Seed roles
        roleService.addRole(new RoleDTO("Cashier"));
        roleService.addRole(new RoleDTO("Cleaner"));

        // 2) Create an employee with role "Cashier"
        CreateEmployeeDTO create = new CreateEmployeeDTO();
        create.setId("empA");
        create.setName("Alice");
        create.setRoles(List.of(new RoleDTO("Cashier")));
        create.setRawPassword("pw");
        create.setBankAccount("BA1");
        create.setSalary(3000f);
        create.setEmploymentDate(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-01"));
        employeeService.addEmployee(create);

        // 3) Insert assignment into employee_roles
        try (var ps = conn.prepareStatement("INSERT INTO employee_roles(employee_id, role_name) VALUES(?,?)")) {
            ps.setString(1, "empA");
            ps.setString(2, "Cashier");
            ps.executeUpdate();
        }

        // 4) Create a Shift
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        java.sql.Date sqlDate = java.sql.Date.valueOf(tomorrow);
        String shiftId = "s-" + tomorrow + "-Morning";
        // requiredCounts: Cashier=1
        Map<Role, Integer> reqCounts = Map.of(new Role("Cashier"), 1);
        Map<Role, ArrayList<Employee>> reqRoles = Map.of(new Role("Cashier"), new ArrayList<>());
        ShiftDAO shiftDao = new ShiftDAOImpl(conn);
        shiftDao.insert(new Shift(shiftId, sqlDate, Shift.ShiftTime.Morning, reqRoles, reqCounts));

        HRInterface hrUi = new HRInterface("hr1");
        outContent.reset();

        // 5) Call assignEmployeeToShift with inputs:
        //    - choose shift 1
        //    - choose role 1 (Cashier)
        //    - choose employee 1 (Alice)
        String input = "1\n1\n1\n0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        hrUi.assignEmployeeToShift(scanner);

        // 6) Verify that shift_assignments table now has a row for empA / shiftId / "Cashier"
        List<ShiftDTO> assignedShifts = shiftService.getAssignedShifts("empA");
        assertEquals(1, assignedShifts.size());
        ShiftDTO assigned = assignedShifts.get(0);
        assertEquals(shiftId, assigned.getId());
        assertTrue(
                assigned.getAssignedEmployees().stream()
                        .anyMatch(sa -> sa.getEmployeeId().equals("empA") && sa.getRoleName().equals("Cashier"))
        );
    }

    @Test
    void processSwapRequests_flow() throws Exception {
        // 1) Seed roles, employee, shift, assignment
        roleService.addRole(new RoleDTO("Cashier"));
        employeeService.addEmployee(new CreateEmployeeDTO() {{
            setId("e1");
            setName("E1");
            setRawPassword("pw");
            setRoles(List.of(new RoleDTO("Cashier")));
            setBankAccount("BA1");
            setSalary(2000f);
            setEmploymentDate(new SimpleDateFormat("yyyy-MM-dd").parse("2020-01-01"));
        }});
        employeeService.addEmployee(new CreateEmployeeDTO() {{
            setId("e2");
            setName("E2");
            setRawPassword("pw");
            setRoles(List.of(new RoleDTO("Cashier")));
            setBankAccount("BA2");
            setSalary(2100f);
            setEmploymentDate(new SimpleDateFormat("yyyy-MM-dd").parse("2020-01-02"));
        }});
        // Insert employee_roles
        try (var ps = conn.prepareStatement("INSERT INTO employee_roles(employee_id, role_name) VALUES(?,?)")) {
            ps.setString(1, "e1"); ps.setString(2, "Cashier"); ps.executeUpdate();
            ps.setString(1, "e2"); ps.setString(2, "Cashier"); ps.executeUpdate();
        }

        // Create shift
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        java.sql.Date sqlDate = java.sql.Date.valueOf(tomorrow);
        Map<Role, Integer> reqCounts = Map.of(new Role("Cashier"), 1);
        Map<Role, ArrayList<Employee>> reqRoles = Map.of(new Role("Cashier"), new ArrayList<>());
        String shiftId = "swapShift";
        new ShiftDAOImpl(conn).insert(new Shift(shiftId, sqlDate, Shift.ShiftTime.Morning, reqRoles, reqCounts));

        // Assign e1 and e2 to the same shift under same role
        shiftService.assignEmployeeToShift(shiftId, "e1", "Cashier");
        shiftService.assignEmployeeToShift(shiftId, "e2", "Cashier");
        outContent.reset();

        // 2) Send two swap requests
        String sendInput1 = "";
        Scanner scanner1 = new Scanner(new ByteArrayInputStream(sendInput1.getBytes()));
        swapService.sendSwapRequest("e1", shiftId, "Cashier");
        swapService.sendSwapRequest("e2", shiftId, "Cashier");

        // 3) Process swap requests (simulate choosing request 1, then compatible 1)
        HRInterface hrUi = new HRInterface("hr1");
        // Build inputs: pick first swap request (1), pick the compatible second in the filtered list (1)
        String processInput = "1\n1\n";
        Scanner scanner2 = new Scanner(new ByteArrayInputStream(processInput.getBytes()));
        hrUi.processSwapRequests(scanner2);

        // 4) After processing, both e1 and e2 should have swapped assignments:
        //    i.e. e1 now has e2’s slot and vice versa. Verify by calling getAssignedShifts for each:
        List<ShiftDTO> e1Shifts = shiftService.getAssignedShifts("e1");
        List<ShiftDTO> e2Shifts = shiftService.getAssignedShifts("e2");
        assertEquals(1, e1Shifts.size());
        assertEquals("Cashier",
                e1Shifts.get(0).getAssignedEmployees().stream()
                        .filter(sa -> sa.getEmployeeId().equals("e1"))
                        .findFirst().get().getRoleName()
        );
        assertEquals(1, e2Shifts.size());
        assertEquals("Cashier",
                e2Shifts.get(0).getAssignedEmployees().stream()
                        .filter(sa -> sa.getEmployeeId().equals("e2"))
                        .findFirst().get().getRoleName()
        );
    }
}
