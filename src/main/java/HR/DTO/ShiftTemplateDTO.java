// File: ShiftTemplateDTO.java
package HR.DTO;

import java.time.DayOfWeek;
import HR.Domain.Shift.ShiftTime;
import java.util.Map;

public class ShiftTemplateDTO {
    private DayOfWeek day;
    private ShiftTime time;
    private Map<String, Integer> defaultCounts;
    // (Map<roleName, requiredCount>)

    public ShiftTemplateDTO() { }

    public ShiftTemplateDTO(DayOfWeek day, ShiftTime time, Map<String, Integer> defaultCounts) {
        this.day = day;
        this.time = time;
        this.defaultCounts = defaultCounts;
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

    public Map<String, Integer> getDefaultCounts() {
        return defaultCounts;
    }

    public void setDefaultCounts(Map<String, Integer> defaultCounts) {
        this.defaultCounts = defaultCounts;
    }
}
