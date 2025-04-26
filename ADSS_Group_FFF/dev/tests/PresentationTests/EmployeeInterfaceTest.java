package tests.PresentationTests;

import Domain.Employee;
import Domain.Role;
import Domain.RecurringShift;
import Domain.Shift;
import Domain.ShiftAssignment;
import Domain.ShiftsRepo;
import Domain.SwapRequest;
import Domain.SwapRequestsRepo;
import Presentation.EmployeeInterface;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeInterfaceTest {

    private Employee employee;
    private EmployeeInterface ui;
    private SimpleDateFormat df;
    private ShiftsRepo repo;

    @BeforeEach
    void setUp() throws Exception {
        df = new SimpleDateFormat("yyyy-MM-dd");
        Date employmentDate = df.parse("2020-01-01");
        Role cashierRole = new Role("Cashier");

        employee = new Employee(
                "1",
                List.of(cashierRole),
                "Dana",
                "pass1",
                "BANK123",
                5000f,
                employmentDate
        );

        ui = new EmployeeInterface(employee);
        repo = ShiftsRepo.getInstance();

        // clear out any previous schedule/templates/history
        repo.getTemplates().clear();
        repo.getSchedule().getCurrentWeek().clear();
        repo.getSchedule().getNextWeek().clear();
        repo.getHistory().clear();
        // clear swap‐requests
        SwapRequestsRepo.getInstance().getSwapRequests().clear();
    }

    @AfterEach
    void tearDown() {
        // reset again
        repo.getTemplates().clear();
        repo.getSchedule().getCurrentWeek().clear();
        repo.getSchedule().getNextWeek().clear();
        repo.getHistory().clear();
        SwapRequestsRepo.getInstance().getSwapRequests().clear();
    }

    @Test
    void testAddVacationViaMenu() throws Exception {
        // simulate: "5. Add Vacation" → enter date → "7. Exit"
        String input = String.join("\n",
                "6",
                "2025-08-15", // vacation date
                "8"
        ) + "\n";

        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ui.employeeMainMenu(sc);

        assertEquals(1, employee.getHolidays().size(), "Should have exactly one holiday");
        assertEquals(df.parse("2025-08-15"), employee.getHolidays().get(0), "Should be the same date");
    }

    @Test
    void testSendWeeklyAvailabilityViaMenu() {
        // first add a single RecurringShift template for next week
        RecurringShift tmpl = new RecurringShift(DayOfWeek.MONDAY, Shift.ShiftTime.Morning);
        tmpl.setDefaultCount(new Role("Cashier"), 1);
        repo.addTemplate(tmpl);

        // force-building next‐week from template, using last Saturday as ref
        LocalDate saturday = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));
        repo.getSchedule().resetNextWeek(repo.getTemplates(), saturday);

        // simulate: "3. Send Weekly Availability" → pick slot 1 → done (0) → exit (7)
        String input = String.join("\n",
                "3",
                "1",
                "0",
                "8"
        ) + "\n";

        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ui.employeeMainMenu(sc);

        // availabilityNextWeek should now contain exactly one entry for Monday/Morning
        var next = employee.getAvailabilityNextWeek();
        assertEquals(1, next.size(), "Should have picked exactly one slot");
        assertEquals(DayOfWeek.MONDAY, next.get(0).getDay());
        assertEquals(Shift.ShiftTime.Morning, next.get(0).getTime());
    }

    @Test
    void testSendSwapRequestViaMenu() throws Exception {
        // prepare one shift in current week and assign the employee
        Date shiftDate = df.parse("2025-10-01");
        var role = new Role("Cashier");
        var reqRoles = new java.util.HashMap<Role, java.util.ArrayList<Employee>>();
        var reqCounts = new java.util.HashMap<Role, Integer>();
        reqRoles.put(role, new java.util.ArrayList<>());
        reqCounts.put(role, 1);
        Shift s = new Shift("S1", shiftDate, Shift.ShiftTime.Evening, reqRoles, reqCounts);
        // attach assignment
        s.assignEmployee(employee, role);
        repo.getSchedule().getCurrentWeek().clear();
        repo.getSchedule().getCurrentWeek().add(s);

        // simulate: "2. Send Swap Request" → choose 1 → exit 7
        String input = String.join("\n",
                "2",
                "1",
                "8"
        ) + "\n";

        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ui.employeeMainMenu(sc);

        var swaps = SwapRequestsRepo.getInstance().getSwapRequests();
        assertEquals(1, swaps.size(), "One swap request should be created");
        SwapRequest r = swaps.get(0);
        assertEquals("S1", r.getShift().getID());
        assertEquals(employee, r.getEmployee());
    }
}
