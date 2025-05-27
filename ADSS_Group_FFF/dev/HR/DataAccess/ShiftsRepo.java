package HR.DataAccess;

import HR.Domain.Shift;
import HR.Domain.ShiftTemplate;
import HR.Domain.WeeklyShiftsSchedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

public class ShiftsRepo implements HR.Domain.ShiftsRepo {
    private static ShiftsRepo instance = null;

    private final List<ShiftTemplate> templates = new ArrayList<>();
    private final WeeklyShiftsSchedule schedule  = new WeeklyShiftsSchedule();
    private final List<Shift>          history   = new ArrayList<>();

    // Remember the last Saturday-18:00 rollover timestamp
    private LocalDateTime lastRollover = LocalDateTime.MIN;

    private ShiftsRepo() {}

    public static synchronized ShiftsRepo getInstance() {
        if (instance == null) instance = new ShiftsRepo();
        return instance;
    }

    public void addTemplate(ShiftTemplate r) {
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

    public Optional<Shift> getCurrentShift() {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        Shift.ShiftTime currentType;

        if (hour >= 8 && hour < 15) {
            currentType = Shift.ShiftTime.Morning;
        } else if (hour >= 15 && hour < 22) {
            currentType = Shift.ShiftTime.Evening;
        } else {
            return Optional.empty(); // Outside shift times
        }

        for (Shift shift : schedule.getCurrentWeek()) {
            if (isSameDay(shift.getDate(), now) && shift.getType() == currentType) {
                return Optional.of(shift);
            }
        }
        return Optional.empty();
    }

    public Shift getShiftByID(String id) {
        // Search current week shifts
        for (Shift shift : getCurrentWeekShifts()) {
            if (shift.getID().equals(id)) {
                return shift;
            }
        }
        // Search next week shifts
        for (Shift shift : getNextWeekShifts()) {
            if (shift.getID().equals(id)) {
                return shift;
            }
        }
        // Search history
        for (Shift shift : history) {
            if (shift.getID().equals(id)) {
                return shift;
            }
        }
        return null; // Not found
    }

    public boolean isSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }


    public WeeklyShiftsSchedule getSchedule()    { return schedule;  }
    public List<ShiftTemplate> getTemplates() { return templates; }
    public List<Shift> getHistory()       { return history;   }
}