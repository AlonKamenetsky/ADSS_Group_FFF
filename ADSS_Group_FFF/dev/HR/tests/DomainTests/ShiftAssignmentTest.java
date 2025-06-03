package HR.tests.DomainTests;

import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.ShiftAssignment;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftAssignmentTest {

    @Test
    public void constructor_and_getters() {
        Role r = new Role("Cleaner");
        ShiftAssignment sa = new ShiftAssignment("emp123", "shift456", r);

        assertEquals("emp123", sa.getEmployeeId());
        assertEquals("shift456", sa.getShiftId());
        assertEquals("Cleaner", sa.getRole().getName());
    }

    @Test
    public void setShift_updatesShiftId() {
        Role r = new Role("Cashier");
        ShiftAssignment sa = new ShiftAssignment("empX", "oldShift", r);

        Shift s = new Shift("newShift", new Date(), Shift.ShiftTime.Evening, Map.of(), Map.of());
        sa.setShift(s);

        assertEquals("newShift", sa.getShiftId());

        // Setting to null should clear shiftId
        sa.setShift(null);
        assertNull(sa.getShiftId());
    }
}
