package HR.Domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeeklyShiftsSchedule {
    private List<Shift> currentWeek = new ArrayList<>();
    private List<Shift> nextWeek    = new ArrayList<>();

    public List<Shift> getCurrentWeek() { return currentWeek; }
    public List<Shift> getNextWeek()    { return nextWeek;    }

    /**
     * Build concrete shifts for the week *after* refSaturday.
     */
    public void resetNextWeek(List<ShiftTemplate> templates, LocalDate refSaturday) {
        nextWeek.clear();
        for (ShiftTemplate rs : templates) {
            // days after Saturday → 1…7
            int satVal  = DayOfWeek.SATURDAY.getValue();
            int dowVal  = rs.getDay().getValue();
            int delta   = (dowVal - satVal + 7) % 7;
            if (delta == 0) delta = 7;
            LocalDate shiftDate = refSaturday.plusDays(delta);

            Date date = Date.from(
                    shiftDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            );

            Map<Role, ArrayList<Employee>> reqRoles  = new HashMap<>();
            Map<Role, Integer>             reqCounts = new HashMap<>();
            rs.getDefaultCounts().forEach((role, cnt) -> {
                reqCounts.put(role, cnt);
                reqRoles .put(role, new ArrayList<>(cnt));
            });

            String id = rs.getDay() + "-" + rs.getTime() + "-" + shiftDate;
            nextWeek.add(new Shift(id, date, rs.getTime(), reqRoles, reqCounts));
        }
    }

    /** Move nextWeek → currentWeek and clear nextWeek. */
    public void swapWeeks() {
        currentWeek.clear();
        currentWeek.addAll(nextWeek);
        nextWeek.clear();
    }
}
