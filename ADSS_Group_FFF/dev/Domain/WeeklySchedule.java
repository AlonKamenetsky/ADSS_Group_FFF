package Domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Holds the actual Shift instances for “this week” and “next week”.
 */
public class WeeklySchedule {
    private List<Shift> currentWeek = new ArrayList<>();
    private List<Shift> nextWeek    = new ArrayList<>();

    public List<Shift> getCurrentWeek() {
        return currentWeek;
    }

    public List<Shift> getNextWeek() {
        return nextWeek;
    }

    /**
     * Using your RecurringShift templates and Saturday as reference,
     * build the nextWeek list of concrete Shift instances.
     */
    public void resetNextWeek(List<RecurringShift> templates, LocalDate refSaturday) {
        nextWeek.clear();
        for (RecurringShift rs : templates) {
            // Compute the calendar date for this template next week
            int saturdayValue = DayOfWeek.SATURDAY.getValue(); // 6
            int dowValue       = rs.getDay().getValue();      // Mon=1 … Sun=7
            int delta = (dowValue - saturdayValue + 7) % 7;
            if (delta == 0) delta = 7;   // Saturday → +7 days
              LocalDate shiftDate = refSaturday.plusDays(delta);
            // Convert to java.util.Date
            Date date = Date.from(shiftDate
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant());

            // Create empty requiredRoles lists, with initial counts from template
            Map<Role, ArrayList<Employee>> reqRoles = new HashMap<>();
            Map<Role, Integer>      reqCounts = new HashMap<>();
            for (Map.Entry<Role, Integer> e : rs.getDefaultCounts().entrySet()) {
                reqCounts.put(e.getKey(), e.getValue());
                reqRoles.put(e.getKey(), new ArrayList<>(e.getValue()));
            }

            Shift s = new Shift(
                    rs.getDay() + "-" + rs.getTime() + "-" + shiftDate,
                    date,
                    rs.getTime(),
                    reqRoles,
                    reqCounts);
            nextWeek.add(s);
        }
    }

    /** Move nextWeek → currentWeek, and empty nextWeek. */
    public void swapWeeks() {
        currentWeek.clear();
        currentWeek.addAll(nextWeek);
        nextWeek.clear();
    }
}
