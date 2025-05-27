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

public interface WeeklyAvailabilityDAO {
    void setAvailability(String employeeId, String date, String shiftTime);
    void clearAvailability(String employeeId);
    List<String> getAvailableEmployees(String date, String shiftTime);


}
