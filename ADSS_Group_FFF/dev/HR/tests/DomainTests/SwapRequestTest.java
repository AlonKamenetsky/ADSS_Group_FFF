package HR.tests.DomainTests;

import HR.Domain.*;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SwapRequestTest {

    @Test
    public void constructor_and_getters_withId() {
        Employee e = new Employee("emp1", Collections.emptyList(), "Alice", "pw", "acc", 1000f, new Date());
        Shift s = new Shift("shiftA", new Date(), Shift.ShiftTime.Morning, Map.of(), Map.of());
        Role r = new Role("Driver");

        SwapRequest req = new SwapRequest(42, e, s, r);
        assertEquals(42, req.getId());
        assertEquals(e, req.getEmployee());
        assertEquals(s, req.getShift());
        assertEquals(r, req.getRole());
    }

    @Test
    public void constructor_withoutId_defaultsToMinusOne() {
        Employee e = new Employee("emp2", Collections.emptyList(), "Bob", "pw", "acc", 1200f, new Date());
        Shift s = new Shift("shiftB", new Date(), Shift.ShiftTime.Evening, Map.of(), Map.of());
        Role r = new Role("Cashier");

        SwapRequest req = new SwapRequest(e, s, r);
        assertEquals(-1, req.getId());
        assertEquals(e, req.getEmployee());
        assertEquals(s, req.getShift());
        assertEquals(r, req.getRole());
    }

    @Test
    public void toString_containsEmployeeNameShiftIdRoleName() {
        Employee e = new Employee("emp3", Collections.emptyList(), "Carol", "pw", "acc", 1300f, new Date());
        Shift s = new Shift("shiftC", new Date(), Shift.ShiftTime.Morning, Map.of(), Map.of());
        Role r = new Role("Cleaner");

        SwapRequest req = new SwapRequest(7, e, s, r);
        String str = req.toString();
        assertTrue(str.contains("Carol"));
        assertTrue(str.contains("shiftC"));
        assertTrue(str.contains("Cleaner"));
    }
}
