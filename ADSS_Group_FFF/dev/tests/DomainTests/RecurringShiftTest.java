package tests;

import Domain.ShiftTemplate;
import Domain.Role;
import Domain.Shift;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RecurringShiftTest {
    private ShiftTemplate rs;
    private Role cashier;

    @BeforeEach
    void setUp() {
        rs = new ShiftTemplate(DayOfWeek.TUESDAY, Shift.ShiftTime.Evening);
        cashier = new Role("Cashier");
    }

    @Test
    void testGetDayAndTime() {
        assertEquals(DayOfWeek.TUESDAY, rs.getDay());
        assertEquals(Shift.ShiftTime.Evening, rs.getTime());
    }

    @Test
    void testDefaultCountsInitiallyEmpty() {
        assertTrue(rs.getDefaultCounts().isEmpty(), "New RecurringShift should start with no default counts");
    }

    @Test
    void testSetDefaultCount() {
        rs.setDefaultCount(cashier, 3);
        Map<Role,Integer> counts = rs.getDefaultCounts();
        assertEquals(1, counts.size());
        assertTrue(counts.containsKey(cashier));
        assertEquals(3, counts.get(cashier).intValue());
    }
}
