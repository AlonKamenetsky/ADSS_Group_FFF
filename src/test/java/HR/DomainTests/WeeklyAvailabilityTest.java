package HR.tests.DomainTests;

import HR.Domain.*;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

public class WeeklyAvailabilityTest {

    @Test
    public void constructor_and_getters() {
        WeeklyAvailability wa = new WeeklyAvailability(DayOfWeek.THURSDAY, Shift.ShiftTime.Evening);
        assertEquals(DayOfWeek.THURSDAY, wa.getDay());
        assertEquals(Shift.ShiftTime.Evening, wa.getTime());
    }

    @Test
    public void equals_and_hashCode_forSameValues() {
        WeeklyAvailability wa1 = new WeeklyAvailability(DayOfWeek.MONDAY, Shift.ShiftTime.Morning);
        WeeklyAvailability wa2 = new WeeklyAvailability(DayOfWeek.MONDAY, Shift.ShiftTime.Morning);
        assertEquals(wa1, wa2);
        assertEquals(wa1.hashCode(), wa2.hashCode());
    }

    @Test
    public void equals_forDifferentValues() {
        WeeklyAvailability wa1 = new WeeklyAvailability(DayOfWeek.MONDAY, Shift.ShiftTime.Morning);
        WeeklyAvailability wa2 = new WeeklyAvailability(DayOfWeek.TUESDAY, Shift.ShiftTime.Morning);
        assertNotEquals(wa1, wa2);

        WeeklyAvailability wa3 = new WeeklyAvailability(DayOfWeek.MONDAY, Shift.ShiftTime.Evening);
        assertNotEquals(wa1, wa3);
    }
}
