package tests.DomainTests;

import Domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShiftsRepoTest {
    private ShiftsRepo repo;

    @BeforeEach
    void setUp() {
        repo = ShiftsRepo.getInstance();
        // Clear out everything so each test starts fresh
        repo.getTemplates().clear();
        repo.getHistory().clear();
        repo.getSchedule().getCurrentWeek().clear();
        repo.getSchedule().getNextWeek().clear();
    }

    @Test
    void testSingletonInstance() {
        assertSame(repo, ShiftsRepo.getInstance(), "getInstance() should always return the same object");
    }

    @Test
    void testAddTemplateAndRetrieve() {
        RecurringShift template = new RecurringShift(DayOfWeek.MONDAY, Shift.ShiftTime.Evening);
        template.setDefaultCount(new Role("Cashier"), 2);

        repo.addTemplate(template);
        List<RecurringShift> allTemplates = repo.getTemplates();

        assertEquals(1, allTemplates.size(), "Should have one template after adding");
        assertTrue(allTemplates.contains(template), "Template list should contain our added template");
    }

    @Test
    void testResetNextWeekBuildsShiftsFromTemplates() {
        RecurringShift rs = new RecurringShift(DayOfWeek.TUESDAY, Shift.ShiftTime.Morning);
        Role r = new Role("Driver");
        rs.setDefaultCount(r, 3);
        repo.addTemplate(rs);

        WeeklySchedule sched = repo.getSchedule();
        // Suppose last Saturday was 2023‑09‑16
        LocalDate lastSat = LocalDate.of(2023, 9, 16);
        sched.resetNextWeek(repo.getTemplates(), lastSat);

        List<Shift> next = sched.getNextWeek();
        assertEquals(1, next.size(), "Should have one shift in nextWeek, matching our single template");

        Shift s = next.get(0);
        // ID was generated as "TUESDAY-Morning-<date>"
        assertTrue(s.getID().startsWith("TUESDAY"), "Shift ID should begin with the weekday");
        assertEquals(Shift.ShiftTime.Morning, s.getType());
        assertEquals(3, s.getRequiredCounts().get(r), "Required count for Driver should be the template’s default");
    }

    @Test
    void testSwapWeeksAndArchiveHistory() {
        RecurringShift rs = new RecurringShift(DayOfWeek.SUNDAY, Shift.ShiftTime.Evening);
        rs.setDefaultCount(new Role("Cleaner"), 1);
        repo.addTemplate(rs);

        WeeklySchedule sched = repo.getSchedule();
        LocalDate sat = LocalDate.of(2023, 9, 16);
        sched.resetNextWeek(repo.getTemplates(), sat);

        // Before swap: current empty, next has 1
        assertTrue(sched.getCurrentWeek().isEmpty());
        assertEquals(1, sched.getNextWeek().size());

        // Simulate end‑of‑week rollover
        sched.swapWeeks();

        // After swap: current has it, next is empty
        assertEquals(1, sched.getCurrentWeek().size());
        assertTrue(sched.getNextWeek().isEmpty());

        // Archive into ShiftsRepo.history
        repo.getHistory().clear();
        repo.getHistory().addAll(sched.getCurrentWeek());
        assertEquals(sched.getCurrentWeek(), repo.getHistory(), "History should match the rolled‑over currentWeek");
    }

    @Test
    void testHistoryInitiallyEmpty() {
        assertTrue(repo.getHistory().isEmpty(), "History should start out empty");
    }
}
