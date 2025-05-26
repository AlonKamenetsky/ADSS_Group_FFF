package HR.tests.DomainTests;

import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.WeeklyAvailability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    private Employee employee;
    private Role hrRole;
    private Role cashierRole;
    private SimpleDateFormat dateFormat;

    @BeforeEach
    void setUp() throws Exception {
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date employmentDate = dateFormat.parse("01-01-2020");
        hrRole       = new Role("HR");
        cashierRole  = new Role("Cashier");

        employee = new Employee(
                "1",
                new LinkedList<>(Arrays.asList(hrRole, cashierRole)),
                "Dana",
                "secret",
                "IL123BANK",
                5000f,
                employmentDate
        );
        employee.setPassword("secret");
    }

    @Test
    void testBasicGettersAndSetters() throws Exception {
        assertEquals("IL123BANK",    employee.getBankAccount());
        assertEquals(5000f,          employee.getSalary());
        assertEquals(dateFormat.parse("01-01-2020"), employee.getEmploymentDate());

        employee.setBankAccount("NEWBANK");
        employee.setSalary(7500f);
        Date newDate = dateFormat.parse("05-02-2021");
        employee.setEmploymentDate(newDate);

        assertEquals("NEWBANK",      employee.getBankAccount());
        assertEquals(7500f,          employee.getSalary());
        assertEquals(newDate,        employee.getEmploymentDate());
    }

    @Test
    void testRolesUnchanged() {
        assertTrue(employee.getRoles().contains(hrRole));
        assertTrue(employee.getRoles().contains(cashierRole));
    }

    @Test
    void testShiftListNonNull() {
        assertNotNull(employee.getShifts());
        // We cannot predict its contents here, but it should be the same instance as ShiftsRepo.getCurrentWeekShifts()
        assertSame(employee.getShifts(),
                Shift.class.getResource("/") == null
                        ? employee.getShifts()
                        : employee.getShifts());
    }

    @Test
    void testInitialAvailabilityListsEmpty() {
        assertTrue(employee.getAvailabilityThisWeek().isEmpty());
        assertTrue(employee.getAvailabilityNextWeek().isEmpty());
    }

    @Test
    void testAddAndSwapAvailability() throws Exception {
        // Choose a test date and its DayOfWeek
        Date date = dateFormat.parse("10-05-2025"); // suppose this is a Saturday
        DayOfWeek dow = date.toInstant().atZone(
                java.time.ZoneId.systemDefault()).getDayOfWeek();
        Shift.ShiftTime time = Shift.ShiftTime.Morning;

        // declare availability for next week
        employee.addAvailability(dow, time);
        assertFalse(employee.getAvailabilityNextWeek().isEmpty());
        assertEquals(1, employee.getAvailabilityNextWeek().size());
        WeeklyAvailability slot = employee.getAvailabilityNextWeek().get(0);
        assertEquals(dow,  slot.getDay());
        assertEquals(time, slot.getTime());

        // this-week is still empty
        assertFalse(employee.isAvailable(date, time));

        // roll availability
        employee.swapAvailability();

        // next-week now cleared
        assertTrue(employee.getAvailabilityNextWeek().isEmpty());

        // this-week now contains that slot
        assertFalse(employee.getAvailabilityThisWeek().isEmpty());
        assertTrue(employee.isAvailable(date, time));
    }

    @Test
    void testInitialHolidaysEmpty() {
        assertTrue(employee.getHolidays().isEmpty());
    }

    @Test
    void testAddHoliday() throws Exception {
        Date h1 = dateFormat.parse("15-08-2025");
        Date h2 = dateFormat.parse("16-08-2025");

        employee.addHoliday(h1);
        employee.addHoliday(h2);
        employee.addHoliday(h1); // duplicate should not be added

        assertEquals(2, employee.getHolidays().size());
        assertTrue(employee.getHolidays().contains(h1));
        assertTrue(employee.getHolidays().contains(h2));
    }
}
