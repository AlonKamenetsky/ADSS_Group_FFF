// src/main/java/Domain/ShiftsRepo.java
package Domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

    /** Add one of your daily templates (e.g. Sunday morning). */
    public void addTemplate(RecurringShift r) {
        templates.add(r);
    }

    /** Always call before reading currentWeekShifts. */
    public void ensureUpToDate() {
        LocalDate today    = LocalDate.now(ZoneId.systemDefault());
        LocalDate saturday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));
        LocalTime now      = LocalTime.now();

        // On Saturday ≥18:00, archive + roll
        if (!saturday.equals(lastReset) && now.isAfter(LocalTime.of(18,0))) {
            history.addAll(schedule.getCurrentWeek());
            schedule.swapWeeks();
            schedule.resetNextWeek(templates, saturday);
            lastReset = saturday;
        }
    }

    /** Get this week’s shifts (Sunday→Saturday). */
    public List<Shift> getCurrentWeekShifts() {
        ensureUpToDate();
        LocalDate today       = LocalDate.now(ZoneId.systemDefault());
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek   = startOfWeek.plusDays(6);

        return schedule.getCurrentWeek().stream()
                .filter(shift -> {
                    LocalDate d = shift.getDate()
                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return !d.isBefore(startOfWeek) && !d.isAfter(endOfWeek);
                })
                .sorted(Comparator.comparing(Shift::getDate))
                .collect(Collectors.toList());
    }

    /** Lazily build nextWeek from templates, then return everything sorted. */
    public List<Shift> getNextWeekShifts() {
        if (schedule.getNextWeek().isEmpty() && !templates.isEmpty()) {
            LocalDate pivot = LocalDate.now(ZoneId.systemDefault())
                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
            schedule.resetNextWeek(templates, pivot);
        }
        return schedule.getNextWeek().stream()
                .sorted(Comparator.comparing(Shift::getDate))
                .collect(Collectors.toList());
    }

    public WeeklySchedule             getSchedule()  { return schedule;  }
    public List<RecurringShift>       getTemplates() { return templates; }
    public List<Shift>                getHistory()   { return history;   }
}
