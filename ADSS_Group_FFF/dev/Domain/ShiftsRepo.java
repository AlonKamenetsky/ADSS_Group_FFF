package Domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.time.ZoneId;
import java.util.stream.Collectors;


/**
 * Singleton repository for:
 *  - RecurringShift templates
 *  - The two‑week rolling schedule
 *  - A simple history of all past shifts
 */
public class ShiftsRepo {
    private static ShiftsRepo instance = null;

    private final List<RecurringShift> templates = new ArrayList<>();
    private final WeeklySchedule       schedule  = new WeeklySchedule();
    private final List<Shift>          history   = new ArrayList<>();

    private LocalDate lastReset = LocalDate.MIN;


    private ShiftsRepo() {}

    public static synchronized ShiftsRepo getInstance() {
        if (instance == null) instance = new ShiftsRepo();
        return instance;
    }

    /** Add one of the two daily templates (e.g. Sunday morning). */
    public void addTemplate(RecurringShift r) {
        templates.add(r);
    }

    public List<Shift> getCurrentWeekShifts() {
        ensureUpToDate();

        // compute week's Sunday and Saturday
        LocalDate today       = LocalDate.now(ZoneId.systemDefault());
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek   = startOfWeek.plusDays(6);

        return schedule.getCurrentWeek().stream()
                .filter(shift -> {
                    LocalDate d = shift.getDate()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return !d.isBefore(startOfWeek) && !d.isAfter(endOfWeek);
                })
                .sorted(Comparator.comparing(Shift::getDate))   // <— sort ascending
                .collect(Collectors.toList());
    }

    public WeeklySchedule getSchedule() {
        return schedule;
    }

    public List<RecurringShift> getTemplates() {
        return templates;
    }

    /** A running log of every Shift once it “ends” (i.e. Saturday evening). */
    public List<Shift> getHistory() {
        return history;
    }

    /**
     * Call before any read of schedule.  On Saturday ≥18:00 it:
     *   - archives the just‑ended currentWeek into history
     *   - swaps nextWeek→currentWeek
     *   - rebuilds nextWeek from templates
     */
    public void ensureUpToDate() {
        LocalDate today   = LocalDate.now();
        LocalDate saturday= today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));
        LocalTime now     = LocalTime.now();

        // If it’s Saturday evening, and we haven’t reset this Saturday yet:
        if (!saturday.equals(lastReset)
                && now.isAfter(LocalTime.of(18, 0))) {

            // Archive
            history.addAll(schedule.getCurrentWeek());
            // Roll
            schedule.swapWeeks();
            schedule.resetNextWeek(templates, saturday);
            lastReset = saturday;
        }
    }

    public List<Shift> getNextWeekShifts() {
        // If we’ve never built nextWeek yet, do so now
        if (schedule.getNextWeek().isEmpty() && !templates.isEmpty()) {
            LocalDate today    = LocalDate.now(ZoneId.systemDefault());
            LocalDate saturday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
            schedule.resetNextWeek(templates, saturday);
        }

        // Just return everything in nextWeek, sorted by date
        return schedule.getNextWeek().stream()
                .sorted(Comparator.comparing(Shift::getDate))
                .collect(Collectors.toList());
    }
}
