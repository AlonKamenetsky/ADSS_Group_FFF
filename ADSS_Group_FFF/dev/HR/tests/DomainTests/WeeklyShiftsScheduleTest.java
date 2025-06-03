package HR.tests.DomainTests;

import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.ShiftTemplate;
import HR.Domain.WeeklyShiftsSchedule;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class WeeklyShiftsScheduleTest {

    @Test
    public void resetNextWeek_buildsCorrectShifts_and_swapWeeks() {
        ShiftTemplate templateMon = new ShiftTemplate(DayOfWeek.MONDAY, Shift.ShiftTime.Morning);
        ShiftTemplate templateWed = new ShiftTemplate(DayOfWeek.WEDNESDAY, Shift.ShiftTime.Evening);

        // Set default counts for roles
        Role rCashier = new Role("Cashier");
        Role rCleaner = new Role("Cleaner");
        templateMon.setDefaultCount(rCashier, 2);
        templateMon.setDefaultCount(rCleaner, 1);
        templateWed.setDefaultCount(rCashier, 1);

        List<ShiftTemplate> templates = List.of(templateMon, templateWed);

        WeeklyShiftsSchedule sched = new WeeklyShiftsSchedule();
        LocalDate refSat = LocalDate.of(2025, 6, 7); // a Saturday

        sched.resetNextWeek(templates, refSat);
        List<Shift> next = sched.getNextWeek();
        assertEquals(2, next.size());

        // Find the Monday shift
        Shift mondayShift = next.stream()
                .filter(s -> s.getType() == Shift.ShiftTime.Morning &&
                        s.getRequiredCounts().containsKey(rCashier))
                .findFirst()
                .orElse(null);
        assertNotNull(mondayShift);

        Date expectedMonDate = Date.from(
                refSat.plusDays(2)      // Monday after Saturday
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        assertEquals(expectedMonDate, mondayShift.getDate());
        assertEquals(2, mondayShift.getRequiredCounts().get(rCashier));
        assertEquals(1, mondayShift.getRequiredCounts().get(rCleaner));

        // Find Wednesday shift
        Shift wedShift = next.stream()
                .filter(s -> s.getType() == Shift.ShiftTime.Evening &&
                        s.getRequiredCounts().containsKey(rCashier) &&
                        s.getRequiredCounts().size() == 1)
                .findFirst()
                .orElse(null);
        assertNotNull(wedShift);

        Date expectedWedDate = Date.from(
                refSat.plusDays(4)   // Wednesday after Saturday
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        assertEquals(expectedWedDate, wedShift.getDate());

        // Now swap weeks
        sched.swapWeeks();
        List<Shift> current = sched.getCurrentWeek();
        assertEquals(2, current.size());
        assertTrue(current.containsAll(next));
        assertTrue(sched.getNextWeek().isEmpty());
    }
}
