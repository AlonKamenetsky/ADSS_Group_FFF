package HR.tests.DomainTests;

import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.WeeklyAvailability;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeTest {

    @Test
    public void constructor_and_getters() {
        List<Role> roles = List.of(new Role("Cashier"));
        Date hireDate = new Date(0L);
        Employee emp = new Employee("e1", roles, "Alice", "pw", "ACC123", 3000f, hireDate);

        assertEquals("e1", emp.getId());
        assertEquals("Alice", emp.getName());
        assertEquals("pw", emp.getPassword());
        assertEquals("ACC123", emp.getBankAccount());
        assertEquals(3000f, emp.getSalary());
        assertEquals(hireDate, emp.getEmploymentDate());
        assertTrue(emp.getAvailabilityThisWeek().isEmpty());
        assertTrue(emp.getAvailabilityNextWeek().isEmpty());
        assertTrue(emp.getShifts().isEmpty());
        assertTrue(emp.getHolidays().isEmpty());
    }

    @Test
    public void addShift_preventsDuplicates() {
        Employee emp = new Employee("e2", List.of(new Role("Warehouse")), "Bob", "pw2", "ACC456", 2500f, new Date());
        Shift s1 = new Shift("s1", new Date(), Shift.ShiftTime.Morning, Map.of(), Map.of());
        emp.addShift(s1);
        emp.addShift(s1); // duplicate

        assertEquals(1, emp.getShifts().size());
        assertTrue(emp.getShifts().contains(s1));
    }

    @Test
    public void holidayManagement() {
        Employee emp = new Employee("e3", List.of(), "Carol", "pw3", "ACC789", 2000f, new Date());
        Date holiday = new Date(1000L);
        emp.addHoliday(holiday);
        assertEquals(1, emp.getHolidays().size());
        assertTrue(emp.getHolidays().contains(holiday));

        // Adding same holiday again does nothing
        emp.addHoliday(holiday);
        assertEquals(1, emp.getHolidays().size());
    }

    @Test
    public void availabilityManagement_and_isAvailable() {
        Employee emp = new Employee("e4", List.of(), "Dan", "pw4", "ACC000", 2200f, new Date());

        // Initially not available (empty availability means “no constraints”)
        Date today = new Date();
        assertTrue(emp.isAvailable(today, Shift.ShiftTime.Morning));

        // Add a next-week slot
        emp.addAvailability(DayOfWeek.MONDAY, Shift.ShiftTime.Evening);
        // Move NextWeek → ThisWeek
        emp.swapAvailability();
        assertFalse(emp.getAvailabilityNextWeek().contains(new WeeklyAvailability(DayOfWeek.MONDAY, Shift.ShiftTime.Evening)));
        assertTrue(emp.getAvailabilityThisWeek().contains(new WeeklyAvailability(DayOfWeek.MONDAY, Shift.ShiftTime.Evening)));

        // If date’s day-of-week is Monday, check availability
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(today);
        // force date to next Monday
        while (cal.get(java.util.Calendar.DAY_OF_WEEK) != java.util.Calendar.MONDAY) {
            cal.add(java.util.Calendar.DATE, 1);
        }
        Date nextMonday = cal.getTime();

        assertTrue(emp.isAvailable(nextMonday, Shift.ShiftTime.Evening));
        assertFalse(emp.isAvailable(nextMonday, Shift.ShiftTime.Morning));

        // Remove availability
        emp.removeAvailability(DayOfWeek.MONDAY, Shift.ShiftTime.Evening);
        // After removal, next-week is empty; but thisWeek still contains previous slot
        assertTrue(emp.getAvailabilityThisWeek().contains(new WeeklyAvailability(DayOfWeek.MONDAY, Shift.ShiftTime.Evening)));
    }
}
