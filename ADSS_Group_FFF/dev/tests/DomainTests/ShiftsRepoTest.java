// src/test/java/tests/DomainTests/ShiftsRepoTest.java
package tests.DomainTests;

import Domain.*;
import org.junit.jupiter.api.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShiftsRepoTest {
    private ShiftsRepo repo;

    @BeforeEach
    void init() {
        repo = ShiftsRepo.getInstance();
        repo.getTemplates().clear();
        repo.getSchedule().getCurrentWeek().clear();
        repo.getSchedule().getNextWeek().clear();
        repo.getHistory().clear();
    }

    @Test
    void testSwapWeeksAndArchiveHistory() {
        RecurringShift rs = new RecurringShift(DayOfWeek.SUNDAY, Shift.ShiftTime.Evening);
        rs.setDefaultCount(new Role("Cleaner"), 1);
        repo.addTemplate(rs);

        WeeklySchedule sched = repo.getSchedule();
        LocalDate sat = LocalDate.of(2023, 9, 16);
        sched.resetNextWeek(repo.getTemplates(), sat);

        // Before swap: current empty, next has one shift
        assertTrue(sched.getCurrentWeek().isEmpty());
        assertEquals(1, sched.getNextWeek().size());

        // Perform the weekly rollover
        sched.swapWeeks();

        // After swap: current has it, next is empty
        assertEquals(1, sched.getCurrentWeek().size());
        assertTrue(sched.getNextWeek().isEmpty());

        // Archive History: manually add current into history
        repo.getHistory().clear();
        repo.getHistory().addAll(sched.getCurrentWeek());
        assertEquals(sched.getCurrentWeek(), repo.getHistory());
    }

    @Test
    void testSwapWeeksAndArchiveHistoryWithAssignments() {
        // 1) seed one template
        RecurringShift rs = new RecurringShift(DayOfWeek.SUNDAY, Shift.ShiftTime.Evening);
        rs.setDefaultCount(new Role("Cleaner"), 1);
        repo.addTemplate(rs);

        // 2) build next week for a known Saturday
        WeeklySchedule sched = repo.getSchedule();
        LocalDate sat = LocalDate.of(2023, 9, 16);
        sched.resetNextWeek(repo.getTemplates(), sat);

        // nextWeek now has exactly one Shift
        List<Shift> next = sched.getNextWeek();
        assertEquals(1, next.size());

        // 3) assign an employee to that shift
        Role cleaner = new Role("Cleaner");
        Employee emp = new Employee("X", List.of(cleaner), "Zoe", "pw", "BA", 0f, new java.util.Date());
        next.get(0).assignEmployee(emp, cleaner);
        assertEquals(1, next.get(0).getAssignedEmployees().size(),
                "Assignment should have stuck in nextWeek");

        // 4) swap into currentWeek
        sched.swapWeeks();
        List<Shift> cur = sched.getCurrentWeek();
        assertEquals(1, cur.size());

        // 5) now the same Shift object with its assignment should be in currentWeek
        assertEquals(1, cur.get(0).getAssignedEmployees().size(),
                "Assignment should have carried over into currentWeek");
    }
}
