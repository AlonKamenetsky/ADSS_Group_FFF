package HR.DTO;

import java.util.List;
import java.util.Objects;

/**
 * DTO representing a pair of shift lists for the current week and next week.
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeeklyShiftsScheduleDTO that = (WeeklyShiftsScheduleDTO) o;
        return Objects.equals(currentWeek, that.currentWeek) &&
                Objects.equals(nextWeek, that.nextWeek);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentWeek, nextWeek);
    }

    @Override
    public String toString() {
        return "WeeklyShiftsScheduleDTO{" +
                "currentWeek=" + currentWeek +
                ", nextWeek=" + nextWeek +
                '}';
    }
}
