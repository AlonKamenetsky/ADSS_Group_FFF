package tests.PresentationTests;

import Domain.Employee;
import Domain.Role;
import Domain.Shift;
import Domain.ShiftsRepo;
import Domain.EmployeesRepo;
import Domain.SwapRequest;
import Presentation.HRInterface;
import Domain.RolesRepo;
import Domain.SwapRequestsRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class HRInterfaceTest {

    private HRInterface hrInterface;
    private SimpleDateFormat dateFormat;

    @BeforeEach
    void setUp() throws Exception {
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        // Clear repositories
        RolesRepo.getInstance().getRoles().clear();
        EmployeesRepo.getInstance().getEmployees().clear();
        ShiftsRepo.getInstance().getShifts().clear();
        SwapRequestsRepo.getInstance().getSwapRequests().clear();

        // Set up roles.
        RolesRepo.getInstance().addRole(new Role("Shift Manager"));
        RolesRepo.getInstance().addRole(new Role("Cashier"));
        RolesRepo.getInstance().addRole(new Role("HR"));

        // Set up employees.
        Role cashier = RolesRepo.getInstance().getRoleByName("Cashier");
        Employee emp1 = new Employee("1", List.of(cashier), "Dana", "pass", "Bank", 5000f, dateFormat.parse("01-01-2020"));
        Employee emp2 = new Employee("2", List.of(cashier), "John", "pass", "Bank", 4500f, dateFormat.parse("01-01-2020"));
        EmployeesRepo.getInstance().addEmployee(emp1);
        EmployeesRepo.getInstance().addEmployee(emp2);

        hrInterface = new HRInterface("HRUser");
        // For testing, set current user role to HR.
        hrInterface.setCurrentUserRole(new Role("HR"));

        // Create one shift for assignment tests.
        Map<Role, ArrayList<Employee>> reqRoles = new HashMap<>();
        Map<Role, Integer> reqCounts = new HashMap<>();
        Role shiftManager = RolesRepo.getInstance().getRoleByName("Shift Manager");
        Role cashierRole = RolesRepo.getInstance().getRoleByName("Cashier");
        // For testing, set required count for Shift Manager to 1 and Cashier to 2.
        reqRoles.put(shiftManager, new ArrayList<>());
        reqCounts.put(shiftManager, 1);
        reqRoles.put(cashierRole, new ArrayList<>());
        reqCounts.put(cashierRole, 2);
        Date shiftDate = dateFormat.parse("15-09-2025");
        Shift shift = new Shift("SHIFT1", shiftDate, Shift.ShiftTime.Morning, reqRoles, reqCounts);
        ShiftsRepo.getInstance().addShift(shift);
    }

    @AfterEach
    void tearDown() {
        // Clear repositories after each test.
        RolesRepo.getInstance().getRoles().clear();
        EmployeesRepo.getInstance().getEmployees().clear();
        ShiftsRepo.getInstance().getShifts().clear();
        SwapRequestsRepo.getInstance().getSwapRequests().clear();
    }

    @Test
    void testCreateShift() {
        // Simulate input for createShift.
        // Input: shift date, shift type, and required count for "Cashier" (since "Shift Manager" is auto-set).
        String input = "16-09-2025\n" +  // shift date
                "2\n" +           // shift type: 2 for Evening
                "3\n";            // required number for Cashier
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);
        hrInterface.createShift(scanner);

        // Expect now 2 shifts in ShiftsRepo.
        assertEquals(2, ShiftsRepo.getInstance().getShifts().size());
        Shift createdShift = ShiftsRepo.getInstance().getShifts().get(1);
        assertEquals("SHIFT2", createdShift.getID());
        Role cashier = RolesRepo.getInstance().getRoleByName("Cashier");
        assertEquals(3, createdShift.getRequiredCounts().get(cashier));
    }

    @Test
    void testAssignEmployeeToShift() throws Exception {
        // For assignEmployeeToShift, simulate input for:
        //  - selecting a shift,
        //  - choosing a role (from the printed list),
        //  - selecting a qualified employee.
        // For this test, we need to ensure that one employee is available.
        // We already have two employees with role Cashier.

        // Simulate input:
        // First, select the only shift: "1"
        // Then, in the role menu, choose "1" (assume that role 1 is Shift Manager or Cashier depending on order)
        // Since the test has two roles (Shift Manager and Cashier), we assume the printed order is defined by keySet iteration.
        // For our test, we wish to assign a Cashier. Let’s assume that Cashier is option 2.
        // Then, select employee "1".
        String input = "1\n" +  // select shift SHIFT1
                "2\n" +  // select role option 2 (Cashier)
                "1\n"+
                "2\n"+
                "1\n"+
                "0\n";  // select employee option 2 (John)



        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        // Call method.
        hrInterface.assignEmployeeToShift(scanner, EmployeesRepo.getInstance().getEmployees(), ShiftsRepo.getInstance().getShifts());

        // Verify that the chosen employee is now assigned to the shift for the Cashier role.
        Shift shift = ShiftsRepo.getInstance().getShifts().get(0);
        Role cashier = RolesRepo.getInstance().getRoleByName("Cashier");
        List<Employee> assignedForCashier = shift.getRequiredRoles().get(cashier);
        assertNotNull(assignedForCashier);
        // Since required count for cashier is 2, and we just assigned one employee, the list should have size 1.
        assertEquals(2, assignedForCashier.size());
    }

    @Test
    void testProcessSwapRequests() throws Exception {
        // Create two swap requests with compatible data.
        // For testing, create a dummy shift and assign two different employees to it.
        Role cashier = RolesRepo.getInstance().getRoleByName("Cashier");
        Employee emp1 = EmployeesRepo.getInstance().getEmployeeById("1");
        Employee emp2 = EmployeesRepo.getInstance().getEmployeeById("2");
        Date shiftDate = dateFormat.parse("20-09-2025");
        Map<Role, ArrayList<Employee>> reqRoles = new HashMap<>();
        Map<Role, Integer> reqCounts = new HashMap<>();
        reqRoles.put(cashier, new ArrayList<>());
        reqCounts.put(cashier, 2);
        Shift shift = new Shift("SHIFT3", shiftDate, Shift.ShiftTime.Morning, reqRoles, reqCounts);
        Shift shift2 = new Shift("SHIFT4", shiftDate, Shift.ShiftTime.Evening, reqRoles, reqCounts);
        shift.assignEmployee(emp1, cashier);
        shift2.assignEmployee(emp2, cashier);
        ShiftsRepo.getInstance().addShift(shift);

        // Create two swap requests manually.
        SwapRequest req1 = new SwapRequest(emp1, shift, cashier);
        SwapRequest req2 = new SwapRequest(emp2, shift2, cashier);
        SwapRequestsRepo.getInstance().getSwapRequests().clear();
        SwapRequestsRepo.getInstance().addSwapRequest(req1);
        SwapRequestsRepo.getInstance().addSwapRequest(req2);

        // Simulate input for processSwapRequests:
        // The method should display two requests, let you choose one (choose option 1),
        // then show compatible ones (the other one, option 1) and then choose that.
        String input = "1\n1\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);
        hrInterface.processSwapRequests(scanner);
        // After processing, both swap requests should be removed.
        assertEquals(0, SwapRequestsRepo.getInstance().getSwapRequests().size());
    }

    @Test
    void testManagerMainMenuReceivesExit() {
        // Test that the manager main menu consumes input "9" to exit.
        // We simulate input "9" (exit) and verify that the method completes.
        String simulatedInput = "9\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner scanner = new Scanner(in);
        hrInterface.managerMainMenu(scanner);
        // No exceptions thrown means the input was received.
    }

    @Test
    void testAddNewRole() {
        // Simulate input for addNewRole.
        String roleName = "TestRole";
        String input = roleName + "\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);
        int initialSize = RolesRepo.getInstance().getRoles().size();
        hrInterface.addNewRole(scanner);
        assertEquals(initialSize + 1, RolesRepo.getInstance().getRoles().size());
        assertNotNull(RolesRepo.getInstance().getRoleByName(roleName));
    }

    @Test
    void testUpdateEmployeeData() {
        // For testing updateEmployeeData, simulate input to update bank account.
        // For simplicity, we use the first employee from EmployeesRepo.
        List<Employee> employees = EmployeesRepo.getInstance().getEmployees();
        if (employees.isEmpty()) {
            fail("No employees available for update test.");
        }
        // Simulate: choose employee 1, choose option 1 (bank account), enter new bank account, then 3 to exit.
        String input = "1\n1\nNEWBANK\n3\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);
        hrInterface.updateEmployeeData(scanner, employees);
        assertEquals("NEWBANK", employees.get(0).getBankAccount());
    }

    @Test
    void testRemoveRole() {
        // Simulate input for removeRole.
        // First, add a role to ensure there is one to remove.
        Role roleToRemove = new Role("TempRole");
        RolesRepo.getInstance().addRole(roleToRemove);
        int initialSize = RolesRepo.getInstance().getRoles().size();

        String input = "4\n"; // choosing the last role and confirming removal.
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);
        hrInterface.removeRole(scanner);
        assertEquals(initialSize - 1, RolesRepo.getInstance().getRoles().size());
        assertNull(RolesRepo.getInstance().getRoleByName("TempRole"));
    }

    @Test
    void testAddEmployee() throws Exception {
        // Simulate input for addEmployee.
        // Inputs: Employee ID, Name, Password, Bank Account, Salary, Employment Date, and Role.
        String input = "3\nNewEmployee\npass\nNewBank\n6000\n01-06-2020\nCashier\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);
        int initialSize = EmployeesRepo.getInstance().getEmployees().size();
        hrInterface.addEmployee(scanner, EmployeesRepo.getInstance().getEmployees());
        assertEquals(initialSize + 1, EmployeesRepo.getInstance().getEmployees().size());
        assertNotNull(EmployeesRepo.getInstance().getEmployeeById("3"));
    }

    @Test
    void testRemoveEmployee() {
        // First, add an employee to remove.
        Employee tempEmployee = new Employee("temp", List.of(new Role("Cashier")), "Temp", "pass", "Bank", 3000f, new Date());
        EmployeesRepo.getInstance().addEmployee(tempEmployee);
        int initialSize = EmployeesRepo.getInstance().getEmployees().size();

        String input = """
                3
                yes
                """; // choosing the first employee and confirming removal.
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);
        hrInterface.removeEmployee(scanner, EmployeesRepo.getInstance().getEmployees());
        assertEquals(initialSize - 1, EmployeesRepo.getInstance().getEmployees().size());
        assertNull(EmployeesRepo.getInstance().getEmployeeById("temp"));
    }
}
