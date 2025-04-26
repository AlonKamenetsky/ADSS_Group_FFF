// Domain/ShiftsRepo.java
package Domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ShiftsRepo {
    private static ShiftsRepo instance = null;

    private final List<RecurringShift> templates = new ArrayList<>();
    private final WeeklySchedule       schedule  = new WeeklySchedule();
    private final List<Shift>          history   = new ArrayList<>();

    // Remember the last Saturday-18:00 rollover timestamp
    private LocalDateTime lastRollover = LocalDateTime.MIN;

    private ShiftsRepo() {}

    public static synchronized ShiftsRepo getInstance() {
        if (instance == null) instance = new ShiftsRepo();
        return instance;
    }

    public void addTemplate(RecurringShift r) {
        templates.add(r);
    }

    /**
     * Archive & roll over exactly once after Saturday at 18:00 (inclusive).
     */
    public void ensureUpToDate() {
        LocalDate today    = LocalDate.now(ZoneId.systemDefault());
        LocalDate saturday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));
        LocalDateTime cutoff = saturday.atTime(18, 0);
        LocalDateTime now    = LocalDateTime.now(ZoneId.systemDefault());

        // If we are at or past Saturday 18:00 and haven’t rolled yet:
        if (!now.isBefore(cutoff) && lastRollover.isBefore(cutoff)) {
            // 1) Archive old currentWeek
            history.addAll(schedule.getCurrentWeek());
            // 2) Swap nextWeek → currentWeek
            schedule.swapWeeks();
            // 3) Build nextWeek for the week after this Saturday
            schedule.resetNextWeek(templates, saturday.plusDays(7));
            lastRollover = cutoff;
        }
    }

    /** Returns whatever is buffered as currentWeek (after any rollover). */
    public List<Shift> getCurrentWeekShifts() {
        ensureUpToDate();
        return schedule.getCurrentWeek().stream()
                .sorted(Comparator.comparing(Shift::getDate))
                .collect(Collectors.toList());
    }

    /** Returns whatever is buffered as nextWeek (after any rollover), lazily building it. */
    public List<Shift> getNextWeekShifts() {
        ensureUpToDate();
        if (schedule.getNextWeek().isEmpty() && !templates.isEmpty()) {
            LocalDate nextSat = LocalDate.now(ZoneId.systemDefault())
                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
            schedule.resetNextWeek(templates, nextSat);
        }
        return schedule.getNextWeek().stream()
                .sorted(Comparator.comparing(Shift::getDate))
                .collect(Collectors.toList());
    }

    public WeeklySchedule getSchedule()    { return schedule;  }
    public List<RecurringShift> getTemplates() { return templates; }
    public List<Shift> getHistory()       { return history;   }
}
