package Domain;

import java.time.DayOfWeek;
import java.util.Objects;

/**
 * Represents an employee’s availability in the week,
 * e.g. Sunday Morning, Thursday Evening, etc.
 */
public class WeeklyAvailability {
    private DayOfWeek day;
    private Shift.ShiftTime time;

    public WeeklyAvailability(DayOfWeek day, Shift.ShiftTime time) {
        this.day = day;
        this.time = time;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public Shift.ShiftTime getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeeklyAvailability)) return false;
        WeeklyAvailability that = (WeeklyAvailability) o;
        return day == that.day && time == that.time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, time);
    }
}
