package HR.tests.DomainTests;

import HR.Domain.WeeklyShiftsSchedule;
import HR.Domain.ShiftTemplate;
import HR.Domain.Role;
import HR.Domain.Shift;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WeeklyScheduleTest {
    private WeeklyShiftsSchedule ws;
    private List<ShiftTemplate> templates;
    private Role cashier, manager;

    @BeforeEach
    void setUp() {
        ws = new WeeklyShiftsSchedule();
        templates = new ArrayList<>();

        ShiftTemplate monMorning = new ShiftTemplate(DayOfWeek.MONDAY, Shift.ShiftTime.Morning);
        ShiftTemplate wedEvening = new ShiftTemplate(DayOfWeek.WEDNESDAY, Shift.ShiftTime.Evening);
        cashier = new Role("Cashier");
        manager = new Role("Manager");

        monMorning.setDefaultCount(cashier, 2);
        wedEvening.setDefaultCount(manager, 1);

        templates.add(monMorning);
        templates.add(wedEvening);
    }

    @Test
    void testResetNextWeekPopulatesCorrectShifts() {
        // Use a known Saturday
        LocalDate saturday = LocalDate.of(2023, 9, 16);
        ws.resetNextWeek(templates, saturday);

        List<Shift> next = ws.getNextWeek();
        assertEquals(2, next.size(), "Should have one shift per template");

        // Verify Monday shift
        Shift mondayShift = next.stream()
                .filter(s -> s.getType() == Shift.ShiftTime.Morning
                        && LocalDate.ofInstant(s.getDate().toInstant(), ZoneId.systemDefault())
                        .getDayOfWeek() == DayOfWeek.MONDAY)
                .findFirst()
                .orElse(null);
        assertNotNull(mondayShift, "Monday shift must be present");
        Map<Role,Integer> countsMon = mondayShift.getRequiredCounts();
        assertEquals(1, countsMon.size());
        assertEquals(2, countsMon.get(cashier).intValue());

        // Verify Wednesday shift
        Shift wedShift = next.stream()
                .filter(s -> s.getType() == Shift.ShiftTime.Evening
                        && LocalDate.ofInstant(s.getDate().toInstant(), ZoneId.systemDefault())
                        .getDayOfWeek() == DayOfWeek.WEDNESDAY)
                .findFirst()
                .orElse(null);
        assertNotNull(wedShift, "Wednesday shift must be present");
        Map<Role,Integer> countsWed = wedShift.getRequiredCounts();
        assertEquals(1, countsWed.size());
        assertEquals(1, countsWed.get(manager).intValue());
    }

    @Test
    void testSwapWeeksMovesNextIntoCurrentAndClearsNext() {
        // Build nextWeek
        LocalDate saturday = LocalDate.of(2023, 9, 16);
        ws.resetNextWeek(templates, saturday);
        assertFalse(ws.getNextWeek().isEmpty());

        ws.swapWeeks();

        assertEquals(templates.size(), ws.getCurrentWeek().size(), "Current week should now contain what was next week");
        assertTrue(ws.getNextWeek().isEmpty(), "Next week should be cleared after swap");
    }
}
