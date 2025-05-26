package HR.tests.DomainTests;

import HR.Domain.WeeklyAvailability;
import HR.Domain.Shift;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

class WeeklyAvailabilityTest {

    @Test
    void testGetters() {
        WeeklyAvailability wa = new WeeklyAvailability(DayOfWeek.MONDAY, Shift.ShiftTime.Evening);
        assertEquals(DayOfWeek.MONDAY, wa.getDay());
        assertEquals(Shift.ShiftTime.Evening, wa.getTime());
    }

    @Test
    void testEqualsAndHashCode() {
        WeeklyAvailability a1 = new WeeklyAvailability(DayOfWeek.SUNDAY, Shift.ShiftTime.Morning);
        WeeklyAvailability a2 = new WeeklyAvailability(DayOfWeek.SUNDAY, Shift.ShiftTime.Morning);
        WeeklyAvailability b  = new WeeklyAvailability(DayOfWeek.SUNDAY, Shift.ShiftTime.Evening);

        assertEquals(a1, a2, "Two availabilities with same day/time should be equal");
        assertEquals(a1.hashCode(), a2.hashCode(), "hashCode must be consistent with equals");
        assertNotEquals(a1, b, "Different time should not be equal");
    }
}
