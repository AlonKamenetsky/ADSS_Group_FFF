package HR.tests.PresentationTests;

import HR.DTO.EmployeeDTO;
import HR.DTO.ShiftDTO;
import HR.DTO.ShiftAssignmentDTO;
import HR.DTO.WeeklyAvailabilityDTO;
import HR.Presentation.EmployeeInterface;
import HR.Service.EmployeeService;
import HR.Service.ShiftService;
import HR.Service.SwapService;
import HR.DataAccess.*;
import HR.Domain.*;
import Util.Database;
import org.junit.jupiter.api.*;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeInterfaceTest {

    private static Connection conn;
    private static EmployeeService employeeService;
    private static ShiftService shiftService;
    private static SwapService swapService; // assuming it uses its own in-memory DAOs

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeAll
    static void setUpDatabase() throws Exception {
        // In-memory SQLite
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement st = conn.createStatement()) {
            // Create minimal tables for employees, roles, shifts, employee_roles, driver_info, shift_assignments, holidays, weekly_availability
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
                CREATE TABLE weekly_availability (
                  employee_id TEXT,
                  available_date DATE,
                  time TEXT
                );
                """);
            st.execute("""
                CREATE TABLE holidays (
                  employee_id TEXT,
                  holiday_date DATE
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
        // Override global connection

        // Initialize singletons
        employeeService = EmployeeService.getInstance();
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
            st.execute("DELETE FROM holidays;");
            st.execute("DELETE FROM weekly_availability;");
            st.execute("DELETE FROM employee_roles;");
            st.execute("DELETE FROM employees;");
            st.execute("DELETE FROM roles;");
            st.execute("DELETE FROM swap_requests;");
        }
        // Reset services (they keep internal caches)
        // For simplicity in tests, re‐initialize singletons with reflection or just rely on fresh tables
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
    void viewMyInfo_noEmployee_printsNotFound() {
        EmployeeInterface ui = new EmployeeInterface("nonexistent");
        ui.viewMyInfo();

        String output = outContent.toString();
        assertTrue(output.contains("Employee not found."));
    }

    @Test
    void viewMyInfo_existingEmployee_printsInfo() throws Exception {
        // 1) Create role
        RoleDAO roleDao = new RoleDAOImpl(conn);
        roleDao.insert(new Role("Cashier"));
        // 2) Create employee domain and DAO
        Employee emp = new Employee("e1", List.of(new Role("Cashier")), "Alice", "hashed", "BANK1", 3000f, new Date());
        EmployeeDAO empDao = new EmployeeDAOImpl(conn);
        empDao.insert(emp);
        // 3) Insert employee_role
        try (var ps = conn.prepareStatement("INSERT INTO employee_roles(employee_id, role_name) VALUES(?,?)")) {
            ps.setString(1, "e1");
            ps.setString(2, "Cashier");
            ps.executeUpdate();
        }

        EmployeeInterface ui = new EmployeeInterface("e1");
        ui.viewMyInfo();

        String output = outContent.toString();
        assertTrue(output.contains("Employee Name: Alice"));
        assertTrue(output.contains("Employee ID: e1"));
        assertTrue(output.contains("Employee Roles: "));
        assertTrue(output.contains("Employee Bank Account: BANK1"));
        assertTrue(output.contains("Employee Salary: 3000.0"));
        assertTrue(output.contains("Employee Employment Date: "));
    }

    @Test
    void viewWeeklyAvailability_empty_printsNoAvailability() {
        // Create role and employee so getEmployeeAvailabilityThisWeek returns empty
        try {
            RoleDAO roleDao = new RoleDAOImpl(conn);
            roleDao.insert(new Role("Cashier"));
            Employee emp = new Employee("e2", List.of(new Role("Cashier")), "Bob", "hash", "BANK2", 2500f, new Date());
            EmployeeDAO empDao = new EmployeeDAOImpl(conn);
            empDao.insert(emp);
            try (var ps = conn.prepareStatement("INSERT INTO employee_roles(employee_id, role_name) VALUES(?,?)")) {
                ps.setString(1, "e2");
                ps.setString(2, "Cashier");
                ps.executeUpdate();
            }
        } catch (Exception ignored) {}

        EmployeeInterface ui = new EmployeeInterface("e2");
        ui.viewWeeklyAvailability();

        String output = outContent.toString();
        assertTrue(output.contains("No availability set for this week."));
    }

    @Test
    void addVacation_success_and_viewHolidays_printsCorrectly() throws Exception {
        // Set up employee
        RoleDAO roleDao = new RoleDAOImpl(conn);
        roleDao.insert(new Role("Warehouse"));
        Employee emp = new Employee("e3", List.of(new Role("Warehouse")), "Carol", "hash", "BANK3", 2700f, new Date());
        EmployeeDAO empDao = new EmployeeDAOImpl(conn);
        empDao.insert(emp);
        try (var ps = conn.prepareStatement("INSERT INTO employee_roles(employee_id, role_name) VALUES(?,?)")) {
            ps.setString(1, "e3");
            ps.setString(2, "Warehouse");
            ps.executeUpdate();
        }

        // Simulate user input for a vacation date
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String input = dateStr + "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        EmployeeInterface ui = new EmployeeInterface("e3");
        ui.addVacation(scanner);
        outContent.reset();

        ui.viewHolidays();
        String output = outContent.toString();
        assertTrue(output.contains(dateStr));
    }

    @Test
    void viewAssignedShifts_noneAssigned_printsMessage() {
        // Employee without any assignments
        try {
            RoleDAO roleDao = new RoleDAOImpl(conn);
            roleDao.insert(new Role("Cleaner"));
            Employee emp = new Employee("e4", List.of(new Role("Cleaner")), "Dave", "hash", "BANK4", 2600f, new Date());
            EmployeeDAO empDao = new EmployeeDAOImpl(conn);
            empDao.insert(emp);
            try (var ps = conn.prepareStatement("INSERT INTO employee_roles(employee_id, role_name) VALUES(?,?)")) {
                ps.setString(1, "e4");
                ps.setString(2, "Cleaner");
                ps.executeUpdate();
            }
        } catch (Exception ignored) {}

        EmployeeInterface ui = new EmployeeInterface("e4");
        ui.viewAssignedShifts();

        String output = outContent.toString();
        assertTrue(output.contains("You are not assigned to any shifts this week."));
    }

    @Test
    void sendSwapRequest_noShifts_printsCannotRequest() {
        // Employee with no assigned shifts
        try {
            RoleDAO roleDao = new RoleDAOImpl(conn);
            roleDao.insert(new Role("Driver"));
            Employee emp = new Employee("e5", List.of(new Role("Driver")), "Eve", "hash", "BANK5", 3100f, new Date());
            EmployeeDAO empDao = new EmployeeDAOImpl(conn);
            empDao.insert(emp);
            try (var ps = conn.prepareStatement("INSERT INTO employee_roles(employee_id, role_name) VALUES(?,?)")) {
                ps.setString(1, "e5");
                ps.setString(2, "Driver");
                ps.executeUpdate();
            }
        } catch (Exception ignored) {}

        String input = ""; // no prompts needed
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        EmployeeInterface ui = new EmployeeInterface("e5");
        ui.sendSwapRequest(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("No assigned shifts; cannot request a swap."));
    }
}
