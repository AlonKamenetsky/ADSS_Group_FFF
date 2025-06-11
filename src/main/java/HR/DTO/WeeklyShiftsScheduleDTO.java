// File: WeeklyShiftsScheduleDTO.java
package HR.DTO;

import java.util.List;

public class WeeklyShiftsScheduleDTO {
    private List<ShiftDTO> currentWeek;
    private List<ShiftDTO> nextWeek;

    public WeeklyShiftsScheduleDTO() { }

    public WeeklyShiftsScheduleDTO(List<ShiftDTO> currentWeek, List<ShiftDTO> nextWeek) {
        this.currentWeek = currentWeek;
        this.nextWeek = nextWeek;
    }

    public List<ShiftDTO> getCurrentWeek() {
        return currentWeek;
    }

    public void setCurrentWeek(List<ShiftDTO> currentWeek) {
        this.currentWeek = currentWeek;
    }

    public List<ShiftDTO> getNextWeek() {
        return nextWeek;
    }

    public void setNextWeek(List<ShiftDTO> nextWeek) {
        this.nextWeek = nextWeek;
    }
}
