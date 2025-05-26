package HR.tests.PresentationTests;

import HR.DataAccess.EmployeesRepo;
import HR.DataAccess.RolesRepo;
import HR.DataAccess.ShiftDAOImpl;
import HR.DataAccess.WeeklyAvailabilityDAO;
import HR.Domain.*;
import HR.tests.Domain.*;
import HR.Presentation.HRInterface;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class HRInterfaceTest {

    private HRInterface ui;
    private SimpleDateFormat df;

    @BeforeEach
    void setUp() throws Exception {
        df = new SimpleDateFormat("dd-MM-yyyy");

        // clear out all singletons
        RolesRepo.getInstance().getRoles().clear();
        EmployeesRepo.getInstance().getEmployees().clear();
        WeeklyAvailabilityDAO.ShiftsRepo.getInstance().getSchedule().getCurrentWeek().clear();
        WeeklyAvailabilityDAO.ShiftsRepo.getInstance().getSchedule().getNextWeek().clear();
        WeeklyAvailabilityDAO.ShiftsRepo.getInstance().getTemplates().clear();
        WeeklyAvailabilityDAO.ShiftsRepo.getInstance().getHistory().clear();
        ShiftDAOImpl.SwapRequestsRepo.getInstance().getSwapRequests().clear();

        // seed roles
        RolesRepo rs = RolesRepo.getInstance();
        rs.addRole(new Role("HR"));
        rs.addRole(new Role("Shift Manager"));
        rs.addRole(new Role("Cashier"));

        // seed employees
        Role cashier = rs.getRoleByName("Cashier");
        Employee e1 = new Employee("1", List.of(cashier), "Alice", "p1", "B1", 4000f, df.parse("01-01-2020"));
        Employee e2 = new Employee("2", List.of(cashier), "Bob",   "p2", "B2", 3500f, df.parse("01-01-2020"));
        EmployeesRepo.getInstance().addEmployee(e1);
        EmployeesRepo.getInstance().addEmployee(e2);

        // seed one shift
        Map<Role, ArrayList<Employee>> reqRoles = new HashMap<>();
        Map<Role, Integer>           reqCnts  = new HashMap<>();
        Role mgr = rs.getRoleByName("Shift Manager");
        reqRoles.put(mgr,       new ArrayList<>());
        reqCnts .put(mgr,       1);
        reqRoles.put(cashier,   new ArrayList<>());
        reqCnts .put(cashier,   2);
        Date shiftDate = df.parse("15-09-2025");
        Shift s = new Shift("SHIFT1", shiftDate, Shift.ShiftTime.Morning, reqRoles, reqCnts);
        WeeklyAvailabilityDAO.ShiftsRepo.getInstance().getSchedule().getCurrentWeek().add(s);

        // construct UI and give it HR privileges
        ui = new HRInterface("HRUser");
        ui.setCurrentUserRole(rs.getRoleByName("HR"));
    }

    @AfterEach
    void tearDown() {
        RolesRepo.getInstance().getRoles().clear();
        EmployeesRepo.getInstance().getEmployees().clear();
        WeeklyAvailabilityDAO.ShiftsRepo.getInstance().getSchedule().getCurrentWeek().clear();
        WeeklyAvailabilityDAO.ShiftsRepo.getInstance().getSchedule().getNextWeek().clear();
        WeeklyAvailabilityDAO.ShiftsRepo.getInstance().getTemplates().clear();
        WeeklyAvailabilityDAO.ShiftsRepo.getInstance().getHistory().clear();
        ShiftDAOImpl.SwapRequestsRepo.getInstance().getSwapRequests().clear();
    }

    @Test void managerMenuExitImmediately() {
        assertDoesNotThrow(() ->
                ui.managerMainMenu(new Scanner(new ByteArrayInputStream("9\n".getBytes())))
        );
    }
    @Test
    void addNewRoleViaMenu() {
        int before = RolesRepo.getInstance().getRoles().size();
        String in = String.join("\n",
                "4",            // Add New Role
                "TempRole",     // role name
                "9"             // Exit
        ) + "\n";
        ui.managerMainMenu(new Scanner(new ByteArrayInputStream(in.getBytes())));
        assertEquals(before + 1, RolesRepo.getInstance().getRoles().size());
        assertNotNull(RolesRepo.getInstance().getRoleByName("TempRole"));
    }

    @Test
    void updateEmployeeDataViaMenu() {
        // we'll update bank account of employee #1
        String in = String.join("\n",
                "3",    // Update Employee Data
                "1",    // choose employee #1 (Alice)
                "1",    // choose Bank Account
                "NEWACC",
                "3",    // exit update
                "9"     // exit manager
        ) + "\n";
        List<Employee> emps = EmployeesRepo.getInstance().getEmployees();
        ui.managerMainMenu(new Scanner(new ByteArrayInputStream(in.getBytes())));
        assertEquals("NEWACC", emps.get(0).getBankAccount());
    }

    @Test
    void removeRoleViaMenu() {
        RolesRepo.getInstance().addRole(new Role("ToRemove"));
        int before = RolesRepo.getInstance().getRoles().size();
        // “ToRemove” will be the last in the list—pick its index
        int idx = before; // 1-based
        String in = String.join("\n",
                "5",            // Remove Role
                String.valueOf(idx),
                "9"
        ) + "\n";
        ui.managerMainMenu(new Scanner(new ByteArrayInputStream(in.getBytes())));
        assertNull(RolesRepo.getInstance().getRoleByName("ToRemove"));
    }

    @Test
    void ConfigureShiftRolesViaMenu() {
        // add one template so next week exists
        ShiftTemplate tmpl = new ShiftTemplate(DayOfWeek.MONDAY, Shift.ShiftTime.Evening);
        tmpl.setDefaultCount(RolesRepo.getInstance().getRoleByName("Cashier"), 3);
        WeeklyAvailabilityDAO.ShiftsRepo.getInstance().addTemplate(tmpl);
        // force‐build next‐week
        LocalDate sat = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));
        WeeklyAvailabilityDAO.ShiftsRepo.getInstance()
                .getSchedule()
                .resetNextWeek(WeeklyAvailabilityDAO.ShiftsRepo.getInstance().getTemplates(), sat);


        String in = String.join("\n",
                "8",    // Set Roles For Shift
                "2",    // Next week
                "1",    // choose shift #1
                "5",    // for Cashier Manager → set 2
                "9"
        ) + "\n";

        ui.managerMainMenu(new Scanner(new ByteArrayInputStream(in.getBytes())));
        Shift updated = WeeklyAvailabilityDAO.ShiftsRepo.getInstance().getSchedule().getNextWeek().get(0);
        assertEquals(1,
                updated.getRequiredCounts()
                        .get(RolesRepo.getInstance().getRoleByName("Shift Manager"))
        );
        assertEquals(5,
                updated.getRequiredCounts()
                        .get(RolesRepo.getInstance().getRoleByName("Cashier"))
        );
    }

    @Test
    void addAndRemoveEmployeeViaMenu() {
        int before = EmployeesRepo.getInstance().getEmployees().size();
        String in = String.join("\n",
                "1",            // Add Employee
                "99",           // ID
                "NewEmp",       // Name
                "pw",           // Password
                "BA99",         // Bank
                "7000",         // Salary
                "01-07-2025",   // Date
                "Cashier",      // Role
                "2",            // Remove Employee
                String.valueOf(EmployeesRepo.getInstance().getEmployees().size()+1), // index of NewEmp
                "yes",
                "9"
        ) + "\n";

        ui.managerMainMenu(new Scanner(new ByteArrayInputStream(in.getBytes())));
        assertEquals(before, EmployeesRepo.getInstance().getEmployees().size(),
                "we added then removed, net should be unchanged");
    }

    @Test
    void assignEmployeeToShiftViaMenu() {
        /*
         * We must quit the inner assignment loop with "0"
         * before sending the outer "9" to exit.
         */
        String in = String.join("\n",
                "6",  // Assign Employee to Shift
                "1",  // pick SHIFT1
                "2",  // pick Cashier
                "1",  // assign Alice
                "0",  // quit assignment loop
                "9"   // exit manager menu
        ) + "\n";

        ui.managerMainMenu(new Scanner(new ByteArrayInputStream(in.getBytes())));
        Shift s = WeeklyAvailabilityDAO.ShiftsRepo.getInstance().getSchedule().getCurrentWeek().get(0);
        assertEquals(1,
                s.getRequiredRoles()
                        .get(RolesRepo.getInstance().getRoleByName("Cashier"))
                        .size()
        );
    }

    @Test
    void processSwapRequestsViaMenu() throws Exception {
        // prepare two swap requests
        Role cashier = RolesRepo.getInstance().getRoleByName("Cashier");
        Employee a = EmployeesRepo.getInstance().getEmployeeById("1");
        Employee b = EmployeesRepo.getInstance().getEmployeeById("2");
        Date d = df.parse("20-09-2025");
        Map<Role,ArrayList<Employee>> rR = new HashMap<>();
        Map<Role,Integer>           rC = new HashMap<>();
        rR.put(cashier, new ArrayList<>()); rC.put(cashier, 1);
        Shift s1 = new Shift("X1", d, Shift.ShiftTime.Morning, rR, rC);
        Shift s2 = new Shift("X2", d, Shift.ShiftTime.Evening, rR, rC);
        s1.assignEmployee(a, cashier);
        s2.assignEmployee(b, cashier);
        WeeklyAvailabilityDAO.ShiftsRepo.getInstance().getSchedule().getCurrentWeek().add(s1);

        ShiftDAOImpl.SwapRequestsRepo.getInstance().getSwapRequests().clear();
        ShiftDAOImpl.SwapRequestsRepo.getInstance().addSwapRequest(new SwapRequest(a, s1, cashier));
        ShiftDAOImpl.SwapRequestsRepo.getInstance().addSwapRequest(new SwapRequest(b, s2, cashier));

        String in = String.join("\n",
                "7",    // Process Swap Requests
                "1",    // pick first request
                "1",    // pick compatible request
                "9"
        ) + "\n";

        ui.managerMainMenu(new Scanner(new ByteArrayInputStream(in.getBytes())));
        assertTrue(ShiftDAOImpl.SwapRequestsRepo.getInstance().getSwapRequests().isEmpty());
    }
}
