package Domain;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

/**
 * A template for “every Sunday morning” / “every Tuesday evening”, etc.
 */
public class ShiftTemplate {
    private DayOfWeek day;
    private Shift.ShiftTime time;
    private Map<Role, Integer> defaultCounts;

    public ShiftTemplate(DayOfWeek day, Shift.ShiftTime time) {
        this.day = day;
        this.time = time;
        this.defaultCounts = new HashMap<>();
    }

    public DayOfWeek getDay() {
        return day;
    }

    public Shift.ShiftTime getTime() {
        return time;
    }

    public Map<Role, Integer> getDefaultCounts() {
        return defaultCounts;
    }

    /** Pre‑configure how many of each role should be required by default. */
    public void setDefaultCount(Role role, int count) {
        defaultCounts.put(role, count);
    }
}
