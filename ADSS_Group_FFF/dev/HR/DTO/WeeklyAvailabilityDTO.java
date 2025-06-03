package HR.DTO;

import java.time.DayOfWeek;
import java.util.Objects;
import HR.Domain.Shift.ShiftTime;  // reuse the ShiftTime enum

/**
 * DTO representing a single (day, time) availability slot.
 */
public class WeeklyAvailabilityDTO {
    private DayOfWeek day;
    private ShiftTime time;

    public WeeklyAvailabilityDTO() { }

    public WeeklyAvailabilityDTO(DayOfWeek day, ShiftTime time) {
        this.day = day;
        this.time = time;
    }

    public DayOfWeek getDay() {
        return day;
    }
    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public ShiftTime getTime() {
        return time;
    }
    public void setTime(ShiftTime time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeeklyAvailabilityDTO that = (WeeklyAvailabilityDTO) o;
        return day == that.day && time == that.time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, time);
    }

    @Override
    public String toString() {
        return "WeeklyAvailabilityDTO{" +
                "day=" + day +
                ", time=" + time +
                '}';
    }
}
