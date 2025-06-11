// File: WeeklyAvailabilityDTO.java
package HR.DTO;

import java.time.DayOfWeek;
import HR.Domain.Shift.ShiftTime;  // reuse the ShiftTime enum

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
}
