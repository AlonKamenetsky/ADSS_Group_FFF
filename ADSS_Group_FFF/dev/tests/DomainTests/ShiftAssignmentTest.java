package tests.DomainTests;

import Domain.Role;
import Domain.ShiftAssignment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftAssignmentTest {

    @Test
    public void testShiftAssignmentGetters() {
        Role role = new Role("Cashier");
        ShiftAssignment assignment = new ShiftAssignment("1", "SHIFT1", role);
        assertEquals("1", assignment.getEmployeeId());
        assertEquals("SHIFT1", assignment.getShiftId());
        assertEquals("Cashier", assignment.getRole().getName());
    }
}
