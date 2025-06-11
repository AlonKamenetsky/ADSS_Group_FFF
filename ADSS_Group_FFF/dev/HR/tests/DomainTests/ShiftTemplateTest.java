package HR.tests.DomainTests;

import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.ShiftTemplate;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftTemplateTest {

    @Test
    public void constructor_and_getters() {
        ShiftTemplate tpl = new ShiftTemplate(DayOfWeek.TUESDAY, Shift.ShiftTime.Morning);

        assertEquals(DayOfWeek.TUESDAY, tpl.getDay());
        assertEquals(Shift.ShiftTime.Morning, tpl.getTime());
        assertTrue(tpl.getDefaultCounts().isEmpty());
    }

    @Test
    public void setDefaultCount_addsEntry() {
        ShiftTemplate tpl = new ShiftTemplate(DayOfWeek.FRIDAY, Shift.ShiftTime.Evening);
        Role r1 = new Role("Cleaner");
        Role r2 = new Role("Cashier");

        tpl.setDefaultCount(r1, 2);
        tpl.setDefaultCount(r2, 3);

        Map<Role, Integer> counts = tpl.getDefaultCounts();
        assertEquals(2, counts.get(r1));
        assertEquals(3, counts.get(r2));
    }
}
