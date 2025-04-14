package tests.PresentationTests;

import Domain.Availability;
import Domain.Employee;
import Domain.Role;
import Domain.Shift;
import Domain.ShiftAssignment;
import Domain.ShiftsRepo;
import Domain.SwapRequest;
import Domain.SwapRequestsRepo;
import Presentation.EmployeeInterface;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeInterfaceTest {

    private Employee employee;
    private EmployeeInterface employeeInterface;
    private SimpleDateFormat dateFormat;

    @BeforeEach
    void setUp() throws Exception {
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date employmentDate = dateFormat.parse("01-01-2020");
        Role cashierRole = new Role("Cashier");
        // Create employee with an empty weekly availability and holidays list.
        employee = new Employee("1", List.of(cashierRole), "Dana", "123", "Bank", 5000f, employmentDate);
        // Ensure holidays list is empty.
        employee.getHolidays().clear();
        employee.getWeeklyAvailability().clear();
        // Reset assigned shifts by clearing ShiftsRepo.
        ShiftsRepo.getInstance().getShifts().clear();
        // Create EmployeeInterface with the employee.
        employeeInterface = new EmployeeInterface(employee);
        // Also clear swap requests.
        SwapRequestsRepo.getInstance().getSwapRequests().clear();
    }

    @AfterEach
    void tearDown() {
        // Clear the ShiftsRepo and SwapRequestsRepo after each test.
        ShiftsRepo.getInstance().getShifts().clear();
        SwapRequestsRepo.getInstance().getSwapRequests().clear();
    }

    @Test
    void testEmployeeMainMenuReceivesExit() {
        // For the main menu, we only test that the method consumes the exit command.
        // Options: 7 (exit) is the last option.
        String simulatedInput = "7\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner scanner = new Scanner(in);
        // When choosing exit, the method should complete without error.
        // (Since the method itself writes to System.out, we can only check that no exception is thrown.)
        employeeInterface.employeeMainMenu(scanner);
    }

    @Test
    void testAddVacation() {
        // Simulate input for addVacation.
        String input = "15-08-2025\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);
        int initialSize = employee.getHolidays().size();
        employeeInterface.addVacation(scanner);
        assertEquals(initialSize + 1, employee.getHolidays().size(), "Vacation should be added");
    }

    @Test
    void testChooseAvailableShifts() throws Exception {
        // For chooseAvailableShifts, simulate input to mark availability.
        // Create two shifts in ShiftsRepo.
        ShiftsRepo.getInstance().getShifts().clear();
        Date shiftDate1 = dateFormat.parse("10-05-2025");
        Map<Role, ArrayList<Employee>> reqRoles = new HashMap<>();
        Map<Role, Integer> reqCounts = new HashMap<>();
        // For testing, use a dummy role.
        Role testRole = new Role("Cashier");
        reqRoles.put(testRole, new ArrayList<>());
        reqCounts.put(testRole, 1);
        Shift shift = new Shift("SHIFT1", shiftDate1, Shift.ShiftTime.Morning, reqRoles, reqCounts);
        ShiftsRepo.getInstance().addShift(shift);

        // Simulate input: choose shift (since availability is empty, the only shift will be shown),
        // then finish selection by entering 0.
        String simulatedInput = "1\n0\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner scanner = new Scanner(in);
        employeeInterface.chooseAvailableShifts(scanner, ShiftsRepo.getInstance().getShifts());

        // After choosing the shift, the employee's weekly availability should now include an entry.
        assertFalse(employee.getWeeklyAvailability().isEmpty(), "Weekly availability should be updated");
        // Optionally check that the availability matches the shift's date and type.
        Availability avail = employee.getWeeklyAvailability().get(0);
        assertEquals(shiftDate1, avail.getDate());
        assertEquals(Shift.ShiftTime.Morning, avail.getType());
    }

    @Test
    void testSendSwapRequest() throws Exception {
        // Create a shift to which the employee is assigned.
        ShiftsRepo.getInstance().getShifts().clear();
        Date shiftDate = dateFormat.parse("10-05-2025");
        Map<Role, ArrayList<Employee>> reqRoles = new HashMap<>();
        Map<Role, Integer> reqCounts = new HashMap<>();
        Role cashier = new Role("Cashier");
        reqRoles.put(cashier, new ArrayList<>());
        reqCounts.put(cashier, 1);
        Shift shift = new Shift("SHIFT1", shiftDate, Shift.ShiftTime.Morning, reqRoles, reqCounts);
        // Simulate assignment: add a ShiftAssignment for the employee.
        shift.assignEmployee(employee, cashier);
        // Ensure the employee is assigned to the shift (via ShiftsRepo).
        ShiftsRepo.getInstance().addShift(shift);

        // Simulate input for sendSwapRequest:
        // The method should display the shifts the employee is assigned to (only one) and then ask for a selection.
        // For simplicity, we select option 1.
        String simulatedInput = "1\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner scanner = new Scanner(in);
        // Before sending the swap request, clear the repository.
        SwapRequestsRepo.getInstance().getSwapRequests().clear();
        employeeInterface.sendSwapRequest(scanner);
        // Verify that a swap request was added.
        assertEquals(1, SwapRequestsRepo.getInstance().getSwapRequests().size(), "A swap request should be created");
        SwapRequest req = SwapRequestsRepo.getInstance().getSwapRequests().get(0);
        assertEquals("SHIFT1", req.getShift().getID());
        // And the employee should be the one sending it.
        assertEquals(employee, req.getEmployee());
    }

    @Test
    void testViewAssignedShiftsWhenNone() {
        // Clear any assignments from all shifts (if any).
        // For this test, ensure the employee is not assigned to any shift.
        ShiftsRepo.getInstance().getShifts().clear();
        // Capture output if necessary (here we simply call the method).
        employeeInterface.viewAssignedShifts(ShiftsRepo.getInstance().getShifts());
        // No assertion here; in a more advanced test, you could capture System.out to verify the message.
    }

    @Test
    void testViewWeeklyAvailability() throws Exception {
        // Add an availability, then call viewWeeklyAvailability.
        Date testDate = dateFormat.parse("10-05-2025");
        employee.addAvailability(testDate, Shift.ShiftTime.Morning);
        // Call the method; for now we'll assume it prints the output.
        employeeInterface.viewWeeklyAvailability();
        // No assertion is made; advanced testing may capture System.out.
    }

    @Test
    void testViewHolidays() throws Exception {
        // Add a holiday and then call viewHolidays.
        Date holiday = dateFormat.parse("15-08-2025");
        employee.getHolidays().add(holiday);
        employeeInterface.viewHolidays();
    }
}
